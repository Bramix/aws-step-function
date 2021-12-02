package br.com.zup;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.amazonaws.services.stepfunctions.model.StopExecutionRequest;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.util.UUID;

public class StepFunctionsSample {

    private static final String STATE_MACHINE_ARN = "arn:aws:states:us-east-2:282913325951:stateMachine:testDelayStepFunction";
    private static final String TIME_INPUT_FIELD_NAME = "timeInput";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z";
    private static final String MOCK_FEATURE_EXECUTION_DATE = "2022-11-07T17:02:23Z";

    private static final ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
    private static final AWSStepFunctions sfnClient;

    static {
        credentialsProvider.getCredentials();
        sfnClient = AWSStepFunctionsClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public static void main(String[] args) throws InterruptedException {
        var sfnInput = new JSONObject();
        var date = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(MOCK_FEATURE_EXECUTION_DATE);
        sfnInput.put(TIME_INPUT_FIELD_NAME, date);

        var executionArn = executeStepFunction(sfnInput);
        Thread.sleep(10000);
        stopExecutionOfStepFunction(executionArn);
    }

    private static String executeStepFunction(JSONObject jsonObject) {

        var startExecutionRequest = new StartExecutionRequest()
                .withStateMachineArn(STATE_MACHINE_ARN)
                .withName(UUID.randomUUID().toString())
                .withInput(jsonObject.toString());

        StartExecutionResult result = sfnClient.startExecution(startExecutionRequest);

        return result.getExecutionArn();
    }


    private static void stopExecutionOfStepFunction(String executionArn) {
        var stopExecutionRequest = new StopExecutionRequest().withExecutionArn(executionArn);
        sfnClient.stopExecution(stopExecutionRequest);
    }

    private static void editExecutionOfStepFunction(String executionArn, JSONObject jsonObject) {
        stopExecutionOfStepFunction(executionArn);
        executeStepFunction(jsonObject);
    }

}
