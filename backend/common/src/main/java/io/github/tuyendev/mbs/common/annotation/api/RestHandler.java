package io.github.tuyendev.mbs.common.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Tag(name = "")
@RestController
@RequestMapping
public @interface RestHandler {

	@AliasFor(annotation = RestController.class, attribute = "value")
	String bean() default "";

	@AliasFor(annotation = Tag.class, attribute = "name")
	String name() default "";

	@AliasFor(annotation = Tag.class, attribute = "description")
	String desc() default "";

	@AliasFor(annotation = RequestMapping.class, attribute = "value")
	String[] path() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "method")
	RequestMethod[] method() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "params")
	String[] params() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "headers")
	String[] headers() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "consumes")
	String[] consumes() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "produces")
	String[] produces() default {};
}
