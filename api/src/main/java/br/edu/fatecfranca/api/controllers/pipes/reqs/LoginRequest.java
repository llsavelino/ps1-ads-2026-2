package br.edu.fatecfranca.api.controllers.pipes.reqs;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String usernameOrEmail, @NotBlank String password) {}

