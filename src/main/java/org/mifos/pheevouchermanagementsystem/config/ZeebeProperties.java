package org.mifos.pheevouchermanagementsystem.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Where the Zeebe broker is and how hard to poll it. Every value is required, as it was when it was a bare
 * {@code @Value} field.
 */
@Validated
@ConfigurationProperties(prefix = "zeebe")
public record ZeebeProperties(@NotNull @Valid Broker broker, @NotNull @Valid Client client) {

    public record Broker(@NotNull String contactpoint) {
    }

    public record Client(@NotNull Integer maxExecutionThreads, @NotNull Integer pollInterval, @NotNull Integer evenlyAllocatedMaxJobs) {
    }
}
