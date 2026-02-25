package com.kjh.spacebook.domain.recommendation.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.recommendation.dto.request.RecommendRequest;
import com.kjh.spacebook.domain.recommendation.service.RecommendationService;
import com.kjh.spacebook.domain.space.dto.response.SpaceListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<SpaceListResponse>>> recommend(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody RecommendRequest request
    ) {
        List<SpaceListResponse> responses = recommendationService.recommend(request.query());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }
}
