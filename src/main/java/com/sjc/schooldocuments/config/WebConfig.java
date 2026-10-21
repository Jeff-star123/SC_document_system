package com.sjc.schooldocuments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers the empty-string-tolerant enum binding the request filters rely on.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EmptyTolerantEnumConverter<>(com.sjc.schooldocuments.request.RequestStatus.class) {
        });
        registry.addConverter(new EmptyTolerantEnumConverter<>(com.sjc.schooldocuments.request.ApprovalStatus.class) {
        });
        registry.addConverter(new EmptyTolerantEnumConverter<>(com.sjc.schooldocuments.user.Role.class) {
        });
        registry.addConverter(new EmptyTolerantEnumConverter<>(com.sjc.schooldocuments.user.AccountStatus.class) {
        });
    }

    /**
     * A blank form value binds to {@code null}; an unknown value also binds to
     * {@code null} rather than failing the whole request. Concrete anonymous
     * subclasses are registered above so the target enum type stays available
     * to Spring's conversion service.
     */
    private abstract static class EmptyTolerantEnumConverter<E extends Enum<E>> implements Converter<String, E> {

        private final Class<E> type;

        EmptyTolerantEnumConverter(Class<E> type) {
            this.type = type;
        }

        @Override
        public E convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }
            try {
                return Enum.valueOf(type, source.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }
}
