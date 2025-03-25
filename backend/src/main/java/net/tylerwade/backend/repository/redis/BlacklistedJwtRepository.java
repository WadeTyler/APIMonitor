package net.tylerwade.backend.repository.redis;

import net.tylerwade.backend.model.entity.BlacklistedJwt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedJwtRepository extends CrudRepository<BlacklistedJwt, String> {

}
