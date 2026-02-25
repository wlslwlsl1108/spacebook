package com.kjh.spacebook.domain.recommendation.service;

import com.kjh.spacebook.domain.space.dto.response.SpaceListResponse;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final GroqService groqService;
    private final SpaceRepository spaceRepository;

    public List<SpaceListResponse> recommend(String query) {
        GroqService.SearchCondition condition = groqService.extractCondition(query);

        List<Space> spaces = spaceRepository.searchByConditions(
                condition.location(),
                condition.capacity(),
                condition.spaceType()
        );

        return spaces.stream()
                .map(SpaceListResponse::from)
                .toList();
    }
}
