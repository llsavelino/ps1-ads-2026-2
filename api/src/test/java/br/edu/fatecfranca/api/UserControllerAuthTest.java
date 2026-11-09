package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.edu.fatecfranca.api.controllers.UserController;
import br.edu.fatecfranca.api.controllers.pipes.reqs.ForgotPasswordRequest;
import br.edu.fatecfranca.api.controllers.pipes.reqs.LoginRequest;
import br.edu.fatecfranca.api.controllers.pipes.reqs.ResetPasswordRequest;
import br.edu.fatecfranca.api.controllers.presentation.responses.LoginResponse;
import br.edu.fatecfranca.api.entities.Users;
import br.edu.fatecfranca.api.services.PasswordResetService;
import br.edu.fatecfranca.api.services.TokenJwtService;
import br.edu.fatecfranca.api.services.UsersService;
import br.edu.fatecfranca.api.services.policy.contracts.Contract;

@ExtendWith(MockitoExtension.class)
class UserControllerAuthTest {

    @Mock
    private UsersService usersService;

    @Mock
    private TokenJwtService tokenService;

    @Mock
    private PasswordResetService passwordResetService;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(usersService, tokenService, passwordResetService);
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        Users user = new Users();
        user.setId(7L);
        when(usersService.authenticate("ana", "password")).thenReturn(Optional.of(user));
        when(tokenService.generate(user)).thenReturn("jwt-token");

        ResponseEntity<Contract<LoginResponse>> response =
            controller.login(new LoginRequest("ana", "password"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getData().token());
    }

    @Test
    void loginRejectsInvalidCredentials() {
        when(usersService.authenticate("ana", "wrong-password")).thenReturn(Optional.empty());

        ResponseEntity<Contract<LoginResponse>> response =
            controller.login(new LoginRequest("ana", "wrong-password"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void forgotPasswordReturnsGenericSuccess() {
        ResponseEntity<Contract<Void>> response =
            controller.forgotPassword(new ForgotPasswordRequest("ana@example.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(passwordResetService).requestReset("ana@example.com");
    }

    @Test
    void resetPasswordRejectsInvalidToken() {
        when(passwordResetService.resetPassword("invalid", "NewPassword123")).thenReturn(false);

        ResponseEntity<Contract<Void>> response =
            controller.resetPassword(new ResetPasswordRequest("invalid", "NewPassword123"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }
}
