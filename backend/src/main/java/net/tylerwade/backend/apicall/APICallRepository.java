package net.tylerwade.backend.apicall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface APICallRepository extends JpaRepository<APICall, Long> {


    List<APICall> findAllByServiceId(String serviceId);
}
