package io.github.tuyendev.mbs.common.response;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.tuyendev.mbs.common.utils.AppContextUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Base64Utils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class Metadata implements Serializable {

    private static Tracer tracer;

    static {
        tracer = AppContextUtils.getBean(Tracer.class);
    }

    private String timestamp;
    private String code;
    private String traceId;
    private String reportId;
    @JsonIgnore
    private String stackTrace;

    public static Metadata successBlock() {
        return Metadata.builder()
                .timestamp(now())
                .traceId(getTracerId())
                .build();
    }

    private static String now() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static String getTracerId() {
        return Optional.ofNullable(tracer)
                .map(Tracer::currentSpan)
                .map(Span::context)
                .map(TraceContext::traceIdString)
                .orElseGet(() -> {
                    log.warn("No span found");
                    return Strings.EMPTY;
                });
    }

    public static Metadata errorBlock(Throwable e) {
        Metadata metadata = Metadata.builder()
                .timestamp(now())
                .traceId(getTracerId())
                .reportId(generateReportId())
                .stackTrace(ExceptionUtils.getStackTrace(e))
                .build();
        log.error("Got an exception, details:", e);
        return metadata;
    }

    private static String generateReportId() {
        return Base64Utils.encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return getSummary();
    }

    private String getSummary() {
        return "\n" + reportId + " - " + stackTrace;
    }
}
