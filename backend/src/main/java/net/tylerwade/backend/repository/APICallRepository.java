package net.tylerwade.backend.repository;

import jakarta.transaction.Transactional;
import net.tylerwade.backend.entity.APICall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface APICallRepository extends JpaRepository<APICall, Long> {


    Page<APICall> findAllByAppId(String appId, Pageable pageable);

    @Modifying
    @Transactional
    void deleteAllByAppId(String appId);
}
