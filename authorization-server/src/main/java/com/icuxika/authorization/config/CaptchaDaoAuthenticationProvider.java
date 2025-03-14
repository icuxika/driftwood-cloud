package com.icuxika.authorization.config;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.transfer.auth.PhoneCodeCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
@Slf4j
public class CaptchaDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private final RedisTemplate<String, Object> redisTemplate;

    public CaptchaDaoAuthenticationProvider(UserDetailsService userDetailsService, RedisTemplate<String, Object> redisTemplate) {
        setUserDetailsService(userDetailsService);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException("No request attributes found");
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String type = request.getParameter("type");
        log.info("登录类型: {}", type);
        if ("password".equals(type)) {
            String requestCaptcha = request.getParameter("captcha");
            log.info("收到的图形验证码: {}", requestCaptcha);
            PhoneCodeCache phoneCodeCache = (PhoneCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_CAPTCHA, "captcha");
            String captcha = phoneCodeCache == null ? "" : phoneCodeCache.getCode();
            log.info("正确的图形验证码: {}", captcha);
            if (captcha != null && !captcha.equals(requestCaptcha)) {
                throw new AuthenticationServiceException("验证码错误");
            }
        }
        if ("phone".equals(type)) {
            String phone = request.getParameter("phone");
            log.info("手机号: {}", phone);
            String code = request.getParameter("code");
            log.info("短信验证码: {}", code);
            UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(phone, code);
            authenticationToken.setDetails(new WebAuthenticationDetails(request));
            return super.authenticate(authenticationToken);
        }
        return super.authenticate(authentication);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException("No request attributes found");
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String type = request.getParameter("type");
        if ("phone".equals(type)) {
            PhoneCodeCache phoneCodeCache = (PhoneCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_PHONE_CODE, authentication.getPrincipal());
            String code = phoneCodeCache == null ? "" : phoneCodeCache.getCode();
            log.info("收到的短信验证码: {}", authentication.getCredentials());
            log.info("正确的短信验证码: {}", code);
            if (!Objects.equals(authentication.getCredentials(), code)) {
                throw new BadCredentialsException("短信验证码错误");
            }
        } else {
            super.additionalAuthenticationChecks(userDetails, authentication);
        }
    }
}
