package com.icuxika.authorization.config.qrcode;

import org.springframework.security.core.userdetails.UserDetails;

public interface QRCodeUserDetailsService {

    UserDetails loadUserByQRCodeId(String qrcodeId);
}
