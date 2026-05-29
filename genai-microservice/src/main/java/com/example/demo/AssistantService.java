package com.example.demo;

import dev.langchain4j.model.chat.ChatLanguageModel;

public class AssistantService {
    
    private final ChatLanguageModel model;

    public AssistantService() {
        // Initialize the model
        this.model = new AiConfig().chatModel();
    }

    public String getAiResponse(String userPrompt) {
        // This sends the request to http://localhost:11434/api/generate
        return model.generate(userPrompt);
    }
}