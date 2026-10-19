package br.edu.fatecfranca.api.controllers.pipes.dtos;

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

    @NotBlank(message = "brand must not be blank")
    @Pattern(regexp = ".*\\S.*", message = "brand must not contain only whitespace")
    @Size(max = 100, message = "brand must be at most 100 characters")
    private String brand;

    @NotBlank(message = "model must not be blank")
    @Pattern(regexp = ".*\\S.*", message = "model must not contain only whitespace")
    @Size(max = 100, message = "model must be at most 100 characters")
    private String model;

    @NotBlank(message = "color must not be blank")
    @Pattern(regexp = ".*\\S.*", message = "color must not contain only whitespace")
    @Size(max = 50, message = "color must be at most 50 characters")
    private String color;

    @NotNull(message = "yearManufacture must not be null")
    @Min(value = 1886, message = "yearManufacture must be at least 1886")
    @Max(value = 2100, message = "yearManufacture must be at most 2100")
    private Integer yearManufacture;

    @NotNull(message = "imported must not be null")
    private Boolean imported;

    @NotBlank(message = "plates must not be blank")
    @Pattern(regexp = "[A-Za-z0-9-]{5,10}", message = "plates must be a valid plate identifier")
    private String plates;

    @PastOrPresent(message = "sellingDate must be in the past or present")
    private LocalDate sellingDate;

    @DecimalMin(value = "0.00", inclusive = true, message = "sellingPrice must be at least 0.00")
    @Digits(integer = 10, fraction = 2, message = "sellingPrice must have at most 10 integer and 2 fractional digits")
    private BigDecimal sellingPrice;

    @Min(value = 1, message = "customerId must be at least 1")
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
