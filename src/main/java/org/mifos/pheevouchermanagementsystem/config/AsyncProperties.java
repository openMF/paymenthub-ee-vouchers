package org.mifos.pheevouchermanagementsystem.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Sizing of the executor behind the {@code asyncExecutor} bean. All three are required, as they were when they were
 * bare {@code @Value} fields.
 */
@Validated
@ConfigurationProperties(prefix = "async")
public record AsyncProperties(@NotNull Integer corePoolSize, @NotNull Integer maxPoolSize, @NotNull Integer queueCapacity) {
}
