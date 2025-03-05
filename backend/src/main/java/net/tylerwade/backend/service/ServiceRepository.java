package net.tylerwade.backend.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    boolean existsByNameAndUserIdIgnoreCase(String name, String userId);

    List<Service> findAllByUserId(String userId);

}
