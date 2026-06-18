package com.sport_pro_be.modules.setting.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.setting.constant.SiteSettingConstant;
import com.sport_pro_be.modules.setting.dto.SiteSettingResponse;
import com.sport_pro_be.modules.setting.service.ISiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SiteSettingController {

    private final ISiteSettingService siteSettingService;

    @GetMapping
    public ApiResponse<SiteSettingResponse> getSettings() {
        return ApiResponse.of(SiteSettingConstant.GET_SETTINGS_SUCCESS, siteSettingService.getSettings());
    }
}
