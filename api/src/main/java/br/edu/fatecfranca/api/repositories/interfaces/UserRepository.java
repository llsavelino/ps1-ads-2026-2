package br.edu.fatecfranca.api.repositories.interfaces;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.edu.fatecfranca.api.entities.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.username = :username")
    Optional<Users> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM Users u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<Users> findByUsernameOrEmail(@Param("identifier") String identifier);


    @Query("SELECT COUNT(u) > 0 FROM Users u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Optional<Users> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM Users u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);
}