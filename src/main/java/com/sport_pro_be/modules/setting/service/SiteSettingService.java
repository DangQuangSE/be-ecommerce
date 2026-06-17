package com.sport_pro_be.modules.setting.service;

import com.sport_pro_be.modules.setting.constant.SiteSettingConstant;
import com.sport_pro_be.modules.setting.domain.SiteSetting;
import com.sport_pro_be.modules.setting.dto.SiteSettingRequest;
import com.sport_pro_be.modules.setting.dto.SiteSettingResponse;
import com.sport_pro_be.modules.setting.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SiteSettingService implements ISiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    @Override
    @Transactional
    public SiteSettingResponse getSettings() {
        return mapEntityToResponse(getOrCreateSettings());
    }

    @Override
    @Transactional
    public SiteSettingResponse updateSettings(SiteSettingRequest request) {
        SiteSetting settings = getOrCreateSettings();
        settings.setReturnPolicy(request.getReturnPolicy());
        return mapEntityToResponse(siteSettingRepository.save(settings));
    }

    private SiteSetting getOrCreateSettings() {
        return siteSettingRepository.findById(SiteSettingConstant.SETTINGS_ID)
                .orElseGet(() -> {
                    SiteSetting settings = new SiteSetting();
                    settings.setId(SiteSettingConstant.SETTINGS_ID);
                    settings.setReturnPolicy(SiteSettingConstant.DEFAULT_RETURN_POLICY);
                    return siteSettingRepository.save(settings);
                });
    }

    private SiteSettingResponse mapEntityToResponse(SiteSetting settings) {
        return SiteSettingResponse.builder()
                .returnPolicy(settings.getReturnPolicy())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
