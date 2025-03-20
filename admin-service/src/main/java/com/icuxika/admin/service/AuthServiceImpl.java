package com.icuxika.admin.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.admin.config.OpenAuthProperties;
import com.icuxika.admin.dto.LoginDTO;
import com.icuxika.admin.dto.RefreshTokenDTO;
import com.icuxika.admin.enumerate.SupportGrantType;
import com.icuxika.admin.feign.AuthClient;
import com.icuxika.admin.vo.QRCodeResponse;
import com.icuxika.admin.vo.TokenInfo;
import com.icuxika.admin.vo.TokenResponse;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.dict.QRCodeStatusType;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.transfer.auth.PhoneCodeCache;
import com.icuxika.framework.basic.transfer.auth.QRCodeCache;
import com.icuxika.framework.basic.util.DateUtil;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import com.icuxika.framework.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthClient authClient;

    private final UserClient userClient;

    private final RedisTemplate<String, Object> redisTemplate;

    private final TemplateEngine templateEngine;

    private final OpenAuthProperties openAuthProperties;

    private final ObjectMapper objectMapper;

    @Override
    public TokenInfo login(LoginDTO loginDTO) {
        Optional<SupportGrantType> supportGrantTypeOptional = EnumSet.allOf(SupportGrantType.class).stream().filter(p -> p.getType().equals(loginDTO.getGrantType())).findFirst();
        if (supportGrantTypeOptional.isEmpty()) {
            throw new GlobalServiceException("不支持的登录类型");
        }
        SupportGrantType supportGrantType = supportGrantTypeOptional.get();
        ResponseEntity<TokenResponse> tokenResponseResponseEntity;
        switch (supportGrantType) {
            case PASSWORD -> tokenResponseResponseEntity = authClient.tokenByPassword(
                    buildHeaders("id_password", "secret3"),
                    SupportGrantType.PASSWORD.getType(),
                    loginDTO.getIdentifier(),
                    loginDTO.getCredentials(),
                    loginDTO.getClientType()
            );
            case PHONE -> tokenResponseResponseEntity = authClient.tokenByPhone(
                    buildHeaders("id_phone", "secret4"),
                    SupportGrantType.PHONE.getType(),
                    loginDTO.getIdentifier(),
                    loginDTO.getCredentials(),
                    loginDTO.getClientType()
            );
            default -> throw new IllegalStateException("[不应出现]不支持的登录类型");
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
        throw new GlobalServiceException("登录失败：" + Optional.ofNullable(tokenResponseResponseEntity.getBody()).map(TokenResponse::getError).orElse("未知错误"));
    }

    @Override
    public TokenInfo refreshToken(RefreshTokenDTO refreshTokenDTO) {
        Optional<SupportGrantType> supportGrantTypeOptional = EnumSet.allOf(SupportGrantType.class).stream().filter(p -> p.getType().equals(refreshTokenDTO.getLoginGrantType())).findFirst();
        if (supportGrantTypeOptional.isEmpty()) {
            throw new GlobalServiceException("不支持的登录类型");
        }
        SupportGrantType supportGrantType = supportGrantTypeOptional.get();
        ResponseEntity<TokenResponse> tokenResponseResponseEntity;
        switch (supportGrantType) {
            case PASSWORD -> tokenResponseResponseEntity = authClient.refreshToken(
                    buildHeaders("id_password", "secret3"),
                    "refresh_token",
                    refreshTokenDTO.getRefreshToken(),
                    refreshTokenDTO.getClientType()

            );
            case PHONE -> tokenResponseResponseEntity = authClient.refreshToken(
                    buildHeaders("id_phone", "secret4"),
                    "refresh_token",
                    refreshTokenDTO.getRefreshToken(),
                    refreshTokenDTO.getClientType()
            );
            default -> throw new IllegalStateException("[不应出现]不支持的登录类型");
        }
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            TokenResponse tokenResponse = tokenResponseResponseEntity.getBody();
            if (tokenResponse == null) {
                throw new GlobalServiceException("[不应出现]token刷新失败，授权服务器返回结果为空！");
            }
            TokenInfo tokenInfo = new TokenInfo();
            BeanUtils.copyProperties(tokenResponse, tokenInfo);
            return tokenInfo;
        }
        throw new GlobalServiceException("token刷新失败：" + Optional.ofNullable(tokenResponseResponseEntity.getBody()).map(TokenResponse::getError).orElse("未知错误"));
    }


    @Override
    public String generateVerificationCode(String phone) {
        ApiData<UserAuthVO> userAuthVOApiData = userClient.findByPhone(phone);
        if (!userAuthVOApiData.isSuccess()) {
            throw new GlobalServiceException("查询手机号对应的用户信息失败");
        }
        if (userAuthVOApiData.getData() == null) {
            throw new GlobalServiceException("手机号对应账户尚未注册");
        }
        // 生成6为短信验证码，取值范围[0, 10)
        String code = new Random().ints(6, 0, 10).mapToObj(String::valueOf).collect(Collectors.joining());
        PhoneCodeCache phoneCodeCache = new PhoneCodeCache(code, Duration.ofMinutes(1).toMillis());
        // 缓存验证码及生效时长到redis
        // TODO: 过期的短信验证码清理；短信发送（阿里云接入）；接口安全保证；短时间重复请求过滤（网关处设置）
        redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_PHONE_CODE, phone, phoneCodeCache);
        return code;
    }

    @Override
    public String authorizationCode(String code) {
        ResponseEntity<TokenResponse> tokenResponseResponseEntity = authClient.tokenByAuthorizationCode(
                buildHeaders("id_authorization_code", "secret1"),
                Map.of("grant_type", "authorization_code", "code", code)
        );
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            TokenResponse tokenResponse = tokenResponseResponseEntity.getBody();
            if (tokenResponse == null) {
                throw new GlobalServiceException("[不应出现]登录失败，授权服务器登录返回结果为空！");
            }
            TokenInfo tokenInfo = new TokenInfo();
            BeanUtils.copyProperties(tokenResponse, tokenInfo);
            String json;
            try {
                json = objectMapper.writeValueAsString(tokenInfo);
            } catch (JsonProcessingException e) {
                throw new GlobalServiceException("登录失败：" + e.getMessage());
            }
            Context context = new Context();
            context.setVariable("message", Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
            context.setVariable("notificationPage", openAuthProperties.getNotificationPage());
            return templateEngine.process(openAuthProperties.getCallbackTemplate(), context);
        }
        throw new GlobalServiceException("登录失败：" + Optional.ofNullable(tokenResponseResponseEntity.getBody()).map(TokenResponse::getError).orElse("未知错误"));
    }

    @Override
    public String desktopAuthorizationCode(String code) {
        ResponseEntity<TokenResponse> tokenResponseResponseEntity = authClient.tokenByAuthorizationCode(
                buildHeaders("id_desktop_authorization_code", "secret5"),
                Map.of("grant_type", "authorization_code", "code", code)
        );
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            TokenResponse tokenResponse = tokenResponseResponseEntity.getBody();
            if (tokenResponse == null) {
                throw new GlobalServiceException("[不应出现]登录失败，授权服务器登录返回结果为空！");
            }
            TokenInfo tokenInfo = new TokenInfo();
            BeanUtils.copyProperties(tokenResponse, tokenInfo);
            String json;
            try {
                json = objectMapper.writeValueAsString(tokenInfo);
            } catch (JsonProcessingException e) {
                throw new GlobalServiceException("登录失败：" + e.getMessage());
            }
            Context context = new Context();
            context.setVariable("message", Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
            return templateEngine.process("desktopCallback", context);
        }
        throw new GlobalServiceException("登录失败：" + Optional.ofNullable(tokenResponseResponseEntity.getBody()).map(TokenResponse::getError).orElse("未知错误"));
    }

    @Override
    public String generateCaptcha() {
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(120, 38, 4, 4);
        PhoneCodeCache phoneCodeCache = new PhoneCodeCache(shearCaptcha.getCode(), Duration.ofMinutes(1).toMillis());
        redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_CAPTCHA, "captcha", phoneCodeCache);
        return shearCaptcha.getImageBase64();
    }

    @Override
    public QRCodeResponse generateQRCode() {
        QrConfig config = new QrConfig(300, 300);
        // 设置边距，既二维码和背景之间的边距
        config.setMargin(3);
        // 设置前景色，既二维码颜色（青色）
        config.setForeColor(Color.BLACK);
        // 设置背景色（灰色）
        config.setBackColor(Color.WHITE);

        String qrcodeId = UUID.randomUUID().toString();
        QRCodeCache qrCodeCache = new QRCodeCache(QRCodeStatusType.UN_SCANNED.getCode());
        redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId, qrCodeCache);
        String imageBase64 = QrCodeUtil.generateAsBase64(qrcodeId, config, "");

        QRCodeResponse qrCodeResponse = new QRCodeResponse();
        qrCodeResponse.setQrcodeId(qrcodeId);
        qrCodeResponse.setImageBase64(imageBase64);
        return qrCodeResponse;
    }

    @Override
    public String scanQRCode(String qrcodeId) {
        long userId = SecurityUtil.getUserId();
        String qrcodeTokenSrc = userId + ":" + DateUtil.getLocalDateTimeText() + ":" + UUID.randomUUID();
        String qrcodeToken = Base64.getEncoder().encodeToString(qrcodeTokenSrc.getBytes(StandardCharsets.UTF_8));

        QRCodeCache qrCodeCache = (QRCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId);
        if (qrCodeCache == null) {
            throw new GlobalServiceException("二维码数据不存在");
        }
        qrCodeCache.setStatus(QRCodeStatusType.SCANNED.getCode());
        redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId, qrCodeCache);

        List<String> qrcodeIdWrapper = new ArrayList<>();
        qrcodeIdWrapper.add(qrcodeId);
        redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeToken, qrcodeIdWrapper);
        return qrcodeToken;
    }

    @Override
    public void confirmQRCode(String qrcodeToken) {
        long userId = SecurityUtil.getUserId();
        @SuppressWarnings("unchecked")
        List<String> qrcodeIdWrapper = (List<String>) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeToken);
        if (qrcodeIdWrapper == null) {
            throw new GlobalServiceException("未找到二维码id");
        }
        String qrcodeId = qrcodeIdWrapper.getFirst();
        QRCodeCache qrCodeCache = (QRCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId);
        if (qrCodeCache == null) {
            throw new GlobalServiceException("二维码数据不存在");
        }
        qrCodeCache.setStatus(QRCodeStatusType.CONFIRMED.getCode());
        qrCodeCache.setUserId(userId);
        redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId, qrCodeCache);
    }

    @Override
    public int getQRCodeStatus(String qrcodeId) {
        QRCodeCache qrCodeCache = (QRCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId);
        if (qrCodeCache == null) {
            throw new GlobalServiceException("二维码数据不存在");
        }
        return qrCodeCache.getStatus();
    }

    private HttpHeaders buildHeaders(String clientId, String clientSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        return headers;
    }
}
