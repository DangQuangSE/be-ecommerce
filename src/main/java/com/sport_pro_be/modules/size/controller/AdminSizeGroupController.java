package com.sport_pro_be.modules.size.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.size.constant.SizeGroupMessageConstant;
import com.sport_pro_be.modules.size.dto.request.SizeGroupRequest;
import com.sport_pro_be.modules.size.dto.response.SizeGroupResponse;
import com.sport_pro_be.modules.size.service.ISizeGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/size-groups")
@RequiredArgsConstructor
public class AdminSizeGroupController {

    private final ISizeGroupService sizeGroupService;

    @GetMapping
    public ApiResponse<List<SizeGroupResponse>> getAll() {
        return ApiResponse.of(SizeGroupMessageConstant.GET_ALL_SUCCESS, sizeGroupService.getAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SizeGroupResponse> create(@Valid @RequestBody SizeGroupRequest request) {
        return ApiResponse.of(SizeGroupMessageConstant.CREATE_SUCCESS, sizeGroupService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SizeGroupResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody SizeGroupRequest request) {
        return ApiResponse.of(SizeGroupMessageConstant.UPDATE_SUCCESS, sizeGroupService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sizeGroupService.delete(id);
        return ApiResponse.of(SizeGroupMessageConstant.DELETE_SUCCESS, null);
    }
}
