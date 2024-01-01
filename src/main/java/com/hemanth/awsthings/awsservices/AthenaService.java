package com.hemanth.awsthings.awsservices;


import com.hemanth.awsthings.model.UserDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionStatus;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AthenaService {



    private final AthenaClient athenaClient;

    private final S3Client s3Client;

    private final String s3BucketName = System.getenv("S3_BUCKET_NAME");

    public AthenaService(){

        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials
                .create(System.getenv("AWS_ACCESS_KEY_ID"),System.getenv("AWS_SECRET_ACCESS_KEY"));

        this.athenaClient = AthenaClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.AP_SOUTH_1)
                .build();

        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.AP_SOUTH_1)
                .build();
    }
    public List<UserDetails> startAthenaQuery(String query) throws InterruptedException {
        QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                .database("netflix_data_db")
                .build();

        ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                .outputLocation("s3://hemanthcse1-store-json-data/athena-query-results/")
                .build();

        StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                .queryString(query)
                .queryExecutionContext(queryExecutionContext)
                .resultConfiguration(resultConfiguration)
                .build();

        StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);

        String executionId = startQueryExecutionResponse.queryExecutionId();
        return waitForQueryToComplete(executionId);
    }

    public List<UserDetails> waitForQueryToComplete(String queryExecutionId) throws InterruptedException {
        List<UserDetails> result = new ArrayList<>();

        boolean isQueryStillRunning = true;
        String queryState = "";

        while (isQueryStillRunning) {
            GetQueryExecutionResponse getQueryExecutionResponse = athenaClient.getQueryExecution(
                    GetQueryExecutionRequest.builder()
                            .queryExecutionId(queryExecutionId)
                            .build());

            queryState = getQueryExecutionResponse.queryExecution().status().state().toString();



            switch (queryState) {
                case "SUCCEEDED":
                    isQueryStillRunning = false;
                   result = getQueryResults(queryExecutionId);
                    break;
                case "FAILED":
                case "CANCELLED":
                    throw new RuntimeException("Query Failed or was Cancelled. State: " + queryState);
                default:
                    // Wait for a short time before checking the status again
                    Thread.sleep(3000);
                    break;
            }
        }

        return result;
    }

    public List<UserDetails> getQueryResults(String queryExecutionId) {

        String keyPrefix = "athena-query-results/";
        String fileName = queryExecutionId + ".csv"; // Adjust the file extension if needed

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3BucketName)
                .key(keyPrefix + fileName)
                .build();


        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);

        List<UserDetails> userDetailsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(object, StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            for (CSVRecord record : csvParser) {
                UserDetails userDetails = new UserDetails();
                userDetails.setFirstName(record.get("firstName"));
                userDetails.setLastName(record.get("lastName"));
                userDetails.setEmail(record.get("email"));
                userDetails.setMobile(record.get("mobile"));
                userDetails.setGender(record.get("gender"));
                userDetails.setCity(record.get("city"));
                userDetails.setState(record.get("state"));
                userDetails.setCountry(record.get("country"));
                userDetails.setPincode(record.get("pincode"));
                userDetailsList.add(userDetails);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading and parsing query results", e);
        }

        return userDetailsList;
    }



}
