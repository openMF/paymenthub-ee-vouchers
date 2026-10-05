package org.mifos.pheevouchermanagementsystem.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The account mapper this connector asks where a payee's money should go. Required, as it was when it was a bare
 * {@code @Value} field.
 */
@Validated
@ConfigurationProperties(prefix = "identity-account-mapper")
public record IdentityAccountMapperProperties(@NotNull String hostname) {
}
