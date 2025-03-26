package net.tylerwade.backend.repository;

import jakarta.transaction.Transactional;
import net.tylerwade.backend.model.entity.alert.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    Page<Alert> findByAppId(String appId, Pageable pageable);

    @Modifying
    @Transactional
    void deleteAlertsByAppId(String appId);

    Long countDistinctByAppId(String appId);
}
