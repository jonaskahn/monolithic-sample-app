package io.github.tuyendev.mbs.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Configuration
@ComponentScan
public @interface Modular {

	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String[] value() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "value")
	String[] basePackages() default {};

	@AliasFor(annotation = ComponentScan.class)
	Class<?>[] basePackageClasses() default {};

	@AliasFor(annotation = ComponentScan.class)
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	@AliasFor(annotation = ComponentScan.class)
	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

	@AliasFor(annotation = ComponentScan.class)
	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

	@AliasFor(annotation = ComponentScan.class)
	String resourcePattern() default "**/*.class";

	@AliasFor(annotation = ComponentScan.class)
	boolean useDefaultFilters() default true;

	@AliasFor(annotation = ComponentScan.class)
	ComponentScan.Filter[] includeFilters() default {};

	@AliasFor(annotation = ComponentScan.class)
	ComponentScan.Filter[] excludeFilters() default {};

	@AliasFor(annotation = ComponentScan.class)
	boolean lazyInit() default false;
}