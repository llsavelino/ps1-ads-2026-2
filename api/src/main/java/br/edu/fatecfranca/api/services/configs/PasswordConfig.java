package br.edu.fatecfranca.api.services.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class PasswordConfig {


    @Bean public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
           16,         // Tamanho do salt, em bytes
           32,         // Tamanho do hash, em bytes
           4,          // Paralelismo
           65 * 1024,  // Memória, em KiB: 65 MiB
           3           // Iterações
        );
    }
}
