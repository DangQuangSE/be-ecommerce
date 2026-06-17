package com.sport_pro_be.modules.setting.repository;

import com.sport_pro_be.modules.setting.domain.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
}
