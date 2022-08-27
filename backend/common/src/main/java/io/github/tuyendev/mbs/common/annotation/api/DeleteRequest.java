package io.github.tuyendev.mbs.common.annotation.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.*;

/**
 * Annotation for mapping HTTP {@code DELETE} requests onto specific handler
 * methods.
 *
 * <p>Specifically, {@code @DeleteMapping} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @RequestMapping(method = RequestMethod.DELETE)}.
 *
 * @author Sam Brannen
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see PatchMapping
 * @see RequestMapping
 * @since 4.3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.DELETE)
@Operation
public @interface DeleteRequest {

    /**
     * Alias for {@link RequestMapping#name}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String name() default "";

    /**
     * Alias for {@link RequestMapping#value}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    /**
     * Alias for {@link RequestMapping#path}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};

    /**
     * Alias for {@link RequestMapping#params}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] params() default {};

    /**
     * Alias for {@link RequestMapping#headers}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] headers() default {};

    /**
     * Alias for {@link RequestMapping#consumes}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] consumes() default {};

    /**
     * Alias for {@link RequestMapping#produces}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] produces() default {};

    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";

    @AliasFor(annotation = Operation.class, attribute = "description")
    String desc() default "";

}
