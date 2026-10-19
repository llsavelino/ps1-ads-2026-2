package br.edu.fatecfranca.api.controllers.pipes.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.edu.fatecfranca.api.entities.Customers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerDto{

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotBlank(message = "name must not be blank")
	@Pattern(regexp = ".*\\S.*", message = "name must not contain only whitespace")
	@Size(max = 100, message = "name must be at most 100 characters")
	private String name;

	@NotBlank(message = "identDocument must not be blank")
	@Pattern(regexp = "[A-Za-z0-9./-]{5,20}", message = "identDocument must contain 5 to 20 valid characters")
	private String identDocument;

	@PastOrPresent(message = "birthDate must be in the past or present")
	private LocalDate birthDate;

	@NotBlank(message = "streetName must not be blank")
	@Pattern(regexp = ".*\\S.*", message = "streetName must not contain only whitespace")
	@Size(max = 200, message = "streetName must be at most 200 characters")
	private String streetName;

	@NotBlank(message = "houseNumber must not be blank")
	@Pattern(regexp = ".*\\S.*", message = "houseNumber must not contain only whitespace")
	@Size(max = 20, message = "houseNumber must be at most 20 characters")
	private String houseNumber;

	private String complements;

	@NotBlank(message = "district must not be blank")
	@Pattern(regexp = ".*\\S.*", message = "district must not contain only whitespace")
	@Size(max = 100, message = "district must be at most 100 characters")
	private String district;

	@NotBlank(message = "municipality must not be blank")
	@Pattern(regexp = ".*\\S.*", message = "municipality must not contain only whitespace")
	@Size(max = 100, message = "municipality must be at most 100 characters")
	private String municipality;

	@NotBlank(message = "state must not be blank")
	@Pattern(regexp = "^[A-Z]{2}$", message = "state must be two uppercase letters")
	private String state;

	@NotBlank(message = "phone must not be blank")
	@Pattern(regexp = "(?=.*\\S)[0-9+()\\-\\s]{8,20}", message = "invalid phone")
	private String phone;

	@NotBlank(message = "email must not be blank")
	@Pattern(regexp = ".*\\S.*", message = "email must not contain only whitespace")
	@Email(message = "email must be valid")
	@Size(max = 254, message = "email must be at most 254 characters")
	private String email;

	public CustomerDto() {}

	public CustomerDto(Customers c) {
		if (c == null) return;
		this.id = c.getId();
		this.name = c.getName();
		this.identDocument = c.getIdentDocument();
		this.birthDate = c.getBirthDate();
		this.streetName = c.getStreetName();
		this.houseNumber = c.getHouseNumber();
		this.complements = c.getComplements();
		this.district = c.getDistrict();
		this.municipality = c.getMunicipality();
		this.state = c.getState();
		this.phone = c.getPhone();
		this.email = c.getEmail();
	}

	public Customers toEntity() {
		Customers c = new Customers();
		c.setName(this.name);
		c.setIdentDocument(this.identDocument);
		c.setBirthDate(this.birthDate);
		c.setStreetName(this.streetName);
		c.setHouseNumber(this.houseNumber);
		c.setComplements(this.complements);
		c.setDistrict(this.district);
		c.setMunicipality(this.municipality);
		c.setState(this.state);
		c.setPhone(this.phone);
		c.setEmail(this.email);
		return c;
	}

	public boolean isValidForUpdate() {
		return (this.name != null && !this.name.isBlank())
			|| (this.identDocument != null && !this.identDocument.isBlank())
			|| (this.streetName != null && !this.streetName.isBlank())
			|| (this.houseNumber != null && !this.houseNumber.isBlank())
			|| (this.district != null && !this.district.isBlank())
			|| (this.municipality != null && !this.municipality.isBlank())
			|| (this.state != null && !this.state.isBlank())
			|| (this.birthDate != null)
			|| (this.complements != null)
			|| (this.phone != null && !this.phone.isBlank())
			|| (this.email != null && !this.email.isBlank());
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

	public Long getId() { return id; }

}
