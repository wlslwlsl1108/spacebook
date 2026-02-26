package com.kjh.spacebook.domain.recommendation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RecommendRequest(
        @Schema(description = "AI 추천 검색어", example = "강남에서 4명이 회의할 수 있는 조용한 공간")
        @NotBlank(message = "검색어를 입력해주세요.")
        String query
) {}
