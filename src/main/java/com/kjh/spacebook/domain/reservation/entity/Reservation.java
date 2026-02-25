package com.kjh.spacebook.domain.reservation.entity;

import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "people_count", nullable = false)
    private int peopleCount;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(length = 255)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private Reservation(
            User user,
            Space space,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int peopleCount,
            int totalPrice,
            String purpose
    ) {
        this.user = user;
        this.space = space;
        this.startTime = startTime;
        this.endTime = endTime;
        this.peopleCount = peopleCount;
        this.totalPrice = totalPrice;
        this.purpose = purpose;
        this.status = ReservationStatus.CONFIRMED;
    }

    public static Reservation of(
            User user,
            Space space,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int peopleCount,
            int totalPrice,
            String purpose
    ) {
        return new Reservation(
                user, space, startTime, endTime,
                peopleCount, totalPrice, purpose
        );
    }
}
