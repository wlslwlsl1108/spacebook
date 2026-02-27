package com.kjh.spacebook.domain.reservation.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.common.service.EmailService;
import com.kjh.spacebook.domain.reservation.dto.request.CreateReservationRequest;
import com.kjh.spacebook.domain.reservation.dto.response.ReservationListResponse;
import com.kjh.spacebook.domain.reservation.dto.response.ReservationResponse;
import com.kjh.spacebook.domain.reservation.dto.response.ReservedTimeResponse;
import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import com.kjh.spacebook.domain.reservation.exception.ReservationErrorCode;
import com.kjh.spacebook.domain.reservation.repository.ReservationRepository;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.exception.SpaceErrorCode;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.exception.UserErrorCode;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public ReservationResponse createReservation(
            Long userId,
            CreateReservationRequest request
    ) {
        if (!request.startTime().truncatedTo(ChronoUnit.HOURS).equals(request.startTime())
                || !request.endTime().truncatedTo(ChronoUnit.HOURS).equals(request.endTime())) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_NOT_HOURLY);
        }

        if (!request.endTime().isAfter(request.startTime())) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_INVALID_TIME);
        }

        if (request.startTime().isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_PAST_TIME);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Space space = spaceRepository.findByIdForReservation(request.spaceId(), SpaceStatus.OPEN)
                .orElseThrow(() -> new BusinessException(SpaceErrorCode.SPACE_NOT_FOUND));

        if (request.peopleCount() > space.getCapacity()) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_EXCEED_CAPACITY);
        }

        boolean hasConflict = reservationRepository.existsOverlapping(
                space,
                ReservationStatus.CONFIRMED,
                request.startTime(),
                request.endTime()
        );

        if (hasConflict) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_TIME_CONFLICT);
        }

        long hours = Duration.between(request.startTime(), request.endTime()).toHours();
        int totalPrice = (int) hours * space.getPricePerHour();

        Reservation reservation = Reservation.of(
                user,
                space,
                request.startTime(),
                request.endTime(),
                request.peopleCount(),
                totalPrice,
                request.purpose()
        );

        reservationRepository.save(reservation);
        emailService.sendReservationConfirm(user.getEmail(), reservation);

        return ReservationResponse.from(reservation);
    }

    // 공개

    public List<ReservedTimeResponse> getReservedTimes(Long spaceId, LocalDate date) {
        LocalDateTime dateStart = date.atStartOfDay();
        LocalDateTime dateEnd = date.plusDays(1).atStartOfDay();

        return reservationRepository.findReservedTimes(
                spaceId,
                ReservationStatus.CONFIRMED,
                dateStart,
                dateEnd
        ).stream().map(ReservedTimeResponse::from).toList();
    }

    // 인증

    public Page<ReservationListResponse> getMyReservations(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return reservationRepository.findAllByUser(user, pageable)
                .map(ReservationListResponse::from);
    }

    public ReservationResponse getMyReservationDetail(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_NOT_OWNER);
        }

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_NOT_OWNER);
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_ALREADY_CANCELLED);
        }

        LocalDateTime deadline = reservation.getStartTime().minusDays(1);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new BusinessException(ReservationErrorCode.RESERVATION_CANCEL_TOO_LATE);
        }

        reservation.cancel();
        emailService.sendReservationCancel(reservation.getUser().getEmail(), reservation);
    }
}
