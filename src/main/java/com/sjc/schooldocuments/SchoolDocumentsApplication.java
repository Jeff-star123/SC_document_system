package com.sjc.schooldocuments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchoolDocumentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolDocumentsApplication.class, args);
    }
}
