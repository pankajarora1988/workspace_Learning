package com.example.demo;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {

    @Bean
    public OllamaChatClient chatClient() {
        // Force a 5-minute timeout directly in the API client
        var api = new OllamaApi("http://localhost:11434");
        
        return new OllamaChatClient(api)
            .withDefaultOptions(OllamaOptions.create()
                .withModel("llama3.2")
                .withTemperature(0.7f));
    }
}