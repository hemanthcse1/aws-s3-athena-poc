package com.hemanth.awsthings.awsservices;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;

@Service
public class AthenaService {

    private final AthenaClient athenaClient;

    public AthenaService(){
        this.athenaClient = AthenaClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.AP_SOUTH_1)
                .build();
    }


}
