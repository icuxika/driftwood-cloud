package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.service.UserService;
import com.icuxika.user.dto.BindOneDTO;
import com.icuxika.user.entity.User;
import com.icuxika.user.vo.UserAuthVO;
import com.icuxika.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    public ApiData<UserAuthVO> findByUsername(@RequestParam("username") String username, HttpServletRequest request) {
        UserAuthVO userAuthVO = userService.findByUsername(username);
        return ApiData.ok(userAuthVO);
    }

    /**
     * OAuth2认证使用 根据手机号获取用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("findByPhone")
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    public ApiData<UserAuthVO> findByPhone(@RequestParam("phone") String phone, HttpServletRequest request) {
        UserAuthVO userAuthVO = userService.findByPhone(phone);
        return ApiData.ok(userAuthVO);
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

    @GetMapping("/page")
    public ApiData<Page<User>> getPage(@PageableDefault(sort = "id") Pageable pageable, User user) {
        Page<User> page = userService.getPage(pageable, user);
        return ApiData.ok(page);
    }

    @GetMapping("/{id}")
    public ApiData<User> getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return ApiData.ok(user);
    }

    @PostMapping
    public ApiData<Void> save(@RequestBody User user) {
        userService.save(user);
        return ApiData.okMsg("新增成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody User user) {
        userService.update(user);
        return ApiData.okMsg("更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }

    @PostMapping("/bindRoles")
    public ApiData<Void> bindRoles(@RequestBody BindOneDTO bindOneDTO) {
        userService.bindRoles(bindOneDTO);
        return ApiData.okMsg("绑定成功");
    }
}
