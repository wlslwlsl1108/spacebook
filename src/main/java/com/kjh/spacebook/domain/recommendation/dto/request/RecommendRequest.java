package com.kjh.spacebook.domain.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RecommendRequest(
        @NotBlank(message = "검색어를 입력해주세요.")
        String query
) {}
