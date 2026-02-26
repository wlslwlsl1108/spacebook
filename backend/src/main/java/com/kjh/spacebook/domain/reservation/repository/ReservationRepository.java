package com.kjh.spacebook.domain.reservation.repository;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByUser(User user, Pageable pageable);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.space.id = :spaceId " +
            "AND r.status = :status " +
            "AND r.startTime < :dateEnd " +
            "AND r.endTime > :dateStart")
    List<Reservation> findReservedTimes(
            @Param("spaceId") Long spaceId,
            @Param("status") ReservationStatus status,
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateEnd") LocalDateTime dateEnd
    );

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.space = :space " +
            "AND r.status = :status " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime")
    boolean existsOverlapping(
            @Param("space") Space space,
            @Param("status") ReservationStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    boolean existsByUserAndStatus(User user, ReservationStatus status);
}
