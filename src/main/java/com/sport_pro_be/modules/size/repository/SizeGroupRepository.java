package com.sport_pro_be.modules.size.repository;

import com.sport_pro_be.modules.size.domain.SizeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeGroupRepository extends JpaRepository<SizeGroup, Long> {
    boolean existsByName(String name);
}
