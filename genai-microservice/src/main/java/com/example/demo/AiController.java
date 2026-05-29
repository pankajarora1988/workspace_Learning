package com.example.demo;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

    private final OllamaChatClient chatClient;

    public AiController(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/test")
    public String test(@RequestParam(value = "message") String message) {
        return "Test Message -" + message;
    }
    
    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message") String message) {
        return chatClient.call(message);
    }
    
    @GetMapping("/ai/prompt")
    public String askAi(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        // This calls your local Llama 3.2 model via Ollama
        return chatClient.call(message);
    }
}