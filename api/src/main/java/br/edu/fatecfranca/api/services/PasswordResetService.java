package br.edu.fatecfranca.api.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.fatecfranca.api.entities.PasswordResetToken;
import br.edu.fatecfranca.api.repositories.interfaces.PasswordResetTokenRepository;

@Service
public class PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long TOKEN_LIFETIME_MINUTES = 15;

    private final UsersService usersService;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final String resetUrl;
    private final String mailFrom;

    public PasswordResetService(
            UsersService usersService,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            @Value("${app.password-reset-url}") String resetUrl,
            @Value("${app.mail-from}") String mailFrom
    ) {
        this.usersService = usersService;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.resetUrl = resetUrl;
        this.mailFrom = mailFrom;
    }

    @Transactional
    public void requestReset(String email) {
        var user = usersService.findByEmail(email.trim());
        if (user.isEmpty()) {
            return;
        }

        String token = generateToken();
        tokenRepository.deleteByUserId(user.get().getId());
        tokenRepository.save(new PasswordResetToken(
            user.get().getId(),
            hashToken(token),
            Instant.now().plus(TOKEN_LIFETIME_MINUTES, ChronoUnit.MINUTES)
        ));

        String link = UriComponentsBuilder.fromUriString(resetUrl)
            .replaceQueryParam("token", token)
            .build()
            .toUriString();
        SimpleMailMessage message = new SimpleMailMessage();
        if (!mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }
        message.setTo(user.get().getEmail());
        message.setSubject("Redefinição de senha");
        message.setText(
            "Use o link abaixo para redefinir sua senha. Ele expira em "
                + TOKEN_LIFETIME_MINUTES + " minutos:\n\n" + link
        );
        mailSender.send(message);
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        var resetToken = tokenRepository.findByTokenHash(hashToken(token));
        if (resetToken.isEmpty()) {
            return false;
        }

        PasswordResetToken storedToken = resetToken.get();
        if (!storedToken.getExpiresAt().isAfter(Instant.now())) {
            tokenRepository.delete(storedToken);
            return false;
        }

        var user = usersService.findById(storedToken.getUserId());
        if (user.isEmpty()) {
            tokenRepository.delete(storedToken);
            return false;
        }

        user.get().setPassword(newPassword);
        usersService.update(user.get());
        tokenRepository.delete(storedToken);
        return true;
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
