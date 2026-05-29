package com.security.demo;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("--- Secure GenAI Demo ---");
		System.out.print("Enter your prompt: ");
		String userInput = scanner.nextLine();

		// Step 1: Input Validation / Filtering
		if (SecureAIService.isSafe(userInput)) {
			System.out.println("Input validated. Sending to Ollama...");
			// Step 2: Secure API communication
			OllamaClient.queryLLM(userInput);
		} else {
			System.out.println("Request blocked by security layer.");
		}

		scanner.close();
	}
}
