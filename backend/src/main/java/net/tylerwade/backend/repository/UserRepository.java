package net.tylerwade.backend.repository;

import net.tylerwade.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u WHERE u.id = (SELECT a.userId FROM Application a WHERE a.id = ?1)")
    Optional<User> findByAppId(String appId);
}
