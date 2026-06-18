package com.sport_pro_be.modules.user_address.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private String label;
    private Boolean isDefault;
}
