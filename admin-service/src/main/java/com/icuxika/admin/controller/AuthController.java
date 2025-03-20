package com.icuxika.admin.controller;

import com.icuxika.admin.dto.LoginDTO;
import com.icuxika.admin.dto.RefreshTokenDTO;
import com.icuxika.admin.service.AuthService;
import com.icuxika.admin.vo.QRCodeResponse;
import com.icuxika.admin.vo.TokenInfo;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.config.annotation.ApiReturn;
import com.icuxika.framework.security.annotation.Anonymous;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RedisTemplate<String, String> redisTemplate;

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

    @Anonymous
    @PostMapping("/refreshToken")
    public ApiData<TokenInfo> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        TokenInfo tokenInfo = authService.refreshToken(refreshTokenDTO);
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

    @Anonymous
    @ApiReturn(disable = true)
    @GetMapping("authorizationCode")
    public String authorizationCode(String code) {
        return authService.authorizationCode(code);
    }

    @Anonymous
    @ApiReturn(disable = true)
    @GetMapping("desktopAuthorizationCode")
    public String desktopAuthorizationCode(String code) {
        return authService.desktopAuthorizationCode(code);
    }

    @Anonymous
    @GetMapping("captcha")
    public ApiData<String> captcha() {
        String imageBase64 = authService.generateCaptcha();
        return ApiData.ok(imageBase64);
    }

    @Anonymous
    @GetMapping("qrcode")
    public ApiData<QRCodeResponse> qrcode() {
        QRCodeResponse qrCodeResponse = authService.generateQRCode();
        return ApiData.ok(qrCodeResponse);
    }

    @Anonymous
    @GetMapping("getQRCodeStatus")
    public ApiData<Integer> getQRCodeStatus(@RequestParam("qrcodeId") String qrcodeId) {
        int status = authService.getQRCodeStatus(qrcodeId);
        return ApiData.ok(status);
    }

    @PostMapping("/scanQRCode")
    public ApiData<String> scanQRCode(@RequestParam("qrcodeId") String qrcodeId) {
        String qrcodeToken = authService.scanQRCode(qrcodeId);
        return ApiData.ok(qrcodeToken);
    }

    @PostMapping("/confirmQRCode")
    public ApiData<Void> confirmQRCode(@RequestParam("qrcodeToken") String qrcodeToken) {
        authService.confirmQRCode(qrcodeToken);
        return ApiData.okMsg("二维码登录已确认");
    }
}
