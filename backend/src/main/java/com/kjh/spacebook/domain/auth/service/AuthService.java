package com.kjh.spacebook.domain.auth.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.common.security.jwt.JwtUtil;
import com.kjh.spacebook.domain.auth.dto.request.LoginRequest;
import com.kjh.spacebook.domain.auth.dto.request.SignupRequest;
import com.kjh.spacebook.domain.auth.dto.request.TokenReissueRequest;
import com.kjh.spacebook.domain.auth.dto.response.TokenResponse;
import com.kjh.spacebook.domain.auth.entity.RefreshToken;
import com.kjh.spacebook.domain.auth.exception.AuthErrorCode;
import com.kjh.spacebook.domain.auth.repository.RefreshTokenRepository;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import com.kjh.spacebook.domain.reservation.repository.ReservationRepository;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.of(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.phoneNumber()
        );

        userRepository.save(user);

        return createAndSaveTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        if (user.isDeleted()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_DELETED);
        }

        user.validatePassword(request.password(), passwordEncoder);

        return createAndSaveTokens(user);
    }

    @Transactional
    public void deleteAccount(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.validatePassword(rawPassword, passwordEncoder);

        if (reservationRepository.existsByUserAndStatus(user, ReservationStatus.CONFIRMED)) {
            throw new BusinessException(AuthErrorCode.HAS_CONFIRMED_RESERVATION);
        }

        refreshTokenRepository.deleteByUser(user);
        user.withdraw();
    }

    @Transactional
    public TokenResponse reissue(TokenReissueRequest request) {
        // 1. 리프레시 토큰 검증
        io.jsonwebtoken.Claims claims;
        try {
            claims = jwtUtil.validateAndGetClaims(request.refreshToken());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new BusinessException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(claims.getSubject());

        // 2. 유저 및 탈퇴 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_DELETED);
        }

        // 3. DB에 저장된 토큰과 일치하는지 확인
        RefreshToken stored = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.REVOKED_REFRESH_TOKEN));

        if (!stored.getToken().equals(request.refreshToken())) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (stored.isExpired()) {
            throw new BusinessException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 4. 새 토큰 발급
        return createAndSaveTokens(user);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUser(user);
    }

    private TokenResponse createAndSaveTokens(User user) {
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(RefreshToken.of(
                user,
                refreshToken,
                jwtUtil.calculateRefreshExpiry()
        ));

        return new TokenResponse(accessToken, refreshToken);
    }
}
