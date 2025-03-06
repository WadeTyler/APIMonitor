package net.tylerwade.backend.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    boolean existsByNameAndUserIdIgnoreCase(String name, String userId);

    List<Service> findAllByUserId(String userId);

    Optional<Service> findByPublicToken(String publicToken);

}
