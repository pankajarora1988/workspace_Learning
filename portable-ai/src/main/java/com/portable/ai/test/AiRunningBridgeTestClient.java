package com.portable.ai.test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class AiRunningBridgeTestClient {

    private static final String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) {
        try {
            System.out.println("=== Starting AI Bridge Integration Tests ===\n");

            // Test 1: Polish Service
            //testTextService("/polish", "i wants to goes to market but weather are bad");

            // Test 2: Generate Service
            //testTextService("/anytext", "Tell me a 1-sentence joke about Java.");

            // Test 3: Attachment Service
            // Create a temporary file to test the upload
            File tempFile = createTempTestFile("test_attachment.txt", "This is the content of the file. It needs summarizing.");
            testAttachmentService("/textwithattachment", tempFile);

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: " + e.getMessage());
            System.err.println("Ensure your 'start.bat' (Ollama + AiBridge) is running.");
        }
    }

    /**
     * Tests /polish and /generate (Plain Text)
     */
    private static void testTextService(String endpoint, String body) throws Exception {
        System.out.println("Testing Endpoint: " + endpoint);
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/plain");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }

        printResponse(conn);
    }

    /**
     * Tests /generateAttachment (Multipart/Form-Data)
     */
    private static void testAttachmentService(String endpoint, File file) throws Exception {
        System.out.println("Testing Endpoint: " + endpoint + " with file: " + file.getName());
        String boundary = "---" + System.currentTimeMillis();
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream os = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {
            
            // Start boundary
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
            writer.append("Content-Type: text/plain").append("\r\n");
            writer.append("\r\n").flush();

            // File content
            Files.copy(file.toPath(), os);
            os.flush();
            
            // End boundary
            writer.append("\r\n").append("--" + boundary + "--").append("\r\n").flush();
        }

        printResponse(conn);
    }

    private static void printResponse(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        System.out.println("HTTP Status: " + code);

        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            System.out.print("AI Response: ");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
        System.out.println("\n--------------------------------------------\n");
    }

    private static File createTempTestFile(String name, String content) throws IOException {
        File file = new File(name);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        file.deleteOnExit();
        return file;
    }
}