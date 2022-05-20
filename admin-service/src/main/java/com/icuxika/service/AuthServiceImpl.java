package com.icuxika.service;

import com.icuxika.dto.LoginDTO;
import com.icuxika.enumerate.SupportGrantType;
import com.icuxika.exception.GlobalServiceException;
import com.icuxika.feign.AuthClient;
import com.icuxika.vo.TokenInfo;
import com.icuxika.vo.TokenResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthClient authClient;

    @Override
    public TokenInfo login(LoginDTO loginDTO) {
        Optional<SupportGrantType> supportGrantTypeOptional = EnumSet.allOf(SupportGrantType.class).stream().filter(p -> p.getType().equals(loginDTO.getGrantType())).findFirst();
        if (!supportGrantTypeOptional.isPresent()) {
            throw new GlobalServiceException("不支持的登录类型");
        }
        SupportGrantType supportGrantType = supportGrantTypeOptional.get();
        ResponseEntity<TokenResponse> tokenResponseResponseEntity;
        switch (supportGrantType) {
            case PASSWORD: {
                tokenResponseResponseEntity = authClient.tokenByPassword(
                        buildHeaders("id_password", "secret"),
                        SupportGrantType.PASSWORD.getType(),
                        loginDTO.getIdentifier(),
                        loginDTO.getCredentials(),
                        loginDTO.getClientType()
                );
                break;
            }
            case PHONE: {
                tokenResponseResponseEntity = authClient.tokenByPhone(
                        buildHeaders("id_phone", "secret"),
                        SupportGrantType.PHONE.getType(),
                        loginDTO.getIdentifier(),
                        loginDTO.getCredentials(),
                        loginDTO.getClientType()
                );
                break;
            }
            default: {
                throw new IllegalStateException("[不应出现]不支持的登录类型");
            }
        }
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            TokenResponse tokenResponse = tokenResponseResponseEntity.getBody();
            if (tokenResponse == null) {
                throw new GlobalServiceException("[不应出现]登录失败，授权服务器登录返回结果为空！");
            }
            TokenInfo tokenInfo = new TokenInfo();
            BeanUtils.copyProperties(tokenResponse, tokenInfo);
            return tokenInfo;
        }
        throw new GlobalServiceException("登录失败：" + tokenResponseResponseEntity.getBody().getError());
    }

    private HttpHeaders buildHeaders(String clientId, String clientSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        return headers;
    }
}
