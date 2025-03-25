package net.tylerwade.backend.repository.redis;

import net.tylerwade.backend.model.entity.DeleteAccountVerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteAccountCodeRepository extends CrudRepository<DeleteAccountVerificationCode, String> {
}
