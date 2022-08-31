package com.icuxika.admin.controller;

import com.icuxika.admin.dto.LoginDTO;
import com.icuxika.admin.service.AuthService;
import com.icuxika.admin.vo.TokenInfo;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.security.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     * @return code
     */
    @Anonymous
    @PostMapping("/code")
    public ApiData<String> code(@RequestParam("phone") String phone) {
        String code = authService.generateVerificationCode(phone);
        return ApiData.ok(code);
    }
}
