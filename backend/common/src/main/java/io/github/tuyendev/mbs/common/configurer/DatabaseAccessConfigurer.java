package io.github.tuyendev.mbs.common.configurer;

import java.util.Objects;
import java.util.Optional;

import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import io.github.tuyendev.mbs.common.utils.AppContextUtils;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.relational.core.mapping.event.AfterConvertEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EntityScan(value = {"io.github.tuyendev.mbs.*.entity.rdb", "io.github.tuyendev.mbs.*.entity.mongodb"})
@EnableJdbcRepositories(value = {"io.github.tuyendev.mbs.*.repository.rdb"})
@EnableJdbcAuditing(auditorAwareRef = "auditorProvider")
@EnableMongoRepositories({"io.github.tuyendev.mbs.*.repository.mongodb"})
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
class DatabaseAccessConfigurer {
	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new DomainAuditorAware();
	}

	@Bean
	ApplicationListener<AfterSaveEvent<Object>> beforeSaveManualPersistable() {
		return event -> {
			Object e = event.getEntity();
			if (e instanceof ManualPersistable) {
				((ManualPersistable<?>) e).setNewEntity(Boolean.FALSE);
			}
		};
	}

	@Bean
	ApplicationListener<AfterConvertEvent<Object>> afterLoadManualPersistable() {
		return event -> {
			Object e = event.getEntity();
			if (e instanceof ManualPersistable) {
				((ManualPersistable<?>) e).setNewEntity(Boolean.FALSE);
			}
		};
	}


	static class DomainAuditorAware implements AuditorAware<Long> {
		@Override
		public Optional<Long> getCurrentAuditor() {
			Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext())
					.map(SecurityContext::getAuthentication);
			if (authentication.isPresent() && Objects.equals("anonymousUser", authentication.get().getPrincipal())) {
				return Optional.of(0L);
			}
			return AppContextUtils.getCurrentLoginUserId();
		}
	}

}
