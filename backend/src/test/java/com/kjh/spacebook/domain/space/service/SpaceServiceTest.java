package com.kjh.spacebook.domain.space.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.space.dto.request.CreateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.request.UpdateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.response.SpaceResponse;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import com.kjh.spacebook.domain.space.exception.SpaceErrorCode;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock SpaceRepository spaceRepository;
    @Mock UserRepository userRepository;

    @InjectMocks SpaceService spaceService;

    @Test
    @DisplayName("공간 생성 성공")
    void createSpace_success() {
        // given
        User user = mock(User.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        CreateSpaceRequest request = new CreateSpaceRequest(
                "강남 스터디룸", "설명", "img",
                SpaceType.STUDY, 10000, "강남", 4
        );

        // when
        SpaceResponse response = spaceService.createSpace(1L, request);

        // then
        verify(spaceRepository).save(any(Space.class));
        assertThat(response.spaceName()).isEqualTo("강남 스터디룸");
    }

    @Test
    @DisplayName("공간 수정 - 존재하지 않으면 예외")
    void updateSpace_notFound() {
        // given
        given(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.empty());

        UpdateSpaceRequest request = new UpdateSpaceRequest(
                "수정", null, null, null, null, null, null, null
        );

        // when & then
        assertThatThrownBy(() -> spaceService.updateSpace(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(SpaceErrorCode.SPACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("가격 범위 잘못되면 예외")
    void getSpaces_invalidPriceRange() {
        // when & then
        assertThatThrownBy(() ->
                spaceService.getSpaces(null, null, 50000, 10000, PageRequest.of(0, 10))
        ).isInstanceOf(BusinessException.class)
         .hasMessageContaining(SpaceErrorCode.INVALID_PRICE_RANGE.getMessage());
    }

    @Test
    @DisplayName("공간 삭제 성공")
    void deleteSpace_success() {
        // given
        Space space = mock(Space.class);
        given(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(space));

        // when
        spaceService.deleteSpace(1L);

        // then
        verify(space).delete();
    }

    @Test
    @DisplayName("공간 상세 조회 - OPEN 상태만 가능")
    void getSpaceDetail_notFound() {
        // given
        given(spaceRepository
                .findByIdAndDeletedAtIsNullAndSpaceStatus(1L, SpaceStatus.OPEN))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> spaceService.getSpaceDetail(1L))
                .isInstanceOf(BusinessException.class);
    }
}
