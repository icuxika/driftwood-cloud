package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.dto.UserDTO;
import com.icuxika.modules.user.entity.User;
import com.icuxika.modules.user.vo.UserAuthVO;
import com.icuxika.modules.user.vo.UserVO;
import com.icuxika.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ApiData<Page<UserVO>> getPage(@PageableDefault(sort = "id") Pageable pageable, UserDTO userDTO) {
        Page<UserVO> page = userService.getPage(pageable, userDTO);
        return ApiData.ok(page);
    }

    @GetMapping("/{id}")
    public ApiData<User> getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return ApiData.ok(user);
    }

    @PostMapping
    public ApiData<Void> save(@Validated @RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return ApiData.okMsg("新增成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
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

    @PostMapping("/uploadAvatar")
    public ApiData<Void> uploadAvatar(@RequestPart("file") MultipartFile file) {
        userService.uploadAvatar(file);
        return ApiData.okMsg("头像上传成功");
    }
}
