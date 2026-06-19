package com.sport_pro_be.modules.setting.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SiteSettingResponse {
    private String returnPolicy;
    private LocalDateTime updatedAt;
}
