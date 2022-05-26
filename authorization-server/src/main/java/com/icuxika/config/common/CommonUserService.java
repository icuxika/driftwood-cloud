package com.icuxika.config.common;

import com.icuxika.config.UserDetailsImpl;
import com.icuxika.modules.user.vo.UserAuthVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public interface CommonUserService {

    /**
     * 基于UserVO构建UserDetails，填充权限信息
     *
     * @param userAuthVO 用户信息
     * @return UserDetails
     */
    default UserDetails buildUserDetails(UserAuthVO userAuthVO) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        BeanUtils.copyProperties(userAuthVO, userDetails);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(userAuthVO.getRoleList().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).toList());
        authorities.addAll(userAuthVO.getPermissionList().stream().map(permission -> new SimpleGrantedAuthority(permission.getAuthority())).toList());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }
}
