package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import br.edu.fatecfranca.api.entities.PasswordResetToken;
import br.edu.fatecfranca.api.entities.Users;
import br.edu.fatecfranca.api.repositories.interfaces.PasswordResetTokenRepository;
import br.edu.fatecfranca.api.services.PasswordResetService;
import br.edu.fatecfranca.api.services.UsersService;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private JavaMailSender mailSender;

    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
            usersService,
            tokenRepository,
            mailSender,
            "https://example.com/reset-password",
            "no-reply@example.com"
        );
    }

    @Test
    void resetRequestSendsTokenAndAllowsOneTimePasswordReset() {
        Users user = new Users();
        user.setId(7L);
        user.setEmail("ana@example.com");
        when(usersService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        passwordResetService.requestReset(user.getEmail());

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).deleteByUserId(user.getId());
        verify(tokenRepository).save(tokenCaptor.capture());
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        PasswordResetToken storedToken = tokenCaptor.getValue();
        String mailText = mailCaptor.getValue().getText();
        Matcher tokenMatcher = Pattern.compile("token=([A-Za-z0-9_-]+)").matcher(mailText);
        assertTrue(tokenMatcher.find());
        String rawToken = tokenMatcher.group(1);
        assertNotEquals(rawToken, storedToken.getTokenHash());
        assertTrue(storedToken.getExpiresAt().isAfter(Instant.now()));
        assertTrue(storedToken.getExpiresAt().isBefore(Instant.now().plusSeconds(16 * 60)));

        when(tokenRepository.findByTokenHash(storedToken.getTokenHash())).thenReturn(Optional.of(storedToken));
        when(usersService.findById(user.getId())).thenReturn(Optional.of(user));

        assertTrue(passwordResetService.resetPassword(rawToken, "NewPassword123"));
        assertEquals("NewPassword123", user.getPassword());
        verify(usersService).update(user);
        verify(tokenRepository).delete(storedToken);
    }

    @Test
    void unknownEmailDoesNotCreateTokenOrSendMail() {
        when(usersService.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        passwordResetService.requestReset("unknown@example.com");

        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void expiredTokenCannotResetPassword() {
        PasswordResetToken expiredToken = new PasswordResetToken(7L, "token-hash", Instant.now().minusSeconds(1));
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expiredToken));

        assertFalse(passwordResetService.resetPassword("expired-token", "NewPassword123"));
        verify(usersService, never()).update(any(Users.class));
        verify(tokenRepository).delete(expiredToken);
    }
}
