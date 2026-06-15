package com.sport_pro_be.modules.size.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.size.constant.SizeGroupMessageConstant;
import com.sport_pro_be.modules.size.dto.response.SizeGroupResponse;
import com.sport_pro_be.modules.size.service.ISizeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/size-groups")
@RequiredArgsConstructor
public class SizeGroupController {

    private final ISizeGroupService sizeGroupService;

    @GetMapping
    public ApiResponse<List<SizeGroupResponse>> getAll() {
        return ApiResponse.of(SizeGroupMessageConstant.GET_ALL_SUCCESS, sizeGroupService.getAll());
    }
}
