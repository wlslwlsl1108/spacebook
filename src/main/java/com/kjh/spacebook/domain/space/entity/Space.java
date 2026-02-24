package com.kjh.spacebook.domain.space.entity;

import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import com.kjh.spacebook.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "spaces")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "space_name", nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "space_type", nullable = false)
    private SpaceType spaceType;

    @Column(name = "price_per_hour", nullable = false)
    private int pricePerHour;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "space_status", nullable = false)
    private SpaceStatus spaceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private Space(
            String name,
            String description,
            String imageUrl,
            SpaceType spaceType,
            int pricePerHour,
            String location,
            int capacity,
            User owner
    ) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.spaceType = spaceType;
        this.pricePerHour = pricePerHour;
        this.location = location;
        this.capacity = capacity;
        this.spaceStatus = SpaceStatus.OPEN;
        this.owner = owner;
    }

    public static Space of(
            String name,
            String description,
            String imageUrl,
            SpaceType spaceType,
            int pricePerHour,
            String location,
            int capacity,
            User owner
    ) {
        return new Space(
                name,
                description,
                imageUrl,
                spaceType,
                pricePerHour,
                location,
                capacity,
                owner
        );
    }

}
