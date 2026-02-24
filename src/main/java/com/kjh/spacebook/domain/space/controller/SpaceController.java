package com.kjh.spacebook.domain.space.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.space.dto.request.CreateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.response.SpaceListResponse;
import com.kjh.spacebook.domain.space.dto.response.SpaceResponse;
import com.kjh.spacebook.domain.space.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceResponse>> createSpace(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateSpaceRequest request
    ) {
        SpaceResponse response = spaceService.createSpace(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SpaceListResponse>>> getSpaces(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<SpaceListResponse> responses = spaceService.getSpaces(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<SpaceResponse>> getSpace(@PathVariable Long spaceId) {
        SpaceResponse response = spaceService.getSpace(spaceId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
