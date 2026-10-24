package org.mifos.pheevouchermanagementsystem.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(SendCallbackService.class);

    /**
     * PUTs the body to the callback URL. A failure is logged and reported through the return value rather than thrown:
     * it used to escape into whichever Zeebe job handler or service called this, and a client that cannot be reached is
     * no reason to fail the voucher work.
     *
     * @return true if the callback was answered with a 2xx status
     */
    public boolean sendCallback(String body, String callbackURL) {
        try {
            Response response = RestAssured.given().baseUri(callbackURL).header("Content-Type", ContentType.JSON).body(body).when().put();
            int status = response.getStatusCode();
            if (status >= 200 && status < 300) {
                logger.info("Callback sent to {}, response status {}", callbackURL, status);
                return true;
            }
            logger.warn("Callback to {} was answered {}", callbackURL, status);
            return false;
        } catch (Exception e) { // RestAssured rethrows checked exceptions such as ConnectException undeclared
            logger.error("Callback to {} failed: {}", callbackURL, e.getMessage(), e);
            return false;
        }
    }

}
