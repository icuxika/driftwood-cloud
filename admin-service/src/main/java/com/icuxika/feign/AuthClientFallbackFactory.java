package com.icuxika.feign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.vo.TokenResponse;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AuthClient create(Throwable cause) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = cause.getMessage();
        if (cause instanceof FeignException.BadRequest badRequest) {
            // Spring Authorization Server 授权出错默认会以 HttpStatus.BAD_REQUEST 为状态码返回错误
            // OAuth2TokenEndpointFilter#sendErrorResponse
            status = badRequest.status();
            String errorJson = badRequest.responseBody().isPresent() ? new String(badRequest.responseBody().get().array()) : null;
            try {
                JsonNode jsonNode = objectMapper.readTree(errorJson);
                if (jsonNode.has("error")) {
                    error = jsonNode.get("error").asText();
                }
            } catch (JsonProcessingException e) {
                // do nothing
            }
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setError(error);

        int finalStatus = status;
        return new AuthClient() {
            @Override
            public ResponseEntity<TokenResponse> tokenByPassword(HttpHeaders headers, String grantType, String username, String password, String clientType) {
                return ResponseEntity.status(finalStatus).body(tokenResponse);
            }

            @Override
            public ResponseEntity<TokenResponse> tokenByPhone(HttpHeaders headers, String grantType, String username, String password, String clientType) {
                return ResponseEntity.status(finalStatus).body(tokenResponse);
            }
        };
    }
}
