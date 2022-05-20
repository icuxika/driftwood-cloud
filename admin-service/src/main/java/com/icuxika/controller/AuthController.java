package com.icuxika.controller;

import com.icuxika.annotations.Anonymous;
import com.icuxika.common.ApiData;
import com.icuxika.dto.LoginDTO;
import com.icuxika.service.AuthService;
import com.icuxika.vo.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * OAuth2用户登录
     *
     * @param loginDTO 登陆参数
     * @return accessToken等信息
     */
    @Anonymous
    @PostMapping("/login")
    public ApiData<TokenInfo> login(@RequestBody LoginDTO loginDTO) {
        TokenInfo tokenInfo = authService.login(loginDTO);
        return ApiData.ok(tokenInfo);
    }
}
