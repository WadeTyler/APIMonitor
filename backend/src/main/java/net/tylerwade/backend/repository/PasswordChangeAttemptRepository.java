package net.tylerwade.backend.repository;

import net.tylerwade.backend.entity.PasswordChangeAttempt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordChangeAttemptRepository extends CrudRepository<PasswordChangeAttempt, String> {

}
