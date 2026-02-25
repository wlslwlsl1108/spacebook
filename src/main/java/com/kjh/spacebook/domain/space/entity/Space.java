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
    private String spaceName;

    @Column(columnDefinition = "TEXT", nullable = false)
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
            String spaceName,
            String description,
            String imageUrl,
            SpaceType spaceType,
            int pricePerHour,
            String location,
            int capacity,
            User owner
    ) {
        this.spaceName = spaceName;
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
            String spaceName,
            String description,
            String imageUrl,
            SpaceType spaceType,
            int pricePerHour,
            String location,
            int capacity,
            User owner
    ) {
        return new Space(
                spaceName,
                description,
                imageUrl,
                spaceType,
                pricePerHour,
                location,
                capacity,
                owner
        );
    }

    public void update(
            String spaceName,
            String description,
            String imageUrl,
            SpaceType spaceType,
            Integer pricePerHour,
            String location,
            Integer capacity,
            SpaceStatus spaceStatus
    ) {
        if (spaceName != null) this.spaceName = spaceName;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (spaceType != null) this.spaceType = spaceType;
        if (pricePerHour != null) this.pricePerHour = pricePerHour;
        if (location != null) this.location = location;
        if (capacity != null) this.capacity = capacity;
        if (spaceStatus != null) this.spaceStatus = spaceStatus;
    }
}
