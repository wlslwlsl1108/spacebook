package com.kjh.spacebook.domain.space.repository;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import com.kjh.spacebook.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Page<Space> findAllByDeletedAtIsNullAndSpaceStatus(SpaceStatus spaceStatus, Pageable pageable);

    Optional<Space> findByIdAndDeletedAtIsNull(Long id);

    Optional<Space> findByIdAndDeletedAtIsNullAndSpaceStatus(Long id, SpaceStatus spaceStatus);

    Page<Space> findAllByOwnerAndDeletedAtIsNull(User owner, Pageable pageable);

    @Query("""
            SELECT s FROM Space s
            WHERE s.deletedAt IS NULL
            AND s.spaceStatus = com.kjh.spacebook.domain.space.enums.SpaceStatus.OPEN
            AND (:location IS NULL OR s.location LIKE CONCAT('%', :location, '%'))
            AND (:capacity IS NULL OR s.capacity >= :capacity)
            AND (:spaceType IS NULL OR s.spaceType = :spaceType)
            """)
    List<Space> searchByConditions(
            @Param("location") String location,
            @Param("capacity") Integer capacity,
            @Param("spaceType") SpaceType spaceType
    );

    @Query("""
            SELECT s FROM Space s
            WHERE s.deletedAt IS NULL
            AND s.spaceStatus = com.kjh.spacebook.domain.space.enums.SpaceStatus.OPEN
            AND (:location IS NULL OR s.location LIKE CONCAT('%', :location, '%'))
            AND (:spaceType IS NULL OR s.spaceType = :spaceType)
            AND (:minPrice IS NULL OR s.pricePerHour >= :minPrice)
            AND (:maxPrice IS NULL OR s.pricePerHour <= :maxPrice)
            """)
    Page<Space> searchSpaces(
            @Param("location") String location,
            @Param("spaceType") SpaceType spaceType,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );
}
