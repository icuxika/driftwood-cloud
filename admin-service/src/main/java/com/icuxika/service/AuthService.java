package com.icuxika.service;

import com.icuxika.dto.LoginDTO;
import com.icuxika.vo.TokenInfo;

public interface AuthService {
    TokenInfo login(LoginDTO loginDTO);

    String generateVerificationCode(String phone);
}
