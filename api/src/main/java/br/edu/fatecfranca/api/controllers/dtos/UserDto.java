package br.edu.fatecfranca.api.controllers.dtos;

import br.edu.fatecfranca.api.entities.Users;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotBlank
	@Pattern(regexp = ".*\\S.*")
	@Size(max = 100)
	private String fullname;

	@NotBlank
	@Pattern(regexp = "[A-Za-z0-9._-]{3,100}")
	private String username;

	@NotBlank
	@Email
	@Size(max = 254)
	private String email;

	@NotBlank
	@Size(min = 8, max = 72)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Boolean isAdmin;

	public UserDto() {}

	public UserDto(Users user) {
		if (user == null) return;
		this.id = user.getId();
		this.fullname = user.getFullname();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.isAdmin = user.getIsAdmin();
	}

	public Users toEntity() {
		Users user = new Users();
		user.setFullname(this.fullname);
		user.setUsername(this.username);
		user.setEmail(this.email);
		user.setPassword(this.password);
		return user;
	}

	public boolean isValidForUpdate() {
		return (this.fullname != null && !this.fullname.isBlank())
			|| (this.username != null && !this.username.isBlank())
			|| (this.email != null && !this.email.isBlank())
			|| (this.password != null && !this.password.isBlank());
	}

	public Long getId() { return id; }

	public String getFullname() { return fullname; }
	public void setFullname(String fullname) { this.fullname = fullname; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public Boolean getIsAdmin() { return isAdmin; }
}
