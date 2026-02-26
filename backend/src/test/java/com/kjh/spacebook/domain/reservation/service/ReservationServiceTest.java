package com.kjh.spacebook.domain.reservation.service;

import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.reservation.dto.request.CreateReservationRequest;
import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import com.kjh.spacebook.domain.reservation.repository.ReservationRepository;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.repository.SpaceRepository;
import com.kjh.spacebook.domain.user.entity.User;
import com.kjh.spacebook.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock ReservationRepository reservationRepository;
    @Mock SpaceRepository spaceRepository;
    @Mock UserRepository userRepository;

    @InjectMocks ReservationService reservationService;

    private LocalDateTime futureHour(int plusHours) {
        return LocalDateTime.now()
                .plusHours(plusHours)
                .truncatedTo(ChronoUnit.HOURS);
    }

    @Test
    @DisplayName("정각 단위 아니면 예외")
    void createReservation_notHourly() {
        // given
        CreateReservationRequest request = new CreateReservationRequest(
                1L,
                LocalDateTime.now().plusHours(2).plusMinutes(30),
                LocalDateTime.now().plusHours(3),
                2,
                "미팅"
        );

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("종료시간이 시작시간 이전이면 예외")
    void createReservation_invalidTime() {
        // given
        LocalDateTime start = futureHour(2);
        LocalDateTime end = start.minusHours(1);

        CreateReservationRequest request =
                new CreateReservationRequest(1L, start, end, 2, "미팅");

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("인원 초과 시 예외")
    void createReservation_exceedCapacity() {
        // given
        LocalDateTime start = futureHour(2);
        LocalDateTime end = futureHour(4);

        CreateReservationRequest request =
                new CreateReservationRequest(1L, start, end, 10, "미팅");

        User user = mock(User.class);
        Space space = mock(Space.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(spaceRepository.findByIdForReservation(1L, SpaceStatus.OPEN))
                .willReturn(Optional.of(space));
        given(space.getCapacity()).willReturn(4);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("시간 충돌 시 예외")
    void createReservation_timeConflict() {
        // given
        LocalDateTime start = futureHour(2);
        LocalDateTime end = futureHour(4);

        CreateReservationRequest request =
                new CreateReservationRequest(1L, start, end, 2, "미팅");

        User user = mock(User.class);
        Space space = mock(Space.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(spaceRepository.findByIdForReservation(1L, SpaceStatus.OPEN))
                .willReturn(Optional.of(space));
        given(space.getCapacity()).willReturn(10);
        given(reservationRepository.existsOverlapping(
                any(), eq(ReservationStatus.CONFIRMED), any(), any()
        )).willReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {
        // given
        LocalDateTime start = futureHour(2);
        LocalDateTime end = futureHour(4);

        CreateReservationRequest request =
                new CreateReservationRequest(1L, start, end, 2, "미팅");

        User user = mock(User.class);
        Space space = mock(Space.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(spaceRepository.findByIdForReservation(1L, SpaceStatus.OPEN))
                .willReturn(Optional.of(space));
        given(space.getCapacity()).willReturn(10);
        given(space.getPricePerHour()).willReturn(10000);
        given(reservationRepository.existsOverlapping(
                any(), eq(ReservationStatus.CONFIRMED), any(), any()
        )).willReturn(false);

        // when
        reservationService.createReservation(1L, request);

        // then
        verify(reservationRepository).save(any(Reservation.class));
    }
}
