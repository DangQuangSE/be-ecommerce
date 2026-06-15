package com.sport_pro_be.modules.size.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SizeOptionResponse {
    private Long id;
    private String name;
    private Integer displayOrder;
}
