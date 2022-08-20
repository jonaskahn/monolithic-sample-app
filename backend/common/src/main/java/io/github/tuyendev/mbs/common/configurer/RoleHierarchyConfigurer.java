package io.github.tuyendev.mbs.common.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
class RoleHierarchyConfigurer {

	@Bean
	public RoleHierarchy roleHierarchy() {
		return new RoleHierarchyImpl();
	}
}
