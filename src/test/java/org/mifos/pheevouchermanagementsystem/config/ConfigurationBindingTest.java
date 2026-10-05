package org.mifos.pheevouchermanagementsystem.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

/**
 * Binds every properties record from the exact keys application.yml uses, and from the environment variable spellings a
 * deployment would use for them.
 *
 * <p>
 * Moving a property from {@code @Value} to {@code @ConfigurationProperties} changes how its name resolves, and several
 * of these names are awkward: {@code async.core_pool_size} has underscores inside a dotted key,
 * {@code identity-account-mapper} has dashes in the prefix, and {@code payer.identifierType} has a capital letter in
 * the middle. Each one is pinned here so a rename cannot slip through.
 * </p>
 *
 * <p>
 * There is no running pod to check these against: the CR for this component is disabled on gazelle3. The CR in
 * mifos-gazelle sets only a few of these keys (the three hostnames and the Zeebe contact point), so for everything else
 * the file values are the contract, and this test is what stands between a rename and a value that silently stops
 * arriving.
 * </p>
 */
class ConfigurationBindingTest {

    /** The values in src/main/resources/application.yml, written here with the exact keys that file uses. */
    private static Map<String, Object> fileDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("zeebe.broker.contactpoint", "localhost:26500");
        defaults.put("zeebe.client.max-execution-threads", 50);
        defaults.put("zeebe.client.evenly-allocated-max-jobs", 1000);
        defaults.put("zeebe.client.poll-interval", 10);
        defaults.put("async.core_pool_size", 10);
        defaults.put("async.max_pool_size", 10);
        defaults.put("async.queue_capacity", 100);
        defaults.put("identity-account-mapper.hostname", "https://identity-mapper.sandbox.mifos.io/");
        defaults.put("voucher.hostname", "https://vouchers.sandbox.mifos.io");
        defaults.put("operations.hostname", "https://ops-bk.sandbox.mifos.io");
        defaults.put("operations.endpoints.transfers", "/api/v1/transfers?size=1&page=0");
        defaults.put("payer.tenant", "rhino");
        defaults.put("payer.identifier", "12345678");
        defaults.put("payer.identifierType", "MSISDN");
        return defaults;
    }

    /** Binds the way the application does: environment first, file defaults behind it. */
    private static <T> T bind(Map<String, Object> environmentVariables, String prefix, Class<T> type) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(
                new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environmentVariables));
        environment.getPropertySources().addLast(new MapPropertySource("file-defaults", fileDefaults()));

        return new Binder(ConfigurationPropertySources.get(environment)).bind(prefix, Bindable.of(type)).get();
    }

    @Test
    @DisplayName("the zeebe settings bind, including the one that four classes used to read separately")
    void zeebePropertiesBind() {
        ZeebeProperties properties = bind(Map.of(), "zeebe", ZeebeProperties.class);

        assertThat(properties.broker().contactpoint()).isEqualTo("localhost:26500");
        assertThat(properties.client().maxExecutionThreads()).isEqualTo(50);
        assertThat(properties.client().pollInterval()).isEqualTo(10);
        assertThat(properties.client().evenlyAllocatedMaxJobs()).isEqualTo(1000);
    }

    @Test
    @DisplayName("ZEEBE_BROKER_CONTACTPOINT overrides the file, which is how any deployment would set it")
    void zeebeContactpointCanBeOverriddenFromTheEnvironment() {
        assertThat(bind(Map.of("ZEEBE_BROKER_CONTACTPOINT", "paymenthub-infra-zeebe-gateway:26500"), "zeebe", ZeebeProperties.class)
                .broker().contactpoint()).isEqualTo("paymenthub-infra-zeebe-gateway:26500");
    }

    @Test
    @DisplayName("the async pool sizes bind although the keys use underscores")
    void asyncPropertiesBind() {
        AsyncProperties properties = bind(Map.of(), "async", AsyncProperties.class);

        assertThat(properties.corePoolSize()).isEqualTo(10);
        assertThat(properties.maxPoolSize()).isEqualTo(10);
        assertThat(properties.queueCapacity()).isEqualTo(100);
    }

    @Test
    @DisplayName("payer.identifierType binds although it has a capital letter in the middle")
    void payerPropertiesBind() {
        PayerProperties properties = bind(Map.of(), "payer", PayerProperties.class);

        assertThat(properties.tenant()).isEqualTo("rhino");
        assertThat(properties.identifier()).isEqualTo("12345678");
        assertThat(properties.identifierType()).isEqualTo("MSISDN");
    }

    @Test
    @DisplayName("the prefix with dashes in it still binds, from the file and from an environment variable")
    void identityAccountMapperPropertiesBind() {
        assertThat(bind(Map.of(), "identity-account-mapper", IdentityAccountMapperProperties.class).hostname())
                .isEqualTo("https://identity-mapper.sandbox.mifos.io/");
        assertThat(bind(Map.of("IDENTITY_ACCOUNT_MAPPER_HOSTNAME", "http://account-mapper:80"), "identity-account-mapper",
                IdentityAccountMapperProperties.class).hostname()).isEqualTo("http://account-mapper:80");
    }

    @Test
    @DisplayName("the operations URL is assembled from the same two parts as before")
    void operationsPropertiesBind() {
        OperationsApiProperties properties = bind(Map.of(), "operations", OperationsApiProperties.class);

        assertThat(properties.hostname()).isEqualTo("https://ops-bk.sandbox.mifos.io");
        assertThat(properties.endpoints().transfers()).isEqualTo("/api/v1/transfers?size=1&page=0");
    }

    @Test
    @DisplayName("this service's own hostname binds")
    void voucherPropertiesBind() {
        assertThat(bind(Map.of(), "voucher", VoucherProperties.class).hostname()).isEqualTo("https://vouchers.sandbox.mifos.io");
    }

    @Test
    @DisplayName("a pool size that is not a number is refused at binding time, naming the property")
    void malformedPoolSizeIsRefused() {
        assertThatThrownBy(() -> bind(Map.of("ASYNC_CORE_POOL_SIZE", "ten"), "async", AsyncProperties.class))
                .hasStackTraceContaining("async.core-pool-size");
    }
}
