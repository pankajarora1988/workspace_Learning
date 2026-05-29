import dev.langchain4j.model.ollama.OllamaChatModel;

public class LocalAITest {
    public static void main(String[] args) {
        // Step A: Point Java to your local Windows Ollama server
        // By default, Ollama runs on port 11434
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2") 
                .build();

        System.out.println("--- System: Sending request to Local LLM ---");

        // Step B: Send the prompt
        String userPrompt = "write a Java program to print table of 5";
        String response = model.generate(userPrompt);

        // Step C: Print result
        System.out.println("AI Response: " + response);
    }
}