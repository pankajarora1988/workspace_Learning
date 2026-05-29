package com.portable.ai;

import static spark.Spark.*;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AiBridge {
	
	public static void main(String[] args) {
        port(8080);

        // Standard Text Routes
        post("/polish", (req, res) -> {
            return callLocalOllama("Polish this text: " + req.body());
        });

        post("/anytext", (req, res) -> {
            return callLocalOllama(req.body());
        });

        // New Service: Accept and Process Attachment
        post("/textwithattachment", (req, res) -> {
            // Configure Spark to handle multipart file uploads
            // (Max file size 5MB, stored in temporary directory)
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            
            try {
                Part filePart = req.raw().getPart("file"); // "file" is the key name in the upload
                try (InputStream is = filePart.getInputStream()) {
                    // Convert InputStream to String (the file content)
                    String fileContent = new Scanner(is).useDelimiter("\\A").next();
                    return callLocalOllama("Summarize and polish this document: " + fileContent);
                }
            } catch (Exception e) {
                res.status(400);
                return "Error processing file: " + e.getMessage();
            }
        });
        
        System.out.println("Portable AI Bridge Started on http://localhost:8080");
    }

    private static String callLocalOllama(String prompt) throws Exception {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        // Note: Escaping quotes for JSON manually (Basic version)
        String safePrompt = prompt.replace("\"", "\\\"").replace("\n", " ");
        //String jsonInput = "{\"model\": \"llama3.2\", \"prompt\": \"" + safePrompt + "\", \"stream\": false}";
        String jsonInput = "{\"model\": \"gemma3:12b\", \"prompt\": \"" + safePrompt + "\", \"stream\": false}";

        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes("UTF-8"));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) { 
                response.append(line); 
            }
        }
        return response.toString(); 
    }
}