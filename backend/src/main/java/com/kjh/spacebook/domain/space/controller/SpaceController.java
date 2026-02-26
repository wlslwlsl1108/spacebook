package com.kjh.spacebook.domain.space.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.space.dto.request.CreateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.request.UpdateSpaceRequest;
import com.kjh.spacebook.domain.space.dto.response.SpaceListResponse;
import com.kjh.spacebook.domain.space.dto.response.SpaceResponse;
import com.kjh.spacebook.domain.space.service.SpaceService;
import com.kjh.spacebook.domain.reservation.dto.response.ReservedTimeResponse;
import com.kjh.spacebook.domain.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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

import java.time.LocalDate;
import java.util.List;

@Tag(name = "공간", description = "공간 등록, 수정, 삭제, 조회, 검색")
@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Validated
public class SpaceController {
    private final SpaceService spaceService;
    private final ReservationService reservationService;

    // 관리자

    @Operation(summary = "공간 등록", description = "관리자가 새로운 공간을 등록합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceResponse>> createSpace(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateSpaceRequest request
    ) {
        SpaceResponse response = spaceService.createSpace(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "공간 수정", description = "관리자가 기존 공간 정보를 수정합니다. null인 필드는 기존 값을 유지합니다.")
    @PatchMapping("/{spaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceResponse>> updateSpace(
            @PathVariable("spaceId") Long spaceId,
            @Valid @RequestBody UpdateSpaceRequest request
    ) {
        SpaceResponse response = spaceService.updateSpace(spaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "공간 삭제", description = "관리자가 공간을 삭제합니다. (Soft Delete)")
    @DeleteMapping("/{spaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(@PathVariable("spaceId") Long spaceId) {
        spaceService.deleteSpace(spaceId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    // 인증

    @Operation(summary = "내 공간 목록 조회", description = "관리자가 자신이 등록한 공간 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<SpaceListResponse>>> getMySpaces(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<SpaceListResponse> responses = spaceService.getMySpaces(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    // 공개

    @Operation(summary = "공간 목록 조회", description = "공간 목록을 조회합니다. 위치, 유형, 가격 범위로 필터링하고 정렬할 수 있습니다.",
            security = {})
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SpaceListResponse>>> getSpaces(
            @Parameter(description = "위치 검색어", example = "강남")
            @RequestParam(name = "location", required = false) String location,
            @Parameter(description = "공간 유형")
            @RequestParam(name = "spaceType", required = false) SpaceType spaceType,
            @Parameter(description = "최소 가격", example = "10000")
            @RequestParam(name = "minPrice", required = false) @Min(0) Integer minPrice,
            @Parameter(description = "최대 가격", example = "50000")
            @RequestParam(name = "maxPrice", required = false) @Min(0) Integer maxPrice,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SpaceListResponse> responses = spaceService.getSpaces(location, spaceType, minPrice, maxPrice, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    @Operation(summary = "공간 상세 조회", description = "공간의 상세 정보를 조회합니다.",
            security = {})
    @GetMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<SpaceResponse>> getSpaceDetail(@PathVariable("spaceId") Long spaceId) {
        SpaceResponse response = spaceService.getSpaceDetail(spaceId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "예약된 시간대 조회", description = "특정 날짜의 예약된 시간대 목록을 조회합니다.",
            security = {})
    @GetMapping("/{spaceId}/reserved-times")
    public ResponseEntity<ApiResponse<List<ReservedTimeResponse>>> getReservedTimes(
            @PathVariable("spaceId") Long spaceId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2026-03-01")
            @RequestParam("date") LocalDate date
    ) {
        List<ReservedTimeResponse> responses = reservationService.getReservedTimes(spaceId, date);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }
}
