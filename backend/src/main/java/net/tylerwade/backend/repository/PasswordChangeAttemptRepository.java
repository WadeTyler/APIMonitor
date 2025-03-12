package net.tylerwade.backend.repository;

import jakarta.transaction.Transactional;
import net.tylerwade.backend.entity.PasswordChangeAttempt;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordChangeAttemptRepository extends CrudRepository<PasswordChangeAttempt, String> {

}
