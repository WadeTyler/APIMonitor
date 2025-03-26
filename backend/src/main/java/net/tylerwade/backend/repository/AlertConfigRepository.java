package net.tylerwade.backend.repository;

import net.tylerwade.backend.model.entity.Application;
import net.tylerwade.backend.model.entity.alert.AlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertConfigRepository extends JpaRepository<AlertConfig, Long> {

    @Query("SELECT ac FROM AlertConfig ac WHERE ac.app.id = ?1 AND ac.app.userId = ?2")
    Optional<AlertConfig> findByAppIdAndUserId(String appId, String userId);

    Optional<AlertConfig> findByApp(Application app);
}
