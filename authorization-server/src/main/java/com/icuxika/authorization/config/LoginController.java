package com.icuxika.authorization.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        Object attribute = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (attribute instanceof AuthenticationException exception) {
            System.out.println("登录异常: " + exception.getMessage());
            model.addAttribute("exception", exception.getMessage());
        }
        return "login";
    }
}
