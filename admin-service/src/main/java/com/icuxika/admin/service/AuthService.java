package com.icuxika.admin.service;

import com.icuxika.admin.dto.LoginDTO;
import com.icuxika.admin.vo.TokenInfo;

public interface AuthService {
    TokenInfo login(LoginDTO loginDTO);

    String generateVerificationCode(String phone);
}
