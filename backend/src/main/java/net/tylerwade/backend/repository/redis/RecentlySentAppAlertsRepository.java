package net.tylerwade.backend.repository.redis;

import net.tylerwade.backend.model.entity.alert.RecentlySentAppAlerts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentlySentAppAlertsRepository extends CrudRepository<RecentlySentAppAlerts, String> {

}
