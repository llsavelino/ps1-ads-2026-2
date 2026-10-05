package br.edu.fatecfranca.api.controllers.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.edu.fatecfranca.api.entities.Cars;
import br.edu.fatecfranca.api.entities.Customers;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CarDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Pattern(regexp = ".*\\S.*")
    @Size(max = 100)
    private String brand;

    @NotBlank
    @Pattern(regexp = ".*\\S.*")
    @Size(max = 100)
    private String model;

    @NotBlank
    @Pattern(regexp = ".*\\S.*")
    @Size(max = 50)
    private String color;

    @NotNull
    @Min(1886)
    @Max(2100)
    private Integer yearManufacture;

    @NotNull
    private Boolean imported;

    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9-]{5,10}")
    private String plates;

    @PastOrPresent
    private LocalDate sellingDate;

    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal sellingPrice;

    @Min(1)
    private Long customerId;

    public CarDto() {}

    public CarDto(Cars c) {
        if (c == null) return;
        this.id = c.getId();
        this.brand = c.getBrand();
        this.model = c.getModel();
        this.color = c.getColor();
        this.yearManufacture = c.getYearManufacture();
        this.imported = c.getImported();
        this.plates = c.getPlates();
        this.sellingDate = c.getSellingDate();
        this.sellingPrice = c.getSellingPrice();
        if (c.getCustomer() != null) {
            this.customerId = c.getCustomer().getId();
        }
    }

    public Cars toEntity() {
        Cars c = new Cars();
        c.setBrand(this.brand);
        c.setModel(this.model);
        c.setColor(this.color);
        c.setYearManufacture(this.yearManufacture);
        c.setImported(this.imported);
        c.setPlates(this.plates);
        c.setSellingDate(this.sellingDate);
        c.setSellingPrice(this.sellingPrice);

        if (this.customerId != null) {
            Customers customer = new Customers();
            customer.setId(this.customerId);
            c.setCustomer(customer);
        }

        return c;
    }

    public boolean isValidForUpdate() {
        return (this.brand != null && !this.brand.isBlank())
            || (this.model != null && !this.model.isBlank())
            || (this.color != null && !this.color.isBlank())
            || (this.yearManufacture != null)
            || (this.imported != null)
            || (this.plates != null && !this.plates.isBlank())
            || (this.sellingDate != null)
            || (this.sellingPrice != null)
            || (this.customerId != null);
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Long getId() { return id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getYearManufacture() { return yearManufacture; }
    public void setYearManufacture(Integer yearManufacture) { this.yearManufacture = yearManufacture; }

    public Boolean getImported() { return imported; }
    public void setImported(Boolean imported) { this.imported = imported; }

    public String getPlates() { return plates; }
    public void setPlates(String plates) { this.plates = plates; }

    public LocalDate getSellingDate() { return sellingDate; }
    public void setSellingDate(LocalDate sellingDate) { this.sellingDate = sellingDate; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
