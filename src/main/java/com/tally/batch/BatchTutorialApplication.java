package com.tally.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BatchTutorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchTutorialApplication.class, args);
    }

}
