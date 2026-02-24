package com.kjh.spacebook.domain.space.repository;

import com.kjh.spacebook.domain.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
}
