package com.sport_pro_be.modules.setting.dto;

import com.sport_pro_be.modules.setting.constant.SiteSettingConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SiteSettingRequest {
    @NotBlank(message = SiteSettingConstant.RETURN_POLICY_CANNOT_BE_BLANK)
    private String returnPolicy;
}
