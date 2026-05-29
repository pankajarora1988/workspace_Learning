package com.security.demo;

import java.util.regex.Pattern;

public class SecureAIService {

    // 1. Input Validation & Prompt Injection Protection
    public static boolean isSafe(String input) {
        if (input == null || input.trim().isEmpty()) return false;

        // Simple blacklist for common prompt injection keywords
        String lowerInput = input.toLowerCase();
        String[] forbiddenTokens = {"ignore previous instructions", "system override", "reveal system prompt"};

        for (String token : forbiddenTokens) {
            if (lowerInput.contains(token)) {
                System.out.println("ALERT: Potential Prompt Injection Detected!");
                return false;
            }
        }

        // 2. Sensitive Data Filtering (PII)
        // Simple Regex for Credit Cards or Emails
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        if (emailPattern.matcher(input).find()) {
            System.out.println("ALERT: Sensitive PII (Email) detected. Blocking request.");
            return false;
        }

        return true;
    }
}