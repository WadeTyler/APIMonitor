package net.tylerwade.backend.repository;

import net.tylerwade.backend.model.entity.alert.AlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertConfigRepository extends JpaRepository<AlertConfig, Long> {

    Optional<AlertConfig> findByAppId(String appId);
}
