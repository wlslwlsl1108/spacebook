package com.kjh.spacebook.domain.reservation.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.reservation.dto.request.CreateReservationRequest;
import com.kjh.spacebook.domain.reservation.dto.response.ReservationListResponse;
import com.kjh.spacebook.domain.reservation.dto.response.ReservationResponse;
import com.kjh.spacebook.domain.reservation.service.ReservationService;
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

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationResponse response = reservationService.createReservation(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<ReservationListResponse>>> getMyReservations(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ReservationListResponse> responses = reservationService.getMyReservations(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getMyReservationDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable("reservationId") Long reservationId
    ) {
        ReservationResponse response = reservationService.getMyReservationDetail(userId, reservationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @AuthenticationPrincipal Long userId,
            @PathVariable("reservationId") Long reservationId
    ) {
        reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "예약이 취소되었습니다."));
    }
}
