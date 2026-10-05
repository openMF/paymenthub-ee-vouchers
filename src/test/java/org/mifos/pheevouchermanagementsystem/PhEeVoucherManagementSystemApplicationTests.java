package org.mifos.pheevouchermanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Starts the whole application context.
 *
 * <p>
 * This test existed already and failed everywhere, because application.yml points the datasource at a hostname that
 * only resolves inside the cluster. The {@code test} profile replaces that with an in-memory database, which is what
 * the h2 dependency in build.gradle was evidently meant for. CI never reported it because it runs checkstyleMain and
 * bootJar and never runs the tests.
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest
class PhEeVoucherManagementSystemApplicationTests {

    @Test
    void contextLoads() {}

}
