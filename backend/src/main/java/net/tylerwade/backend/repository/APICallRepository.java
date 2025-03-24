package net.tylerwade.backend.repository;

import jakarta.transaction.Transactional;
import net.tylerwade.backend.model.entity.APICall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface APICallRepository extends JpaRepository<APICall, Long> {


    Page<APICall> findAllByAppId(String appId, Pageable pageable);
    Page<APICall> findAllByAppIdAndPathContainingIgnoreCaseOrMethodContainingIgnoreCase(String appId, String search, String search2, Pageable pageable);

    Page<APICall> findAllByAppIdAndPathContainingIgnoreCaseOrMethodContainingIgnoreCaseOrResponseStatusEquals(String appId, String search, String search2, Integer search3, Pageable pageable);


    @Query("select count(distinct a.remoteAddress) from APICall a WHERE a.appId = ?1")
    Long countDistinctAPICallByRemoteAddressAndAppIdEquals(String appId);

    @Query("select count(distinct a) from APICall a WHERE a.appId = ?1")
    Long countDistinctAPICallByAppId(String appId);

    @Query("select count(a) from APICall a where a.appId = ?1")
    Long countAPICallByAppId(String appId);


    @Query("SELECT a.id FROM APICall a WHERE a.appId = ?1 ORDER BY a.timestamp ASC LIMIT ?2")
    List<Long> findTopIdsByIdOrderByTimestampAsc(String appId, int top);


    @Modifying
    @Transactional
    void deleteAllByAppId(String appId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM APICall a WHERE a.id IN ?1")
    void deleteByIds(List<Long> ids);

    @Query("select distinct a.path from APICall a WHERE a.appId = ?1")
    String[] findDistinctPathByAppId(String appId);

    @Query("select distinct a.method from APICall a where a.appId = ?1")
    String[] findDistinctMethodByAppId(String appId);

    @Query("select count(a.method) from APICall a where a.method = ?1 and a.appId = ?2")
    Long countMethodByAndAppIdEquals(String method, String appId);
}
