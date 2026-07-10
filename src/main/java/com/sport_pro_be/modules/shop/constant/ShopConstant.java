package com.sport_pro_be.modules.shop.constant;

public final class ShopConstant {

    private ShopConstant() {
    }

    public static final String SHOP_RETRIEVED = "Lấy thông tin cửa hàng thành công";
    public static final String SHOP_UPDATED = "Cập nhật thông tin cửa hàng thành công";
    public static final String SHOP_IMAGE_UPLOADED = "Tải ảnh lên thành công";
    public static final String GEOCODE_SUCCESS = "Tìm tọa độ thành công";
    public static final String REVERSE_GEOCODE_SUCCESS = "Tìm địa chỉ thành công";

    // Defaults for the first-ever shop profile.
    public static final String DEFAULT_NAME = "Sport Pro";
    public static final String DEFAULT_ADDRESS = "123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh";
    // Coordinates matching DEFAULT_ADDRESS (Nguyễn Văn Linh, Quận 7) so a fresh shop has a valid marker.
    public static final String DEFAULT_LATITUDE = "10.7295";
    public static final String DEFAULT_LONGITUDE = "106.7215";
    public static final String DEFAULT_PHONE = "0909 123 456";
    public static final String DEFAULT_OPENING_HOURS = "08:00 - 21:00";
    public static final String DEFAULT_DESCRIPTION =
            "Cửa hàng thể thao chính hãng — giày, trang phục và phụ kiện hiệu suất cao.";

    // Google Maps "directions to" deep link.
    public static final String MAPS_DIRECTIONS_BASE_URL = "https://www.google.com/maps/dir/?api=1&destination=";
    public static final String MAPS_DIRECTIONS_PLACE_ID_PARAM = "&destination_place_id=";
}
