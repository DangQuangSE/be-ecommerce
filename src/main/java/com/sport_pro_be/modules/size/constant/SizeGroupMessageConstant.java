package com.sport_pro_be.modules.size.constant;

public class SizeGroupMessageConstant {
    private SizeGroupMessageConstant() {}

    public static final String CREATE_SUCCESS = "Size group created successfully";
    public static final String UPDATE_SUCCESS = "Size group updated successfully";
    public static final String DELETE_SUCCESS = "Size group deleted successfully";
    public static final String GET_ALL_SUCCESS = "Size groups retrieved successfully";

    public static final String NOT_FOUND = "Size group not found with id: %d";
    public static final String NAME_ALREADY_EXISTS = "Size group name already exists: %s";
    public static final String IN_USE_BY_PRODUCT = "Cannot delete size group '%s' because it is used by one or more products";
}
