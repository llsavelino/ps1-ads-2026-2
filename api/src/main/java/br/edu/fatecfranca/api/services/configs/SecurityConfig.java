package br.edu.fatecfranca.api.services.configs;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                // A autenticação usa Bearer token, sem cookies de sessão.
                .csrf(csrf -> csrf.disable())

                // Não cria sessão para manter o usuário autenticado.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users/login")
                        .permitAll()
                        .anyRequest()
                        .authenticated())

                // Habilita a autenticação pelo JWT recebido no cabeçalho.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))

                .build();
    }


    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
       // Usa os mesmos bytes da chave usada pelo TokenService.
        var key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Valida o emissor e as condições temporais do token.
        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer("fatecfranca-api"));

        return decoder;
    }
}

