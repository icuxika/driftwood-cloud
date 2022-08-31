package com.icuxika.user.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MultipartBodyPublisher;
import com.github.mizosoft.methanol.MutableRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.http.HttpResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc()
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPage() throws Exception {
        var multipartBodyPublisher = MultipartBodyPublisher.newBuilder()
                .textPart("grant_type", "password")
                .textPart("username", "icuxika")
                .textPart("password", "rbj549232512")
                .textPart("client_type", "0")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("id_password", "secret");
        var request = MutableRequest.POST("http://driftwood-cloud:8900/auth/oauth2/token", multipartBodyPublisher)
                .header(HttpHeaders.AUTHORIZATION, headers.toSingleValueMap().get(HttpHeaders.AUTHORIZATION));
        var httpClient = Methanol.create();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode tokenInfoJsonNode = objectMapper.readTree(response.body());
        String accessToken = "";
        if (tokenInfoJsonNode.has("access_token")) {
            accessToken = tokenInfoJsonNode.get("access_token").asText();
        }
        Assertions.assertTrue(accessToken.length() > 0);

        MvcResult mvcResult = mockMvc.perform(
                        get("/user/page")
                                .header("Authorization", "Bearer " + accessToken)
                                .param("username", "icuxika")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        JsonNode apiDataJsonNode = objectMapper.readTree(content);
        Assertions.assertTrue(apiDataJsonNode.has("data"));
        JsonNode dataJsonNode = apiDataJsonNode.get("data");
        Assertions.assertTrue(dataJsonNode.has("totalElements"));
        Assertions.assertEquals(1, dataJsonNode.get("totalElements").asInt());
    }

}
