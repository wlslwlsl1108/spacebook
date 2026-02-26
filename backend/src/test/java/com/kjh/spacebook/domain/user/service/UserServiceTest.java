package com.kjh.spacebook.domain.user.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.user.dto.request.UpdateUserRequest;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_success() {
        // given
        User user = mock(User.class);
        given(userRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(user));

        // when
        userService.getMyInfo(1L);

        // then
        verify(userRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("사용자 없음 예외")
    void getMyInfo_userNotFound() {
        // given
        given(userRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMyInfo(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("전화번호만 변경 성공")
    void updateMyInfo_phoneOnly() {
        // given
        User user = mock(User.class);
        given(userRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(user));

        UpdateUserRequest request = new UpdateUserRequest("010-9999-9999", null, null);

        // when
        userService.updateMyInfo(1L, request);

        // then
        verify(user).updatePhoneNumber("010-9999-9999");
    }

    @Test
    @DisplayName("비밀번호 변경 - 하나만 입력 시 예외")
    void updateMyInfo_passwordIncomplete() {
        // given
        User user = mock(User.class);
        given(userRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(user));

        UpdateUserRequest request = new UpdateUserRequest(null, "current", null);

        // when & then
        assertThatThrownBy(() -> userService.updateMyInfo(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UserErrorCode.PASSWORD_CHANGE_INCOMPLETE.getMessage());
    }

    @Test
    @DisplayName("새 비밀번호가 기존과 동일하면 예외")
    void updateMyInfo_samePassword() {
        // given
        User user = mock(User.class);
        given(userRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(user));

        willDoNothing().given(user).validatePassword("samePass", passwordEncoder);

        UpdateUserRequest request = new UpdateUserRequest(null, "samePass", "samePass");

        // when & then
        assertThatThrownBy(() -> userService.updateMyInfo(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UserErrorCode.SAME_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updateMyInfo_changePasswordSuccess() {
        // given
        User user = mock(User.class);
        given(userRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(user));
        given(passwordEncoder.encode("newPass1234!!")).willReturn("encoded");

        willDoNothing().given(user).validatePassword("oldPass", passwordEncoder);

        UpdateUserRequest request = new UpdateUserRequest(null, "oldPass", "newPass1234!!");

        // when
        userService.updateMyInfo(1L, request);

        // then
        verify(user).changePassword("encoded");
    }
}
