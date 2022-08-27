package io.github.tuyendev.mbs.common.configurer;

import io.github.tuyendev.mbs.common.annotation.context.MessageResourceClaim;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
class MessageSourceConfigurer {

    private final List<MessageResourceClaim> instructors;

    @Bean
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public MessageSource messageSource() {
        var sourcePaths = StreamEx.of(instructors)
                .map(MessageResourceClaim::messageSource)
                .flatMap(Arrays::stream)
                .toArray(String.class);

        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(sourcePaths);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

}
