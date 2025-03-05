package net.tylerwade.backend.apicall;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface APICallRepository extends JpaRepository<APICall, Long> {


    List<APICall> findAllByServiceId(String serviceId);

    @Modifying
    @Transactional
    void deleteAllByServiceId(String serviceId);
}
