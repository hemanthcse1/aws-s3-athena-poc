package com.hemanth.awsthings.controllers;

import com.hemanth.awsthings.awsservices.AthenaService;
import com.hemanth.awsthings.model.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/athena")
public class AthenaController {
    private final AthenaService athenaService;



    public AthenaController(AthenaService athenaService) throws InterruptedException {
        this.athenaService = athenaService;
    }


    @GetMapping("/execute-query")
    public ResponseEntity<List<UserDetails>> executeQuery() throws InterruptedException {

        String query = "SELECT * FROM netflixuserdetails_json LIMIT 10;";

        List<UserDetails> userDetails = athenaService.startAthenaQuery(query);

        return ResponseEntity.ok(userDetails);
    }
}
