package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

public class ImageToImageProcessor {

    public static void main(String[] args) {
        
        // 1. INPUT CONFIGURATION (Verify these file paths exist on your local disk)
        List<String> imagePaths = List.of("C:/Users/panka/Desktop/Vanya600.jpg");
        String transformationPrompt = "Blend the concepts of these images together, but render the result in a futuristic Cyberpunk artistic style.";
        String outputImagePath = "C:/Users/panka/Desktop/generated_output.png";

        // CRITICAL Paths for executing your embedded Python configuration environment
        String pythonExecutable = "C:/DATA_FOLDER/Dev/Jar_And_Lib/Fooocus_win64_2-5-0/python_embeded/python.exe";
        String bridgeScriptPath = "C:/DATA_FOLDER/Dev/Jar_And_Lib/Fooocus_win64_2-5-0/generate_bridge.py";

        System.out.println("Phase 1: Analysing input images with Gemma 3 locally via Ollama...");
        
        // 2. OLLAMA CONFIGURATION (Set with a generous 20-minute patience threshold for local CPU processing)
        ChatLanguageModel visionModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("gemma3:12b")
                .temperature(0.2)
                .timeout(Duration.ofMinutes(20)) 
                .build();

        // 3. BUILD MULTI-MODAL CONTENT PAYLOAD FOR OLLAMA
        List<dev.langchain4j.data.message.Content> contents = new ArrayList<>();
        String instruction = "Analyze the provided image(s). Based on their contents and the user request: '" 
                + transformationPrompt + "', write a highly descriptive, single-paragraph image generation prompt. "
                + "Only return the raw prompt text, do not write introductions or explanations.";
        
        contents.add(TextContent.from(instruction));

        for (String path : imagePaths) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                try {
                    byte[] bytes = Files.readAllBytes(imgFile.toPath());
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    String mimeType = Files.probeContentType(imgFile.toPath());
                    contents.add(ImageContent.from(base64, mimeType != null ? mimeType : "image/jpeg"));
                } catch (IOException e) {
                    System.err.println("Could not read image binary data: " + path);
                }
            } else {
                System.err.println("CRITICAL ERROR: Input image not found at: " + path);
                return;
            }
        }

        // Execute local visual inference via Ollama
        Response<AiMessage> visionResponse = visionModel.generate(UserMessage.from(contents));
        String optimizedPrompt = visionResponse.content().text().trim();
        
        System.out.println("\n[Gemma 3 Generated Prompt]: " + optimizedPrompt);
        System.out.println("\nPhase 2: Invoking Fooocus Python Bridge Engine (CPU Mode)...");

        // 4. CALL EMBEDDED PYTHON PROCESS VIA OS RUNTIME
        ProcessBuilder pb = new ProcessBuilder(
            pythonExecutable, 
            bridgeScriptPath, 
            optimizedPrompt, 
            outputImagePath
        );
        
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            
            // Stream terminal updates from your running CPU engine to the Java Console log
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                String generatedTempFilePath = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Fooocus Engine Log] " + line);
                    
                    // Intercept the parsed success identifier path keyword
                    if (line.contains("SUCCESS_PATH:")) {
                        generatedTempFilePath = line.split("SUCCESS_PATH:")[1].trim();
                    }
                }
                
                int exitCode = process.waitFor();
                if (exitCode == 0 && generatedTempFilePath != null) {
                    File renderedFile = new File(generatedTempFilePath);
                    File destinationFile = new File(outputImagePath);
                    
                    if (renderedFile.exists()) {
                        Files.copy(renderedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("\n=======================================================");
                        System.out.println("SUCCESS! Your locally rendered image has been saved to:");
                        System.out.println(outputImagePath);
                        System.out.println("=======================================================");
                    }
                } else {
                    System.err.println("\nImage rendering engine execution finished without capturing output location paths. Exit Code: " + exitCode);
                }
            }
        } catch (Exception e) {
            System.err.println("Execution failed during local image generation bridge execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}