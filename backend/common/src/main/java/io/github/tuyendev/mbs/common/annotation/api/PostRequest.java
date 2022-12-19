package io.github.tuyendev.mbs.common.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.POST)
@Operation
public @interface PostRequest {

	@AliasFor(annotation = RequestMapping.class)
	String name() default "";

	@AliasFor(annotation = RequestMapping.class)
	String[] value() default {};

	@AliasFor(annotation = RequestMapping.class)
	String[] path() default {};

	@AliasFor(annotation = RequestMapping.class)
	String[] params() default {};

	@AliasFor(annotation = RequestMapping.class)
	String[] headers() default {};

	@AliasFor(annotation = RequestMapping.class)
	String[] consumes() default {};

	@AliasFor(annotation = RequestMapping.class)
	String[] produces() default {};

	@AliasFor(annotation = Operation.class, attribute = "summary")
	String summary() default "";

	@AliasFor(annotation = Operation.class, attribute = "description")
	String desc() default "";
}
