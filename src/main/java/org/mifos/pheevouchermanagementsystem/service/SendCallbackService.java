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

    public void sendCallback(String body, String callbackURL) {
        Response response = RestAssured.given().baseUri(callbackURL).header("Content-Type", ContentType.JSON).body(body).when().put();
        logger.info("Callback sent to {}, response status {}", callbackURL, response.getStatusCode());
    }

}
