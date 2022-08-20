package io.github.tuyendev.mbs.common.thread;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class TaskContextHolderDecorator implements TaskDecorator {

	@Override
	public Runnable decorate(Runnable runnable) {
		var securityContext = SecurityContextHolder.getContext();
		var locale = LocaleContextHolder.getLocale();
		return () -> {
			try {
				SecurityContextHolder.setContext(securityContext);
				LocaleContextHolder.setLocale(locale, true);
				runnable.run();
			}
			catch (Throwable e) {
				log.error("Cannot execute with TaskContextHolderDecorator", e);
			}
		};
	}
}
