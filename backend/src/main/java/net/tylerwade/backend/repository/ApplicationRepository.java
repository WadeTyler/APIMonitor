package net.tylerwade.backend.repository;

import net.tylerwade.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    boolean existsByNameAndUserIdIgnoreCase(String name, String userId);

    List<Application> findAllByUserId(String userId);

    Optional<Application> findByPublicToken(String publicToken);

}
