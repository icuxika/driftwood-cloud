package com.icuxika.feign;

import com.icuxika.vo.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "authorization-server", contextId = "authClient", fallbackFactory = AuthClientFallbackFactory.class)
public interface AuthClient {

    @PostMapping(value = "/oauth2/token")
    ResponseEntity<TokenResponse> tokenByPassword(
            @RequestHeader HttpHeaders headers,
            @RequestParam("grant_type") String grantType,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("client_type") String clientType
    );

    @PostMapping("/oauth2/token")
    ResponseEntity<TokenResponse> tokenByPhone(
            @RequestHeader HttpHeaders headers,
            @RequestParam("grant_type") String grantType,
            @RequestParam("phone") String phone,
            @RequestParam("code") String code,
            @RequestParam("client_type") String clientType
    );
}
