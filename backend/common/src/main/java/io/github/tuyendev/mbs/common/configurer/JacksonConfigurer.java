package io.github.tuyendev.mbs.common.configurer;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * From Jhipster auto-generated configuration
 */
@Configuration
class JacksonConfigurer {

	/**
	 * Support for Java date and time API.
	 *
	 * @return the corresponding Jackson module.
	 */
	@Bean
	public JavaTimeModule javaTimeModule() {
		return new JavaTimeModule();
	}

	@Bean
	public Jdk8Module jdk8TimeModule() {
		return new Jdk8Module();
	}

	/*
	 * Module for serialization/deserialization of RFC7807 Problem.
	 */
	@Bean
	public ProblemModule problemModule() {
		return new ProblemModule();
	}

	/*
	 * Module for serialization/deserialization of ConstraintViolationProblem.
	 */
	@Bean
	public ConstraintViolationProblemModule constraintViolationProblemModule() {
		return new ConstraintViolationProblemModule();
	}
}
