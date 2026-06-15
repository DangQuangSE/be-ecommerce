package com.sport_pro_be.modules.size.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SizeOptionRequest {

    @NotBlank(message = "Size option name cannot be blank")
    @Size(max = 50, message = "Size option name must not exceed 50 characters")
    private String name;

    private Integer displayOrder = 0;
}
