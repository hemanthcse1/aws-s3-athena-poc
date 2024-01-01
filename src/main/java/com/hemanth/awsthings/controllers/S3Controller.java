package com.hemanth.awsthings.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemanth.awsthings.awsservices.S3Service;
import com.hemanth.awsthings.model.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {


    private static Logger logger = LoggerFactory.getLogger(S3Controller.class);
    private final S3Service s3Service;


    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @PostMapping("/upload")
    public void uploadJsonToS3(@RequestBody CreateUserRequest createUserRequest) throws JsonProcessingException {

        logger.info("userDetails : {}", createUserRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        s3Service.uploadJsonToS3("user_data.txt", objectMapper.writeValueAsString(createUserRequest));

    }

    @PostMapping("/bulk-upload")
    public void bulkUploadUsers(@RequestBody List<CreateUserRequest> createUserRequest) throws JsonProcessingException {

        logger.info("userDetails : {}", createUserRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        s3Service.uploadJsonToS3("user_data.json", objectMapper.writeValueAsString(createUserRequest));

    }

    @GetMapping("/read")
    public String readDataFromS3(){
        return s3Service.readDataFromS3("user_data.json");
    }

}
