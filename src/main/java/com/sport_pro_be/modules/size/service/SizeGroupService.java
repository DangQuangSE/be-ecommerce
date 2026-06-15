package com.sport_pro_be.modules.size.service;

import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ConflictException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.product.repository.ProductRepository;
import com.sport_pro_be.modules.size.constant.SizeGroupMessageConstant;
import com.sport_pro_be.modules.size.domain.SizeGroup;
import com.sport_pro_be.modules.size.domain.SizeOption;
import com.sport_pro_be.modules.size.dto.request.SizeGroupRequest;
import com.sport_pro_be.modules.size.dto.request.SizeOptionRequest;
import com.sport_pro_be.modules.size.dto.response.SizeGroupResponse;
import com.sport_pro_be.modules.size.dto.response.SizeOptionResponse;
import com.sport_pro_be.modules.size.repository.SizeGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeGroupService implements ISizeGroupService {

    private final SizeGroupRepository sizeGroupRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SizeGroupResponse> getAll() {
        return sizeGroupRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SizeGroupResponse create(SizeGroupRequest request) {
        if (sizeGroupRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    String.format(SizeGroupMessageConstant.NAME_ALREADY_EXISTS, request.getName()));
        }
        SizeGroup group = new SizeGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        populateSizes(group, request.getSizes());
        return toResponse(sizeGroupRepository.save(group));
    }

    @Override
    public SizeGroupResponse update(Long id, SizeGroupRequest request) {
        SizeGroup group = findById(id);
        if (!group.getName().equals(request.getName()) && sizeGroupRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    String.format(SizeGroupMessageConstant.NAME_ALREADY_EXISTS, request.getName()));
        }
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.getSizes().clear();
        populateSizes(group, request.getSizes());
        return toResponse(sizeGroupRepository.save(group));
    }

    @Override
    public void delete(Long id) {
        SizeGroup group = findById(id);
        if (productRepository.existsBySizeGroupId(id)) {
            throw new ConflictException(
                    String.format(SizeGroupMessageConstant.IN_USE_BY_PRODUCT, group.getName()));
        }
        sizeGroupRepository.deleteById(id);
    }

    private SizeGroup findById(Long id) {
        return sizeGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(SizeGroupMessageConstant.NOT_FOUND, id)));
    }

    private void populateSizes(SizeGroup group, List<SizeOptionRequest> sizeRequests) {
        if (sizeRequests == null) return;
        for (SizeOptionRequest req : sizeRequests) {
            SizeOption option = new SizeOption();
            option.setName(req.getName());
            option.setDisplayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0);
            option.setSizeGroup(group);
            group.getSizes().add(option);
        }
    }

    private SizeGroupResponse toResponse(SizeGroup group) {
        List<SizeOptionResponse> sizes = group.getSizes().stream()
                .map(o -> SizeOptionResponse.builder()
                        .id(o.getId())
                        .name(o.getName())
                        .displayOrder(o.getDisplayOrder())
                        .build())
                .toList();
        return SizeGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .sizes(sizes)
                .build();
    }
}
