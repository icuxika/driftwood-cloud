package com.icuxika.admin.service;

import com.icuxika.admin.dto.LoginDTO;
import com.icuxika.admin.enumerate.SupportGrantType;
import com.icuxika.admin.feign.AuthClient;
import com.icuxika.admin.vo.TokenInfo;
import com.icuxika.admin.vo.TokenResponse;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.transfer.auth.PhoneCodeCache;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthClient authClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
                    buildHeaders("id_password", "secret"),
                    SupportGrantType.PASSWORD.getType(),
                    loginDTO.getIdentifier(),
                    loginDTO.getCredentials(),
                    loginDTO.getClientType()
            );
            case PHONE -> tokenResponseResponseEntity = authClient.tokenByPhone(
                    buildHeaders("id_phone", "secret"),
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

    private HttpHeaders buildHeaders(String clientId, String clientSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        return headers;
    }
}
