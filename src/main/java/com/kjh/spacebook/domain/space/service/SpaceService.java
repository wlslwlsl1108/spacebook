package com.kjh.spacebook.domain.space.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.space.dto.request.CreateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.response.SpaceResponse;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public SpaceResponse createSpace(Long userId, CreateSpaceRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Space space = Space.of(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.spaceType(),
                request.pricePerHour(),
                request.location(),
                request.capacity(),
                owner
        );

        spaceRepository.save(space);

        return SpaceResponse.from(space);
    }
}
