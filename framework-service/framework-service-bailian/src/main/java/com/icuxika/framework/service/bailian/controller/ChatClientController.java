package com.icuxika.framework.service.bailian.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <a href="https://github.com/springaialibaba/spring-ai-alibaba-examples/blob/main/spring-ai-alibaba-chat-example/dashscope-chat/dashscope-chat-client/src/main/java/com/alibaba/cloud/ai/example/chat/dashscope/controller/DashScopeChatClientController.java">DashScopeChatClientController.java</a>
 */
@RestController
@RequestMapping("/chat-client")
public class ChatClientController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient chatClient;
    private final ChatModel chatModel;

    public ChatClientController(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(DashScopeChatOptions.builder().withTopP(0.7).withModel("qwen2.5").build())
                .build();
    }

    @GetMapping("/simple/chat")
    public String simpleChat(String userInput) {
        return chatClient.prompt(DEFAULT_PROMPT).call().content();
    }

    @GetMapping("/stream/chat")
    public Flux<String> streamChat(HttpServletResponse response, String userInput) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return chatClient.prompt(Objects.requireNonNullElse(userInput, DEFAULT_PROMPT)).stream().content();
    }

}
