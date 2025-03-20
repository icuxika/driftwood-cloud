package com.icuxika.admin.service;

import com.icuxika.admin.dto.LoginDTO;
import com.icuxika.admin.dto.RefreshTokenDTO;
import com.icuxika.admin.vo.QRCodeResponse;
import com.icuxika.admin.vo.TokenInfo;

public interface AuthService {
    TokenInfo login(LoginDTO loginDTO);

    TokenInfo refreshToken(RefreshTokenDTO refreshTokenDTO);

    String generateVerificationCode(String phone);

    String authorizationCode(String code);

    String desktopAuthorizationCode(String code);

    String generateCaptcha();

    QRCodeResponse generateQRCode();

    String scanQRCode(String qrcodeId);

    void confirmQRCode(String qrcodeToken);

    int getQRCodeStatus(String qrcodeId);
}
