package net.tylerwade.backend.repository;

import jakarta.transaction.Transactional;
import net.tylerwade.backend.model.entity.alert.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT alert FROM Alert alert WHERE alert.app.id = ?1 ORDER BY alert.apiCall.timestamp DESC")
    List<Alert> findByAppId(String appId);

    @Modifying
    @Transactional
    void deleteAlertsByAppId(String appId);

}
