package com.kjh.spacebook.domain.reservation.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.reservation.dto.request.CreateReservationRequest;
import com.kjh.spacebook.domain.reservation.dto.response.ReservationListResponse;
import com.kjh.spacebook.domain.reservation.dto.response.ReservationResponse;
import com.kjh.spacebook.domain.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "예약", description = "예약 생성, 내 예약 조회, 예약 취소")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "공간을 예약합니다. 예약 시간 중복 검증이 수행됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationResponse response = reservationService.createReservation(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "내 예약 목록 조회", description = "로그인한 사용자의 예약 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<ReservationListResponse>>> getMyReservations(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ReservationListResponse> responses = reservationService.getMyReservations(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    @Operation(summary = "예약 상세 조회", description = "예약의 상세 정보를 조회합니다. 본인의 예약만 조회 가능합니다.")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getMyReservationDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @PathVariable("reservationId") Long reservationId
    ) {
        ReservationResponse response = reservationService.getMyReservationDetail(userId, reservationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "예약 취소", description = "예약을 취소합니다. 본인의 예약만 취소 가능합니다.")
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @PathVariable("reservationId") Long reservationId
    ) {
        reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "예약이 취소되었습니다."));
    }
}
