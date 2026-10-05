package br.edu.fatecfranca.api.controllers.reqs;

import java.time.LocalDate;

import br.edu.fatecfranca.api.entities.Customers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerReq {

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "name must not contain only whitespace")
	@Size(max = 100)
	private String name;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = "[A-Za-z0-9./-]{5,20}", message = "identDocument must contain 5 to 20 valid characters")
	private String identDocument;

	@PastOrPresent
	private LocalDate birthDate;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "streetName must not contain only whitespace")
	@Size(max = 200)
	private String streetName;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "houseNumber must not contain only whitespace")
	@Size(max = 20)
	private String houseNumber;

	@Size(max = 200)
	private String complements;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "district must not contain only whitespace")
	@Size(max = 100)
	private String district;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "municipality must not contain only whitespace")
	@Size(max = 100)
	private String municipality;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = "[A-Z]{2}", message = "state must be two uppercase letters")
	private String state;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = "(?=.*\\S)[0-9+()\\-\\s]{8,20}", message = "invalid phone")
	private String phone;

	@NotBlank(groups = ValidationGroups.Required.class)
	@Pattern(regexp = ".*\\S.*", message = "email must not contain only whitespace")
	@Email
	@Size(max = 254)
	private String email;

	public Customers toEntity() {
		Customers customer = new Customers();
		customer.setName(name);
		customer.setIdentDocument(identDocument);
		customer.setBirthDate(birthDate);
		customer.setStreetName(streetName);
		customer.setHouseNumber(houseNumber);
		customer.setComplements(complements);
		customer.setDistrict(district);
		customer.setMunicipality(municipality);
		customer.setState(state);
		customer.setPhone(phone);
		customer.setEmail(email);
		return customer;
	}

	public boolean hasAnyField() {
		return name != null || identDocument != null || birthDate != null || streetName != null
			|| houseNumber != null || complements != null || district != null || municipality != null
			|| state != null || phone != null || email != null;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getIdentDocument() { return identDocument; }
	public void setIdentDocument(String identDocument) { this.identDocument = identDocument; }
	public LocalDate getBirthDate() { return birthDate; }
	public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
	public String getStreetName() { return streetName; }
	public void setStreetName(String streetName) { this.streetName = streetName; }
	public String getHouseNumber() { return houseNumber; }
	public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
	public String getComplements() { return complements; }
	public void setComplements(String complements) { this.complements = complements; }
	public String getDistrict() { return district; }
	public void setDistrict(String district) { this.district = district; }
	public String getMunicipality() { return municipality; }
	public void setMunicipality(String municipality) { this.municipality = municipality; }
	public String getState() { return state; }
	public void setState(String state) { this.state = state; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
}
