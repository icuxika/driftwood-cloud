package com.icuxika.admin.controller;

import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MoreBodyHandlers;
import com.github.mizosoft.methanol.MutableRequest;
import com.icuxika.admin.config.OpenAuthProperties;
import com.icuxika.admin.vo.GithubAccessToken;
import com.icuxika.admin.vo.GithubUser;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.dict.OpenAuthType;
import com.icuxika.framework.config.annotation.ApiReturn;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/third/auth")
public class ThirdAuthController {

    @Autowired
    private OpenAuthProperties openAuthProperties;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserClient userClient;

    @ApiReturn(disable = true)
    @RequestMapping("/github/callback")
    public String callbackForGithub(String code) {
        Context context = new Context();
        var client = Methanol.create();
        var request = MutableRequest.POST(
                openAuthProperties.getGithub().getAccessTokenUrl() +
                        "?client_id=" + openAuthProperties.getGithub().getClientId() +
                        "&client_secret=" + openAuthProperties.getGithub().getClientSecret() +
                        "&code=" + code, HttpRequest.BodyPublishers.noBody()
        ).header("Accept", "application/json");
        try {
            var tokenResponse = client.send(request, MoreBodyHandlers.ofObject(GithubAccessToken.class));
            GithubAccessToken githubAccessToken = tokenResponse.body();

            request = MutableRequest.GET(openAuthProperties.getGithub().getUserUrl())
                    .header("Authorization", githubAccessToken.getTokenType() + " " + githubAccessToken.getAccessToken());
            var userResponse = client.send(request, MoreBodyHandlers.ofObject(GithubUser.class));
            GithubUser githubUser = userResponse.body();
            ApiData<Boolean> isBoundApiData = userClient.findThirdBindByOpenid(githubUser.getId(), OpenAuthType.GITHUB.getCode());
            if (isBoundApiData.isSuccess()) {
                boolean isBound = isBoundApiData.getData();
                // 1、登录
                //  1.1、已绑定，直接登录
                //  1.2、未绑定，提示绑定用户
                // 2、已登录进行绑定
                //  2.1、已绑定，提示冲突，要先去解绑
                //  2.2、未绑定，成功绑定
                if (isBound) {
                } else {
                }
                return templateEngine.process(openAuthProperties.getCallbackTemplate(), context);
            } else {
                return templateEngine.process(openAuthProperties.getErrorCallbackTemplate(), context);
            }
        } catch (IOException | InterruptedException e) {
            return templateEngine.process(openAuthProperties.getErrorCallbackTemplate(), context);
        }
    }

    @ApiReturn(disable = true)
    @RequestMapping("/gitee/callback")
    public String callbackForGitee(String code) {
        Context context = new Context();
        return templateEngine.process(openAuthProperties.getErrorCallbackTemplate(), context);
    }

    @ApiReturn(disable = true)
    @RequestMapping("/wechat/mini/callback")
    public String callbackForWeChatMini(String code) {
        Context context = new Context();
        return templateEngine.process(openAuthProperties.getErrorCallbackTemplate(), context);
    }

    /**
     * <a href="https://opendocs.alipay.com/apis/api_9/alipay.system.oauth.token">alipay.system.oauth.token(换取授权访问令牌)</a>
     *
     * @return
     */
    @ApiReturn(disable = true)
    @RequestMapping("/alipay/mini/callback")
    public String callbackForAlipayMini(String code) {
        Context context = new Context();
        return templateEngine.process(openAuthProperties.getErrorCallbackTemplate(), context);
    }

    private String key() {
        return "DRIFTWOOD:OAUTH2:OPENID:IDENTIFIER:t" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
