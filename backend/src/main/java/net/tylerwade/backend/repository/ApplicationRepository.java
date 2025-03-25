package net.tylerwade.backend.repository;

import net.tylerwade.backend.model.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    boolean existsByNameAndUserIdIgnoreCase(String name, String userId);
    boolean existsByIdAndUserId(String id, String userId);

    Optional<Application> findByNameIgnoreCaseAndUserIdIgnoreCase(String name, String userId);

    Integer countByUserId(String userId);

    @Query("select a.id from Application a where a.userId = ?1")
    String[] findApplicationIdByUserId(String userId);

    List<Application> findAllByUserId(String userId);

    Optional<Application> findByPublicToken(String publicToken);

    Optional<Application> findByIdAndUserId(String appId, String userId);



}
