package com.icuxika.framework.service.bailian;

import org.junit.jupiter.api.Test;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FrameworkServiceBailianApplicationTest {

    @Autowired
    private VectorStore vectorStore;

    @Test
    void documentReaderTest() {
        JsoupDocumentReader reader = new JsoupDocumentReader("test.html");
        List<Document> documents = reader.read();
        List<Document> chunks = new TokenTextSplitter().apply(documents);
        vectorStore.write(chunks);

        vectorStore.similaritySearch("森林的经营").stream().forEach(document -> {
            System.out.println("----------------------------------------");
            System.out.println(document.getScore());
            System.out.println(document.getText());
        });
    }

    @Test
    void structuredOutputTest() {
        BeanOutputConverter<ActorsFilms> outputConverter = new BeanOutputConverter<>(ActorsFilms.class);
        String format = outputConverter.getFormat();
        System.out.println(format);
    }

}

record ActorsFilms(String actor, List<String> movies) {
}