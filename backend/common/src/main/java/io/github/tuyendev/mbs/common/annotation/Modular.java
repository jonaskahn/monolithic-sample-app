package io.github.tuyendev.mbs.common.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

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