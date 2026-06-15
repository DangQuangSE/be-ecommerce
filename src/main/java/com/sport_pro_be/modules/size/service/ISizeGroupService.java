package com.sport_pro_be.modules.size.service;

import com.sport_pro_be.modules.size.dto.request.SizeGroupRequest;
import com.sport_pro_be.modules.size.dto.response.SizeGroupResponse;

import java.util.List;

public interface ISizeGroupService {
    List<SizeGroupResponse> getAll();
    SizeGroupResponse create(SizeGroupRequest request);
    SizeGroupResponse update(Long id, SizeGroupRequest request);
    void delete(Long id);
}
