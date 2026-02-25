package com.kjh.spacebook.domain.space.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.space.dto.request.CreateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.request.UpdateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.response.SpaceListResponse;
import com.kjh.spacebook.domain.space.dto.response.SpaceResponse;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import com.kjh.spacebook.domain.space.exception.SpaceErrorCode;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                request.spaceName(),
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

    @Transactional
    public SpaceResponse updateSpace(Long spaceId, UpdateSpaceRequest request) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new BusinessException(SpaceErrorCode.SPACE_NOT_FOUND));

        space.update(
                request.spaceName(),
                request.description(),
                request.imageUrl(),
                request.spaceType(),
                request.pricePerHour(),
                request.location(),
                request.capacity(),
                request.spaceStatus()
        );

        return SpaceResponse.from(space);
    }

    @Transactional
    public void deleteSpace(Long spaceId) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new BusinessException(SpaceErrorCode.SPACE_NOT_FOUND));

        space.delete();
    }

    public Page<SpaceListResponse> getMySpaces(Long userId, Pageable pageable) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return spaceRepository.findAllByOwnerAndDeletedAtIsNull(owner, pageable)
                .map(SpaceListResponse::from);
    }

    public Page<SpaceListResponse> getSpaces(
            String location,
            SpaceType spaceType,
            Integer minPrice,
            Integer maxPrice,
            Pageable pageable
    ) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new BusinessException(SpaceErrorCode.INVALID_PRICE_RANGE);
        }

        return spaceRepository.searchSpaces(location, spaceType, minPrice, maxPrice, pageable)
                .map(SpaceListResponse::from);
    }

    public SpaceResponse getSpaceDetail(Long spaceId) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNullAndSpaceStatus(spaceId, SpaceStatus.OPEN)
                .orElseThrow(() -> new BusinessException(SpaceErrorCode.SPACE_NOT_FOUND));

        return SpaceResponse.from(space);
    }
}
