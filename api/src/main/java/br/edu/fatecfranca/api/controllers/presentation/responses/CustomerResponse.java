package br.edu.fatecfranca.api.controllers.presentation.responses;

import java.time.LocalDate;

import br.edu.fatecfranca.api.entities.Customers;

public record CustomerResponse(
    Long id,
    String name,
    String identDocument,
    LocalDate birthDate,
    String streetName,
    String houseNumber,
    String complements,
    String district,
    String municipality,
    String state,
    String phone,
    String email
) {
    public CustomerResponse(Customers customer) {
        this(
            customer != null ? customer.getId() : null,
            customer != null ? customer.getName() : null,
            customer != null ? customer.getIdentDocument() : null,
            customer != null ? customer.getBirthDate() : null,
            customer != null ? customer.getStreetName() : null,
            customer != null ? customer.getHouseNumber() : null,
            customer != null ? customer.getComplements() : null,
            customer != null ? customer.getDistrict() : null,
            customer != null ? customer.getMunicipality() : null,
            customer != null ? customer.getState() : null,
            customer != null ? customer.getPhone() : null,
            customer != null ? customer.getEmail() : null
        );
    }

    public static CustomerResponse from(Customers customer) {
        return new CustomerResponse(customer);
    }
}
