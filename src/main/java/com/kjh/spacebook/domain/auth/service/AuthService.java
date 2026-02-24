package com.kjh.spacebook.domain.auth.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.common.security.jwt.JwtUtil;
import com.kjh.spacebook.domain.auth.dto.request.LoginRequest;
import com.kjh.spacebook.domain.auth.dto.request.SignupRequest;
import com.kjh.spacebook.domain.auth.dto.response.TokenResponse;
import com.kjh.spacebook.domain.auth.entity.RefreshToken;
import com.kjh.spacebook.domain.auth.exception.AuthErrorCode;
import com.kjh.spacebook.domain.auth.repository.RefreshTokenRepository;
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
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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

        user.validatePassword(request.password(), passwordEncoder);

        return createAndSaveTokens(user);
    }

    private TokenResponse createAndSaveTokens(User user) {
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(RefreshToken.of(
                user,
                refreshToken,
                LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS)
        ));

        return new TokenResponse(accessToken, refreshToken);
    }
}
