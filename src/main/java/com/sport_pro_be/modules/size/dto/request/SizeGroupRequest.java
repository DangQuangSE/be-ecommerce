package com.sport_pro_be.modules.size.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SizeGroupRequest {

    @NotBlank(message = "Size group name cannot be blank")
    @Size(min = 2, max = 100, message = "Size group name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Valid
    private List<SizeOptionRequest> sizes = new ArrayList<>();
}
