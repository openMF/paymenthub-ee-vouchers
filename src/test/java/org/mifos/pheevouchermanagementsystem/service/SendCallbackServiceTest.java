package org.mifos.pheevouchermanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendCallbackServiceTest {

    private final SendCallbackService service = new SendCallbackService();
    private HttpServer server;
    private volatile int status = 200;

    @BeforeEach
    void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/callback", exchange -> {
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private String url() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/callback";
    }

    @Test
    void anAcceptedCallbackReportsTrue() {
        assertThat(service.sendCallback("{}", url())).isTrue();
    }

    @Test
    void aRejectedCallbackReportsFalse() {
        status = 500;
        assertThat(service.sendCallback("{}", url())).isFalse();
    }

    @Test
    void anUnreachableCallbackReportsFalseInsteadOfThrowing() throws Exception {
        int freePort;
        try (ServerSocket socket = new ServerSocket(0)) {
            freePort = socket.getLocalPort();
        }
        assertThat(service.sendCallback("{}", "http://127.0.0.1:" + freePort + "/callback")).isFalse();
    }
}
