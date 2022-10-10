package com.icuxika.admin.controller;

import com.icuxika.admin.config.OpenAuthProperties;
import com.icuxika.framework.config.annotation.ApiReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/third/auth")
public class ThirdAuthController {

    @Autowired
    private OpenAuthProperties openAuthProperties;

    @ApiReturn(disable = true)
    @RequestMapping("/github/callback")
    public String callbackForGithub(String code) {
        return "";
    }

    @ApiReturn(disable = true)
    @RequestMapping("/gitee/callback")
    public String callbackForGitee(String code) {
        return "";
    }

    @ApiReturn(disable = true)
    @RequestMapping("/wechat/mini/callback")
    public String callbackForWeChatMini(String code) {
        return "";
    }

    /**
     * <a href="https://opendocs.alipay.com/apis/api_9/alipay.system.oauth.token">alipay.system.oauth.token(换取授权访问令牌)</a>
     *
     * @return
     */
    @ApiReturn(disable = true)
    @RequestMapping("/alipay/mini/callback")
    public String callbackForAlipayMini() {
        return "";
    }

}
