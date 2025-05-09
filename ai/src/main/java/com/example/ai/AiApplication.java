package com.example.ai;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }
}

@RestController
class AiController {

    private final ChatClient chatClient;
    private final Map<String, PromptChatMemoryAdvisor> memory = new ConcurrentHashMap<>();
    private final VectorStore vectorStore;
    private final String systemPrompt = "You are a helpful assistant.";

    public AiController(ChatClient.Builder builder,
                        VectorStore vectorStore) {
        this.vectorStore = vectorStore;
//        var document = new Document("user: %s, information: %s".formatted(
//                "Phong", "I am a software engineer with 0 years of experience in Java and Spring Boot."));
//        this.vectorStore.add(List.of(document));
        this.chatClient = builder.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore)).build();
    }

    @GetMapping("/{user}/ai")
    public String getAiResponse(@PathVariable("user") String user, @RequestParam String input) {
        var advisor =
                this.memory.computeIfAbsent(
                        user, _ -> PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build());
        return this.chatClient.prompt()
                .user(input)
                .advisors(advisor)
                .system(systemPrompt)
                .call().content();
    }
}
