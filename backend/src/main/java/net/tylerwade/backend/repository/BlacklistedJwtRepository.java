package net.tylerwade.backend.repository;

import net.tylerwade.backend.entity.BlacklistedJwt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedJwtRepository extends CrudRepository<BlacklistedJwt, String> {

}
