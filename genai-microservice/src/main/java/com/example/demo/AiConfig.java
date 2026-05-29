package com.example.demo;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class AiConfig {
    public OllamaChatModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.7) // Controls creativity (0.0 is precise, 1.0 is creative)
                .build();
    }
}