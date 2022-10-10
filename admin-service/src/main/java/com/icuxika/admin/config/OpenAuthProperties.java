package com.icuxika.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "open.auth")
public class OpenAuthProperties {

    private String callbackTemplate;

    private String errorCallbackTemplate;

    private String notificationPage;

    private GithubProperties github;

    private GiteeProperties gitee;

    private WeChatMiniProperties wechatMini;

    private AlipayMiniProperties alipayMini;

    public String getCallbackTemplate() {
        return callbackTemplate;
    }

    public void setCallbackTemplate(String callbackTemplate) {
        this.callbackTemplate = callbackTemplate;
    }

    public String getErrorCallbackTemplate() {
        return errorCallbackTemplate;
    }

    public void setErrorCallbackTemplate(String errorCallbackTemplate) {
        this.errorCallbackTemplate = errorCallbackTemplate;
    }

    public String getNotificationPage() {
        return notificationPage;
    }

    public void setNotificationPage(String notificationPage) {
        this.notificationPage = notificationPage;
    }

    public GithubProperties getGithub() {
        return github;
    }

    public void setGithub(GithubProperties github) {
        this.github = github;
    }

    public GiteeProperties getGitee() {
        return gitee;
    }

    public void setGitee(GiteeProperties gitee) {
        this.gitee = gitee;
    }

    public WeChatMiniProperties getWechatMini() {
        return wechatMini;
    }

    public void setWechatMini(WeChatMiniProperties wechatMini) {
        this.wechatMini = wechatMini;
    }

    public AlipayMiniProperties getAlipayMini() {
        return alipayMini;
    }

    public void setAlipayMini(AlipayMiniProperties alipayMini) {
        this.alipayMini = alipayMini;
    }
}
