package com.sport_pro_be.modules.user_address.service;

import com.sport_pro_be.common.SecurityUtils;
import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.audit.annotation.Loggable;
import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.user_address.constant.AddressMessageConstant;
import com.sport_pro_be.modules.user_address.domain.UserAddress;
import com.sport_pro_be.modules.user_address.dto.request.AddressRequest;
import com.sport_pro_be.modules.user_address.dto.response.AddressResponse;
import com.sport_pro_be.modules.user_address.enums.AddressType;
import com.sport_pro_be.modules.user_address.interfaces.IAddressService;
import com.sport_pro_be.modules.user_address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private static final int MAX_ADDRESSES = 5;

    @Override
    @Transactional
    @Loggable(action = "CREATE_ADDRESS", module = "USER_ADDRESS")
    public AddressResponse createAddress(AddressRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        long count = addressRepository.countByUserId(currentUser.getId());
        if (count >= MAX_ADDRESSES) {
            throw new BadRequestException(AddressMessageConstant.ADDRESS_LIMIT_REACHED);
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.resetDefaultByUserId(currentUser.getId());
        } else if (count == 0) {
            request.setIsDefault(true);
        }

        UserAddress address = UserAddress.builder()
                .user(currentUser)
                .receiverName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .province(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .detailAddress(request.getAddressLine())
                .label(request.getLabel())
                .isDefault(request.getIsDefault())
                .type(AddressType.HOME)
                .build();

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    @Override
    @Transactional
    @Loggable(action = "UPDATE_ADDRESS", module = "USER_ADDRESS")
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        UserAddress address = addressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AddressMessageConstant.ADDRESS_NOT_FOUND));

        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.getIsDefault()) {
            addressRepository.resetDefaultByUserId(currentUser.getId());
        }

        address.setReceiverName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setProvince(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setDetailAddress(request.getAddressLine());
        address.setLabel(request.getLabel());
        address.setIsDefault(request.getIsDefault());

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    @Override
    @Transactional
    @Loggable(action = "DELETE_ADDRESS", module = "USER_ADDRESS")
    public void deleteAddress(Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        UserAddress address = addressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AddressMessageConstant.ADDRESS_NOT_FOUND));

        addressRepository.delete(address);

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<UserAddress> addresses = addressRepository.findAllByUserIdOrderByIsDefaultDesc(currentUser.getId());
            if (!addresses.isEmpty()) {
                UserAddress nextDefault = addresses.get(0);
                nextDefault.setIsDefault(true);
                addressRepository.save(nextDefault);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        Long userId = SecurityUtils.getCurrentUserId();
        return addressRepository.findAllByUserIdOrderByIsDefaultDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Loggable(action = "SET_DEFAULT_ADDRESS", module = "USER_ADDRESS")
    public AddressResponse setDefaultAddress(Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        UserAddress address = addressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AddressMessageConstant.ADDRESS_NOT_FOUND));

        if (!address.getIsDefault()) {
            addressRepository.resetDefaultByUserId(currentUser.getId());
            address.setIsDefault(true);
            address = addressRepository.save(address);
        }

        return mapToResponse(address);
    }

    private AddressResponse mapToResponse(UserAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getReceiverName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine(address.getDetailAddress())
                .ward(address.getWard())
                .district(address.getDistrict())
                .city(address.getProvince())
                .label(address.getLabel())
                .isDefault(address.getIsDefault())
                .build();
    }
}
