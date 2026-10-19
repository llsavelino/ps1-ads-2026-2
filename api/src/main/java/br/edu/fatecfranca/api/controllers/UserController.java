package br.edu.fatecfranca.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import br.edu.fatecfranca.api.controllers.pipes.dtos.UserDto;
import br.edu.fatecfranca.api.controllers.pipes.reqs.UserReq;
import br.edu.fatecfranca.api.controllers.pipes.reqs.ValidationGroups;
import br.edu.fatecfranca.api.entities.Users;
import br.edu.fatecfranca.api.services.UsersService;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.metadata.PageMeta;
import br.edu.fatecfranca.api.services.policy.contracts.Contract;

@RestController @RequestMapping("/users")
public class UserController {

	private final UsersService service;

	public UserController(UsersService service) { this.service = service; }

	private boolean exists(Long id) { return this.service.existsById(id); }

	@PostMapping
	public ResponseEntity<Contract<Long>> create(
			@Validated(ValidationGroups.Required.class) @RequestBody UserReq userReq
	) {
		Contract<Long> body = Contract.created(this.service.create(userReq.toEntity()).getId());
		return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Contract<UserDto>> update(
			@PathVariable Long id,
			@Validated(ValidationGroups.Required.class) @RequestBody UserReq userReq
	) {
		var existingUser = this.service.findById(id);
		if (existingUser.isEmpty()) {
			Contract<UserDto> body = Contract.notFound("USER_NOT_FOUND", "User not found");
			return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
		}

		Users user = userReq.toEntity();
		user.setId(id);
		user.setIsAdmin(existingUser.get().getIsAdmin());
		Contract<UserDto> body = Contract.ok(new UserDto(this.service.update(user)));
		return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
	}

	@GetMapping
	public ResponseEntity<Contract<Iterable<UserDto>>> getAll(Pageable pageable) {
		Page<UserDto> page = this.service.findAll(pageable).map(UserDto::new);
		Contract<Iterable<UserDto>> body = Contract.okPage(
			page.getContent(), PageMeta.of(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages())
		);
		return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Contract<UserDto>> find(@PathVariable Long id) {
		Contract<UserDto> body = this.service.findById(id)
			.map(user -> Contract.ok(new UserDto(user)))
			.orElse(Contract.notFound("USER_NOT_FOUND", "User not found"));
		return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Contract<Void>> delete(@PathVariable Long id) {
		if (!exists(id)) {
			Contract<Void> body = Contract.notFound("USER_NOT_FOUND", "User not found");
			return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
		}
		this.service.deleteById(id);
		return ResponseEntity.status(HttpStatus.valueOf(Contract.noContent().getHttpStatus())).body(Contract.noContent());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Contract<UserDto>> patch(
			@PathVariable Long id,
			@Validated @RequestBody UserReq userReq
	) {
		var optionalUser = this.service.findById(id);
		if (optionalUser.isEmpty()) {
			Contract<UserDto> body = Contract.notFound("USER_NOT_FOUND", "User not found");
			return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
		}
		if (!userReq.hasAnyField()) {
			Contract<UserDto> body = Contract.badRequest("BAD_REQUEST", "no fields provided for update");
			return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
		}

		Users user = optionalUser.get();
		if (userReq.getFullname() != null) user.setFullname(userReq.getFullname());
		if (userReq.getUsername() != null) user.setUsername(userReq.getUsername());
		if (userReq.getEmail() != null) user.setEmail(userReq.getEmail());
		if (userReq.getPassword() != null) user.setPassword(userReq.getPassword());

		Contract<UserDto> body = Contract.ok(new UserDto(this.service.update(user)));
		return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
	}
}
