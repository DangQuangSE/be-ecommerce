package com.sport_pro_be.modules.size.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SizeGroupResponse {
    private Long id;
    private String name;
    private String description;
    private List<SizeOptionResponse> sizes;
}
