package com.icuxika.framework.service.bailian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.codec.ServerSentEvent;
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
@Slf4j
public class ChatClientController {

    private static final String DEFAULT_USER_PROMPT = "你好，介绍下你自己！";
    private static final String DEFAULT_SYSTEM_PROMPT = "你是一个有帮助的助手。";

    private final ObjectMapper objectMapper;

    private final ChatClient chatClient;

    public ChatClientController(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder
                .defaultSystem(DEFAULT_SYSTEM_PROMPT)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(OpenAiChatOptions.builder().topP(0.7).build())
                .build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("/simple/chat")
    public String simpleChat(String userInput) {
        String userText = Objects.requireNonNullElse(userInput, DEFAULT_USER_PROMPT);
        return chatClient.prompt().user(userText).call().content();
    }

    @GetMapping("/stream/chat")
    public Flux<String> streamChat(HttpServletResponse response, String userInput) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String userText = Objects.requireNonNullElse(userInput, DEFAULT_USER_PROMPT);
        return chatClient.prompt().user(userText).stream().content();
    }

    @GetMapping("/stream/sse/chat")
    public Flux<ServerSentEvent<String>> streamSSEChat(HttpServletResponse response, String userInput) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String userText = Objects.requireNonNullElse(userInput, DEFAULT_USER_PROMPT);
        return chatClient.prompt().user(userText).stream().chatResponse()
                .<ServerSentEvent<String>>handle((s, sink) -> {
                    if ("STOP".equals(s.getResult().getMetadata().getFinishReason())) {
                        var usage = s.getMetadata().getUsage();
                        log.info("请求数据统计->[promptTokens(输入):{}],[generationTokens(生成):{}],[totalTokens(总):{}]", usage.getPromptTokens(), usage.getGenerationTokens(), usage.getTotalTokens());
                    }
                    sink.next(ServerSentEvent.<String>builder()
                            .event("message")
                            .data(s.getResult().getOutput().getText())
                            .build());
                }).concatWithValues(
                        ServerSentEvent.<String>builder()
                                .event("message")
                                .data("[DONE]")
                                .build()
                );
    }
}
