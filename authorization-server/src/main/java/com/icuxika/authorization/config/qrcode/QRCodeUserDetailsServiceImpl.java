package com.icuxika.authorization.config.qrcode;

import com.icuxika.authorization.config.common.CommonUserService;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.transfer.auth.QRCodeCache;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service(value = "qrCodeUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class QRCodeUserDetailsServiceImpl implements QRCodeUserDetailsService, CommonUserService {

    private final UserClient userClient;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByQRCodeId(String qrcodeId) {
        log.info("qrcodeId: {}", qrcodeId);
        QRCodeCache qrCodeCache = (QRCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_QR_CODE, qrcodeId);
        if (qrCodeCache != null) {
            log.info("userId: {}", qrCodeCache.getUserId());
        }

        ApiData<UserAuthVO> userApiData = userClient.findByUsername("icuxika");
        UserAuthVO user = userApiData.getData();
        return buildUserDetails(user);
    }

}
