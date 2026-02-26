package com.kjh.spacebook.domain.user.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.user.dto.request.UpdateUserRequest;
import com.kjh.spacebook.domain.user.dto.response.UserResponse;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateMyInfo(Long userId, UpdateUserRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (request.phoneNumber() != null) {
            user.updatePhoneNumber(request.phoneNumber());
        }

        boolean hasCurrentPassword = request.currentPassword() != null;
        boolean hasNewPassword = request.newPassword() != null;

        if (hasCurrentPassword != hasNewPassword) {
            throw new BusinessException(UserErrorCode.PASSWORD_CHANGE_INCOMPLETE);
        }

        if (hasCurrentPassword && hasNewPassword) {
            user.validatePassword(request.currentPassword(), passwordEncoder);
            if (request.currentPassword().equals(request.newPassword())) {
                throw new BusinessException(UserErrorCode.SAME_PASSWORD);
            }
            user.changePassword(passwordEncoder.encode(request.newPassword()));
        }

        return UserResponse.from(user);
    }

}
