package br.edu.fatecfranca.api.controllers.reqs;

import java.math.BigDecimal;
import java.time.LocalDate;

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

public class CarReq {

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "brand must not contain only whitespace")
	@Size(max = 100)
	private String brand;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "model must not contain only whitespace")
	@Size(max = 100)
	private String model;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "color must not contain only whitespace")
	@Size(max = 50)
	private String color;

	@NotNull(groups = ValidationGroups.Required.class)
	@Min(1886)
	@Max(2100)
	private Integer yearManufacture;

	@NotNull(groups = ValidationGroups.Required.class)
	private Boolean imported;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = "[A-Za-z0-9-]{5,10}", message = "plates must be a valid plate identifier")
	private String plates;

	@PastOrPresent
	private LocalDate sellingDate;

	@DecimalMin(value = "0.00", inclusive = true)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal sellingPrice;

	@Min(1)
	private Long customerId;

	public Cars toEntity() {
		Cars car = new Cars();
		car.setBrand(brand);
		car.setModel(model);
		car.setColor(color);
		car.setYearManufacture(yearManufacture);
		car.setImported(imported);
		car.setPlates(plates);
		car.setSellingDate(sellingDate);
		car.setSellingPrice(sellingPrice);
		if (customerId != null) {
			Customers customer = new Customers();
			customer.setId(customerId);
			car.setCustomer(customer);
		}
		return car;
	}

	public boolean hasAnyField() {
		return brand != null || model != null || color != null || yearManufacture != null
			|| imported != null || plates != null || sellingDate != null || sellingPrice != null
			|| customerId != null;
	}

	public String getBrand() { return brand; }
	public void setBrand(String brand) { this.brand = brand; }
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
