package com.kjh.spacebook.domain.space.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.space.dto.request.CreateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.request.UpdateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.response.SpaceListResponse;
import com.kjh.spacebook.domain.space.dto.response.SpaceResponse;
import com.kjh.spacebook.domain.space.service.SpaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Validated
public class SpaceController {
    private final SpaceService spaceService;

    // 관리자

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceResponse>> createSpace(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateSpaceRequest request
    ) {
        SpaceResponse response = spaceService.createSpace(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PatchMapping("/{spaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceResponse>> updateSpace(
            @PathVariable Long spaceId,
            @Valid @RequestBody UpdateSpaceRequest request
    ) {
        SpaceResponse response = spaceService.updateSpace(spaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping("/{spaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(@PathVariable Long spaceId) {
        spaceService.deleteSpace(spaceId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    // 인증

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<SpaceListResponse>>> getMySpaces(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<SpaceListResponse> responses = spaceService.getMySpaces(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    // 공개

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SpaceListResponse>>> getSpaces(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) SpaceType spaceType,
            @RequestParam(required = false) @Min(0) Integer minPrice,
            @RequestParam(required = false) @Min(0) Integer maxPrice,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SpaceListResponse> responses = spaceService.getSpaces(location, spaceType, minPrice, maxPrice, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<SpaceResponse>> getSpaceDetail(@PathVariable Long spaceId) {
        SpaceResponse response = spaceService.getSpaceDetail(spaceId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
