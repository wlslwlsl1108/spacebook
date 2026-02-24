package com.kjh.spacebook.domain.space.repository;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Page<Space> findAllByDeletedAtIsNullAndSpaceStatus(Pageable pageable, SpaceStatus spaceStatus);
}
