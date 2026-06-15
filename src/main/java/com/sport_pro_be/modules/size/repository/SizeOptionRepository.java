package com.sport_pro_be.modules.size.repository;

import com.sport_pro_be.modules.size.domain.SizeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeOptionRepository extends JpaRepository<SizeOption, Long> {
}
