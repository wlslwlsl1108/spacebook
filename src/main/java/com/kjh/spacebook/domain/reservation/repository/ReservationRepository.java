package com.kjh.spacebook.domain.reservation.repository;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
