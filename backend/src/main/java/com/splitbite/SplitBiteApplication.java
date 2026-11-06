package com.splitbite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SplitBiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SplitBiteApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" SplitBite Backend Server Started on Port 8080! ");
        System.out.println(" API Base URL: http://localhost:8080/api/bills   ");
        System.out.println("=================================================");
    }
}
