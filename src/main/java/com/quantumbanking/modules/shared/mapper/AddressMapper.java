package com.quantumbanking.modules.shared.mapper;

import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.AddressDTO;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDTO toAddressDTO(Address address) {
        return new AddressDTO(
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }

    public Address toAddress(AddressRequestDTO requestDTO) {
        return new Address(
                requestDTO.street(),
                requestDTO.number(),
                requestDTO.complement(),
                requestDTO.neighborhood(),
                requestDTO.city(),
                requestDTO.state(),
                requestDTO.zipCode()
        );
    }
}