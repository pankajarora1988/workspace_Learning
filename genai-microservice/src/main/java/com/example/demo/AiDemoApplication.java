package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the GenAI Microservice.
 * This class initializes the Spring context and starts the embedded web server.
 */
@SpringBootApplication
public class AiDemoApplication {

    public static void main(String[] args) {
        // This line launches the entire Spring Boot framework
        SpringApplication.run(AiDemoApplication.class, args);
        
        System.out.println("--- AI Microservice is running and ready for prompts! ---");
    }

}