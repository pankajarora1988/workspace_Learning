package com.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class RagDemo {

    // 1. Define an interface for your AI service
    interface Assistant {
        String chat(String message);
    }

    public static void main(String[] args) {
        // 2. Setup the Brain (LLM) via Ollama
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .build();

        // 3. Setup the Librarian (Embedding Model)
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .build();

        // 4. Create a "Database" (In-memory for this demo)  --- This is vector Db store in-memory in this example
       EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        
        

        // 5. Ingest Data (Feed the "book" to the librarian)
        Document doc = Document.from("The secret code for the office door is 12345.");
        // In a real app, LangChain4j handles splitting and storing this automatically
        
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
        ingestor.ingest(doc);
        
        
     // 6a. Create a Retriever (The bridge)
     // This tells the AI: "Use this model to search this specific store"
     EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
             .embeddingStore(embeddingStore)
             .embeddingModel(embeddingModel)
             .maxResults(2) // Find the top 2 most relevant pieces of info
             .build();

     // 6b. Tie it all together
     Assistant assistant = AiServices.builder(Assistant.class)
             .chatLanguageModel(model)
             .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
             .contentRetriever(contentRetriever) // Pass the bridge object here
             .build();

        // 7. Ask a question!
        String response = assistant.chat("What is the secret code for the door?");
        System.out.println("AI Response: " + response);
    }
}
