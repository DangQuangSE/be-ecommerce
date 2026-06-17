package com.sport_pro_be.modules.setting.constant;

public class SiteSettingConstant {
    private SiteSettingConstant() {
    }

    public static final Long SETTINGS_ID = 1L;
    public static final String DEFAULT_RETURN_POLICY =
            "Miễn phí đổi size trong vòng 7 ngày kể từ khi nhận hàng. Bảo hành chính hãng keo chỉ 3 tháng đối với các lỗi sản xuất. Dịch vụ chăm sóc vận động viên tận tình.";

    // Success Messages
    public static final String GET_SETTINGS_SUCCESS = "Site settings retrieved successfully";
    public static final String UPDATE_SETTINGS_SUCCESS = "Site settings updated successfully";

    // Validation Messages
    public static final String RETURN_POLICY_CANNOT_BE_BLANK = "Return policy content cannot be blank";

    // Error Messages
    public static final String SITE_SETTINGS_NOT_FOUND = "Site settings not found";
}
