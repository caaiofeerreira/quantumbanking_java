package com.quantumbanking.modules.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAddressRequestDTO(@NotBlank String street,
                                      @NotBlank String number,
                                      String complement,
                                      @NotBlank String neighborhood,
                                      @NotBlank String city,
                                      @NotBlank(message = "Estado inválido")
                                      @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres") String state,
                                      @NotBlank(message = "CEP é obrigatório")
                                      @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido") String zipCode) {

}
