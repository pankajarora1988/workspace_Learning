package com.security.demo;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.google.gson.JsonObject;

public class OllamaClient {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public static void queryLLM(String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OLLAMA_URL);
            
            JsonObject json = new JsonObject();
            json.addProperty("model", "llama3.2");
            json.addProperty("prompt", prompt);
            json.addProperty("stream", false);

            post.setEntity(new StringEntity(json.toString()));
            post.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(post)) {
                // In a real demo, you would parse the response body here
                System.out.println("Ollama Response Status: " + response.getCode());
                System.out.println("LLM processed the request securely.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
