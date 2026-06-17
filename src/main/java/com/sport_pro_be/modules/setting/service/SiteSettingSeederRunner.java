package com.sport_pro_be.modules.setting.service;

import com.sport_pro_be.modules.setting.constant.SiteSettingConstant;
import com.sport_pro_be.modules.setting.domain.SiteSetting;
import com.sport_pro_be.modules.setting.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SiteSettingSeederRunner implements CommandLineRunner {

    private final SiteSettingRepository siteSettingRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (siteSettingRepository.existsById(SiteSettingConstant.SETTINGS_ID)) {
            return;
        }
        log.info("Seeding default site settings...");
        SiteSetting settings = new SiteSetting();
        settings.setId(SiteSettingConstant.SETTINGS_ID);
        settings.setReturnPolicy(SiteSettingConstant.DEFAULT_RETURN_POLICY);
        siteSettingRepository.save(settings);
        log.info("Successfully seeded default site settings.");
    }
}
