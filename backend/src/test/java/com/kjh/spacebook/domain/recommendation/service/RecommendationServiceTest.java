package com.kjh.spacebook.domain.recommendation.service;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private GroqService groqService;

    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    @DisplayName("AI 조건 추출 후 공간 검색 성공")
    void recommend_success() {
        // given
        GroqService.SearchCondition condition =
                new GroqService.SearchCondition("강남", 4, SpaceType.MEETING);

        given(groqService.extractCondition("강남에서 회의"))
                .willReturn(condition);
        given(spaceRepository.searchByConditions("강남", 4, SpaceType.MEETING))
                .willReturn(List.of(mock(Space.class)));

        // when
        var result = recommendationService.recommend("강남에서 회의");

        // then
        assertThat(result).hasSize(1);
        verify(spaceRepository).searchByConditions("강남", 4, SpaceType.MEETING);
    }

    @Test
    @DisplayName("GroqService 예외 발생 시 그대로 전파")
    void recommend_groqException() {
        // given
        given(groqService.extractCondition(any()))
                .willThrow(new RuntimeException("AI 실패"));

        // when & then
        assertThatThrownBy(() -> recommendationService.recommend("테스트"))
                .isInstanceOf(RuntimeException.class);
    }
}
