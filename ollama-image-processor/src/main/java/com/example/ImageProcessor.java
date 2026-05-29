package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

public class ImageProcessor {

    public static void main(String[] args) {
        
        // 1. Point to your local image file (Change this path to a real image on your machine!)
        String imagePath = "C:/Users/panka/Desktop/Vanya600.jpg"; 
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists()) {
            System.err.println("Error: Image file not found at " + imagePath);
            return;
        }

        System.out.println("Initializing Ollama with gemma3:12b...");

        // 2. Build the Ollama client model connection
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434") // Default Ollama local address
                .modelName("gemma3:12b")
                .temperature(0.2)                  // Lower temperature means more factual output
                .timeout(Duration.ofMinutes(5))
                .build();

        try {
            // 3. Read image file and convert to Base64
            byte[] fileContent = Files.readAllBytes(imageFile.toPath());
            String base64Image = Base64.getEncoder().encodeToString(fileContent);

            // 4. Determine image mime-type dynamically
            String mimeType = Files.probeContentType(imageFile.toPath());
            if (mimeType == null) {
                mimeType = "image/jpeg"; // fallback standard
            }

            System.out.println("Processing image and sending request to Gemma 3...");

            // 5. Package the Text prompt and the Image content into a combined UserMessage
            UserMessage userMessage = UserMessage.from(
                    TextContent.from("Describe what you see in this image in detail, and list any prominent objects."),
                    ImageContent.from(base64Image, mimeType)
            );

            // 6. Execute the request
            Response<AiMessage> response = model.generate(userMessage);

            // 7. Output the Vision LLM results
            System.out.println("\n--- Model Response ---");
            System.out.println(response.content().text());
            System.out.println("----------------------");

        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred during AI inference: " + e.getMessage());
            e.printStackTrace();
        }
    }
}