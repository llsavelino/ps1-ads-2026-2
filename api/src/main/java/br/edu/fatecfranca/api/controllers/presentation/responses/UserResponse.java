package br.edu.fatecfranca.api.controllers.presentation.responses;

import br.edu.fatecfranca.api.entities.Users;

public record UserResponse(
    Long id,
    String fullname,
    String username,
    String email,
    Boolean isAdmin
) {
    public UserResponse(Users user) {
        this(
            user != null ? user.getId() : null,
            user != null ? user.getFullname() : null,
            user != null ? user.getUsername() : null,
            user != null ? user.getEmail() : null,
            user != null ? user.getIsAdmin() : null
        );
    }

    public static UserResponse from(Users user) {
        return new UserResponse(user);
    }
}
