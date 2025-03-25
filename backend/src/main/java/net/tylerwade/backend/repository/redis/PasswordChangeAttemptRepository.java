package net.tylerwade.backend.repository.redis;

import net.tylerwade.backend.model.entity.PasswordChangeAttempt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordChangeAttemptRepository extends CrudRepository<PasswordChangeAttempt, String> {

}
