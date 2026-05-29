import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing Local AI Connection...");

        // 1. Setup the model connection
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .timeout(Duration.ofSeconds(60)) // Vital for laptop power-save modes
                .build();

        try {
            // 2. Test connectivity
            String question = "Briefly explain what an LLM is.";
            System.out.println("User: " + question);
            
            String response = model.generate(question);
            
            System.out.println("\nAI Response:\n" + response);
            System.out.println("\n--- Test Successful! ---");
            
        } catch (Exception e) {
            System.err.println("Connection Failed! Make sure Ollama is running in your taskbar.");
            e.printStackTrace();
        }
    }
}