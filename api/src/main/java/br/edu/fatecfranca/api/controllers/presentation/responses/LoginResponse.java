package br.edu.fatecfranca.api.controllers.presentation.responses;

public record LoginResponse(
    String token
) {
    public LoginResponse(String token) {
        this.token = token;
    }

    public static LoginResponse from(String token) {
        return new LoginResponse(token);
    }
}
