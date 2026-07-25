package com.buymore.backend.service;

import com.buymore.backend.dto.AddressRequest;
import com.buymore.backend.dto.AddressResponse;
import com.buymore.backend.entity.Address;
import com.buymore.backend.entity.User;
import com.buymore.backend.exception.ForbiddenException;
import com.buymore.backend.exception.ResourceNotFoundException;
import com.buymore.backend.repository.AddressRepository;
import com.buymore.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressResponse> getByCustomer(Long customerId) {
        return addressRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AddressResponse create(Long customerId, AddressRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + customerId));

        Address address = Address.builder()
                .fullName(request.fullName())
                .phone(request.phone())
                .courierName(request.courierName())
                .location(request.location())
                .customer(customer)
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long customerId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("Address does not belong to this user");
        }

        addressRepository.delete(address);
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getCourierName(),
                address.getLocation()
        );
    }
}
