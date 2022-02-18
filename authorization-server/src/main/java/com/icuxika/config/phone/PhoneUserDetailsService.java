package com.icuxika.config.phone;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface PhoneUserDetailsService {

    UserDetails loadUserByPhone(String phone, String code) throws UsernameNotFoundException;
}
