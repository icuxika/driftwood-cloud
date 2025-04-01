package com.icuxika.framework.service.bailian;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@SpringBootApplication(scanBasePackages = "com.icuxika")
@Slf4j
public class FrameworkServiceBailianApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceBailianApplication.class);
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    CommandLineRunner runner(VectorStore vectorStore, @Value("classpath:rag/terms-of-service.txt") String termsOfServiceUrl) {
        return args -> {
            vectorStore.write(new TokenTextSplitter().transform(
                    new TextReader(termsOfServiceUrl).read()
            ));
        };
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder().requestInterceptor((request, body, execution) -> {
            if (body.length > 0) {
                log.info("-------------------------------------------------------");
                log.info("请求地址: {}", request.getURI());
                log.info("请求详情: {}", new String(body, StandardCharsets.UTF_8));
            }
            return execution.execute(request, body);
        });
    }

}
