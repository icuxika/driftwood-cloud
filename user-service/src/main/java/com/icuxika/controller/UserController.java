package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.service.UserService;
import com.icuxika.user.entity.User;
import com.icuxika.user.vo.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * OAuth2认证使用 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("findByUsername")
    @PreAuthorize("#request.getHeader('FEIGN-REQUEST') == 'VERIFICATION' || hasRole('USER')")
    public ApiData<User> findByUsername(@RequestParam("username") String username, HttpServletRequest request) {
        User user = userService.findByUsername(username);
        return ApiData.ok(user);
    }

    /**
     * OAuth2认证使用 根据手机号获取用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("findByPhone")
    @PreAuthorize("#request.getHeader('FEIGN-REQUEST') == 'VERIFICATION' || hasRole('USER')")
    public ApiData<User> findByPhone(@RequestParam("phone") String phone, HttpServletRequest request) {
        User user = userService.findByPhone(phone);
        return ApiData.ok(user);
    }

    /**
     * 获取当前登录用户的用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getUserInfo")
    public ApiData<UserVO> getUserInfo() {
        return ApiData.ok(userService.getUserInfo());
    }
}
