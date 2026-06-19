package com.sport_pro_be.modules.user_address.dto.request;

import com.sport_pro_be.modules.user_address.constant.AddressMessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = AddressMessageConstant.FULL_NAME_REQUIRED)
    private String fullName;

    @NotBlank(message = AddressMessageConstant.PHONE_NUMBER_REQUIRED)
    private String phoneNumber;

    @NotBlank(message = AddressMessageConstant.ADDRESS_LINE_REQUIRED)
    private String addressLine;

    @NotBlank(message = AddressMessageConstant.WARD_REQUIRED)
    private String ward;

    @NotBlank(message = AddressMessageConstant.DISTRICT_REQUIRED)
    private String district;

    @NotBlank(message = AddressMessageConstant.CITY_REQUIRED)
    private String city;

    private String label;

    @Builder.Default
    private Boolean isDefault = false;
}
