package org.mifos.pheevouchermanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan("org.mifos.pheevouchermanagementsystem.config")
@SuppressWarnings({ "HideUtilityClassConstructor" })
public class PhEeVoucherManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhEeVoucherManagementSystemApplication.class, args);
    }

}
