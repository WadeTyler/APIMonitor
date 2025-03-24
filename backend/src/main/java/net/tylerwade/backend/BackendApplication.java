package net.tylerwade.backend;

import net.tylerwade.backend.config.properties.ApplicationProperties;
import net.tylerwade.backend.config.properties.JwtConfig;
import net.tylerwade.backend.config.properties.RedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_DEMAND, shadowCopy = RedisKeyValueAdapter.ShadowCopy.OFF)
@EnableScheduling
@EnableConfigurationProperties({JwtConfig.class, ApplicationProperties.class, RedisProperties.class})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
