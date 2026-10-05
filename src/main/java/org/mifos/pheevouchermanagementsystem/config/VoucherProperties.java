package org.mifos.pheevouchermanagementsystem.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * This service's own address, which it hands to the payment scheme as the callback URL. Required, as it was when it was
 * a bare {@code @Value} field.
 */
@Validated
@ConfigurationProperties(prefix = "voucher")
public record VoucherProperties(@NotNull String hostname) {
}
