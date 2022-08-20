package io.github.tuyendev.mbs.common.utils;

import java.util.Optional;

import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.security.DomainUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AppContextUtils {

	private static ApplicationContext applicationContext;

	@Autowired
	public AppContextUtils(ApplicationContext applicationContext) {
		AppContextUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static Optional<User> getCurrentLoginUser() {
		return Optional.of(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getPrincipal)
				.map(DomainUserDetails.class::cast)
				.map(DomainUserDetails::getUser);
	}

	public static Optional<Long> getCurrentLoginUserId() {
		return getCurrentLoginUser()
				.map(User::getId);
	}

}