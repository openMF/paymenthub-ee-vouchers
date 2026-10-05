package org.mifos.pheevouchermanagementsystem.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import java.time.Duration;
import org.mifos.pheevouchermanagementsystem.config.ZeebeProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeClientConfiguration {

    private final ZeebeProperties zeebeProperties;

    public ZeebeClientConfiguration(ZeebeProperties zeebeProperties) {
        this.zeebeProperties = zeebeProperties;
    }

    @Bean(destroyMethod = "close")
    public ZeebeClient setup() {
        return ZeebeClient.newClientBuilder().gatewayAddress(zeebeProperties.broker().contactpoint()).usePlaintext()
                .defaultJobPollInterval(Duration.ofMillis(zeebeProperties.client().pollInterval())).defaultJobWorkerMaxJobsActive(2000)
                .numJobWorkerExecutionThreads(zeebeProperties.client().maxExecutionThreads()).build();
    }
}
