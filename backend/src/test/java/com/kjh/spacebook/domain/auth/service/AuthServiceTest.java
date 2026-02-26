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
import com.kjh.spacebook.domain.user.enums.Role;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;

    @InjectMocks AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequest request =
                new SignupRequest("홍길동", "test@test.com", "test1234!!", "010-1234-5678");

        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encoded");

        given(jwtUtil.createAccessToken(any(), any())).willReturn("access");
        given(jwtUtil.createRefreshToken(any())).willReturn("refresh");
        given(jwtUtil.calculateRefreshExpiry()).willReturn(LocalDateTime.now().plusDays(7));

        // when
        TokenResponse response = authService.signup(request);

        // then
        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복 예외")
    void signup_duplicateEmail() {
        // given
        SignupRequest request =
                new SignupRequest("홍길동", "test@test.com", "test1234!!", "010-1234-5678");

        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UserErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 이메일 예외")
    void login_userNotFound() {
        // given
        LoginRequest request = new LoginRequest("none@test.com", "1234");

        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(AuthErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 - 확정 예약 존재 시 예외")
    void deleteAccount_hasConfirmedReservation() {
        // given
        User user = mock(User.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reservationRepository.existsByUserAndStatus(user, ReservationStatus.CONFIRMED))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.deleteAccount(1L, "password"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(AuthErrorCode.HAS_CONFIRMED_RESERVATION.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_success() {
        // given
        Claims claims = mock(Claims.class);
        given(claims.getSubject()).willReturn("1");

        User user = mock(User.class);
        given(user.isDeleted()).willReturn(false);
        given(user.getId()).willReturn(1L);
        given(user.getRole()).willReturn(Role.USER);

        RefreshToken stored = mock(RefreshToken.class);

        given(jwtUtil.validateAndGetClaims("refresh")).willReturn(claims);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findByUser(user)).willReturn(Optional.of(stored));
        given(stored.getToken()).willReturn("refresh");
        given(stored.isExpired()).willReturn(false);

        given(jwtUtil.createAccessToken(any(), any())).willReturn("newAccess");
        given(jwtUtil.createRefreshToken(any())).willReturn("newRefresh");
        given(jwtUtil.calculateRefreshExpiry()).willReturn(LocalDateTime.now().plusDays(7));

        // when
        TokenResponse response = authService.reissue(new TokenReissueRequest("refresh"));

        // then
        assertThat(response.accessToken()).isEqualTo("newAccess");
        assertThat(response.refreshToken()).isEqualTo("newRefresh");
    }
}
