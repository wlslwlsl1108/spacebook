package com.kjh.spacebook.domain.space.repository;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Page<Space> findAllByDeletedAtIsNullAndSpaceStatus(SpaceStatus spaceStatus, Pageable pageable);

    Optional<Space> findByIdAndDeletedAtIsNull(Long id);

    Optional<Space> findByIdAndDeletedAtIsNullAndSpaceStatus(Long id, SpaceStatus spaceStatus);
}
