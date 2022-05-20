package com.icuxika.feign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.vo.TokenResponse;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AuthClient create(Throwable cause) {
        FeignException.BadRequest badRequest = (FeignException.BadRequest) cause;
        String errorJson = badRequest.responseBody().isPresent() ? new String(badRequest.responseBody().get().array()) : null;
        String error = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(errorJson);
            if (jsonNode.has("error")) {
                error = jsonNode.get("error").asText();
            }
        } catch (JsonProcessingException e) {
            // do nothing
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setError(error);

        return new AuthClient() {
            @Override
            public ResponseEntity<TokenResponse> tokenByPassword(HttpHeaders headers, String grantType, String username, String password, String clientType) {
                return ResponseEntity.status(badRequest.status()).body(tokenResponse);
            }

            @Override
            public ResponseEntity<TokenResponse> tokenByPhone(HttpHeaders headers, String grantType, String username, String password, String clientType) {
                return ResponseEntity.status(badRequest.status()).body(tokenResponse);
            }
        };
    }
}
