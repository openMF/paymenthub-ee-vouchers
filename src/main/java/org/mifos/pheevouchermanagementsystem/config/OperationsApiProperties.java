package org.mifos.pheevouchermanagementsystem.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The operations API this connector polls for the transfer status of a redemption. Both values are required, as they
 * were when they were bare {@code @Value} fields.
 */
@Validated
@ConfigurationProperties(prefix = "operations")
public record OperationsApiProperties(@NotNull String hostname, @NotNull @Valid Endpoints endpoints) {

    public record Endpoints(@NotNull String transfers) {
    }
}
