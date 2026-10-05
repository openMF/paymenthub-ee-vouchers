package org.mifos.pheevouchermanagementsystem.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The party the voucher budget is drawn from. All three are required, as they were when they were bare {@code @Value}
 * fields.
 */
@Validated
@ConfigurationProperties(prefix = "payer")
public record PayerProperties(@NotNull String tenant, @NotNull String identifier, @NotNull String identifierType) {
}
