package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.fatecfranca.api.entities.Users;
import br.edu.fatecfranca.api.repositories.interfaces.UserRepository;
import br.edu.fatecfranca.api.services.UsersService;

class UserPasswordHashingTest {

    @Test
    void createHashesPasswordBeforeSaving() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 4, 65 * 1024, 3);
        UsersService service = new UsersService(repository, passwordEncoder);

        Users user = new Users();
        user.setFullname("Ana Souza");
        user.setUsername("ana.souza");
        user.setEmail("ana@example.com");
        user.setPassword("123456");

        when(repository.save(any(Users.class))).thenAnswer(invocation -> {
            Users saved = invocation.getArgument(0);
            assertNotEquals("123456", saved.getPassword());
            assertTrue(passwordEncoder.matches("123456", saved.getPassword()));
            return saved;
        });

        service.create(user);

        verify(repository).save(any(Users.class));
    }

    @Test
    void authenticateMatchesPasswordByUsernameOrEmail() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 4, 65 * 1024, 3);
        UsersService service = new UsersService(repository, passwordEncoder);

        Users user = new Users();
        user.setId(1L);
        user.setFullname("Ana Souza");
        user.setUsername("ana.souza");
        user.setEmail("ana@example.com");
        user.setPassword(passwordEncoder.encode("123456"));

        when(repository.findByUsernameOrEmail("ana.souza")).thenReturn(java.util.Optional.of(user));
        when(repository.findByUsernameOrEmail("ana@example.com")).thenReturn(java.util.Optional.of(user));

        assertTrue(service.authenticate("ana.souza", "123456").isPresent());
        assertFalse(service.authenticate("ana.souza", "wrong-password").isPresent());
        assertTrue(service.authenticate("ana@example.com", "123456").isPresent());
    }
}
