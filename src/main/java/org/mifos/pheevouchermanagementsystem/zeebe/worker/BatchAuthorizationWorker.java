package org.mifos.pheevouchermanagementsystem.zeebe.worker;

import static org.mifos.pheevouchermanagementsystem.zeebe.ZeebeVariables.CACHED_TRANSACTION_ID;
import static org.mifos.pheevouchermanagementsystem.zeebe.worker.Worker.BATCH_AUTH;

import io.camunda.zeebe.client.ZeebeClient;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.mifos.pheevouchermanagementsystem.config.PayerProperties;
import org.mifos.pheevouchermanagementsystem.config.VoucherProperties;
import org.mifos.pheevouchermanagementsystem.config.ZeebeProperties;
import org.mifos.pheevouchermanagementsystem.data.AuthorizationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchAuthorizationWorker extends BaseWorker {

    @Autowired
    private ZeebeClient zeebeClient;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private CamelContext camelContext;
    @Autowired
    private ZeebeProperties zeebeProperties;
    // Left as @Value on purpose. mock_schema.endpoints.batch_auth is appended to in the handler below, which is a
    // defect (the field grows with every batch). Binding it into a record would make the field immutable and fix
    // that silently, and it deserves its own ticket and its own test.
    @Value("${mock_schema.hostname}")
    private String mockSchemaHostname;
    @Value("${mock_schema.endpoints.batch_auth}")
    private String batchAuthEndpoint;
    @Autowired
    private VoucherProperties voucherProperties;
    @Autowired
    private PayerProperties payerProperties;

    @Override
    public void setup() {
        logger.info("## generating " + BATCH_AUTH.getValue() + "zeebe worker");
        zeebeClient.newWorker().jobType(BATCH_AUTH.getValue()).handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> existingVariables = job.getVariablesAsMap();
            existingVariables.put(CACHED_TRANSACTION_ID, job.getKey());
            RequestSpecification requestSpec = new RequestSpecBuilder().build();
            requestSpec.relaxedHTTPSValidation();
            requestSpec.header("X-Client-Correlation-ID", job.getKey());
            requestSpec.header("Content-Type", "application/json");
            requestSpec.header("X-CallbackURL", voucherProperties.hostname() + "/authorization/callbacks");
            requestSpec.queryParam("command", "authorize");
            batchAuthEndpoint = batchAuthEndpoint + existingVariables.get("batchId").toString();
            AuthorizationRequest authorizationRequest = new AuthorizationRequest();
            authorizationRequest.setBatchId(existingVariables.get("batchId").toString());
            authorizationRequest.setPayerIdentifier(payerProperties.identifier());
            String totalAmount = existingVariables.get("totalAmount").toString();
            authorizationRequest.setAmount(new BigDecimal(totalAmount));
            authorizationRequest.setCurrency(existingVariables.get("currency").toString());

            String response = RestAssured.given(requestSpec).baseUri(mockSchemaHostname).body(authorizationRequest).expect()
                    .spec(new ResponseSpecBuilder().build()).when().post(batchAuthEndpoint).andReturn().asString();

            client.newCompleteCommand(job.getKey()).variables(existingVariables).send().join();
        }).name(BATCH_AUTH.getValue()).maxJobsActive(zeebeProperties.client().evenlyAllocatedMaxJobs()).open();
    }
}
