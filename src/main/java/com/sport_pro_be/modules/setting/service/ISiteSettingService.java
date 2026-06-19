package com.sport_pro_be.modules.setting.service;

import com.sport_pro_be.modules.setting.dto.SiteSettingRequest;
import com.sport_pro_be.modules.setting.dto.SiteSettingResponse;

public interface ISiteSettingService {
    SiteSettingResponse getSettings();
    SiteSettingResponse updateSettings(SiteSettingRequest request);
}
