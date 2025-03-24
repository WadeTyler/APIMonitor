package net.tylerwade.backend.repository;

import net.tylerwade.backend.model.entity.SignupVerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupVerificationCodeRepository extends CrudRepository<SignupVerificationCode, String> {
}
