package com.sport_pro_be.modules.setting.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.setting.constant.SiteSettingConstant;
import com.sport_pro_be.modules.setting.dto.SiteSettingRequest;
import com.sport_pro_be.modules.setting.dto.SiteSettingResponse;
import com.sport_pro_be.modules.setting.service.ISiteSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSiteSettingController {

    private final ISiteSettingService siteSettingService;

    @GetMapping
    public ApiResponse<SiteSettingResponse> getSettings() {
        return ApiResponse.of(SiteSettingConstant.GET_SETTINGS_SUCCESS, siteSettingService.getSettings());
    }

    @PutMapping
    public ApiResponse<SiteSettingResponse> updateSettings(@Valid @RequestBody SiteSettingRequest request) {
        return ApiResponse.of(SiteSettingConstant.UPDATE_SETTINGS_SUCCESS, siteSettingService.updateSettings(request));
    }
}
