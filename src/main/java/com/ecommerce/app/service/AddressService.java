package com.ecommerce.app.service;

import com.ecommerce.app.dto.AddressDto;
import com.ecommerce.app.entity.Address;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.AddressMapper;
import com.ecommerce.app.repository.AddressRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository,
                          AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    @Transactional
    public AddressDto addAddress(String email, AddressDto addressDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        List<Address> existingAddresses = addressRepository.findByUser(user);
        boolean makeDefault = existingAddresses.isEmpty() || addressDto.isDefault();

        if (makeDefault) {
            // Remove default from all others
            existingAddresses.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existingAddresses);
        }

        Address address = addressMapper.toEntity(addressDto);
        address.setUser(user);
        address.setDefault(makeDefault);

        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    public List<AddressDto> getAddresses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto updateAddress(String email, Long addressId, AddressDto addressDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized modification of address");
        }

        if (addressDto.isDefault() && !address.isDefault()) {
            // Setting this address as default, unset others first
            List<Address> existingAddresses = addressRepository.findByUser(user);
            existingAddresses.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existingAddresses);
            address.setDefault(true);
        } else if (!addressDto.isDefault() && address.isDefault()) {
            address.setDefault(false);
        }

        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPincode(addressDto.getPincode());
        address.setCountry(addressDto.getCountry());

        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDto(updatedAddress);
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized deletion of address");
        }

        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        if (wasDefault) {
            // Set another address of the user as default if available
            List<Address> remaining = addressRepository.findByUser(user);
            if (!remaining.isEmpty()) {
                remaining.get(0).setDefault(true);
                addressRepository.save(remaining.get(0));
            }
        }
    }
}
