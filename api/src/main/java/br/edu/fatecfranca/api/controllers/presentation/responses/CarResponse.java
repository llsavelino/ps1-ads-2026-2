package br.edu.fatecfranca.api.controllers.presentation.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.fatecfranca.api.entities.Cars;

public record CarResponse(
    Long id,
    String brand,
    String model,
    String color,
    Integer yearManufacture,
    Boolean imported,
    String plates,
    LocalDate sellingDate,
    BigDecimal sellingPrice,
    Long customerId
) {
    public CarResponse(Cars car) {
        this(
            car != null ? car.getId() : null,
            car != null ? car.getBrand() : null,
            car != null ? car.getModel() : null,
            car != null ? car.getColor() : null,
            car != null ? car.getYearManufacture() : null,
            car != null ? car.getImported() : null,
            car != null ? car.getPlates() : null,
            car != null ? car.getSellingDate() : null,
            car != null ? car.getSellingPrice() : null,
            car != null ? car.getCustomerId() : null
        );
    }

    public static CarResponse from(Cars car) {
        return new CarResponse(car);
    }
}
