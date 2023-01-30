package com.icuxika.user.runner;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.object.modules.user.entity.User;
import com.icuxika.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AdminInitializeRunner implements CommandLineRunner {

    private static final Logger L = LoggerFactory.getLogger(AdminInitializeRunner.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "icuxika";
        String adminPassword = "rbj549232512";
        if (L.isInfoEnabled()) {
            L.info("管理员用户初始化");
        }
        Optional<User> userOptional = userRepository.findByUsername(adminUsername);
        if (userOptional.isPresent()) {
            if (L.isInfoEnabled()) {
                L.info("管理员用户[" + adminUsername + "]已经存在");
            }
        } else {
            PasswordEncoder passwordEncoder =
                    PasswordEncoderFactories.createDelegatingPasswordEncoder();
            User user = new User();
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setPhone("18752065699");
            user.setNickname("浮木");
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCreateTime(LocalDateTime.now());
            user.setCreateUserId(SystemConstant.SYSTEM_CREATE_USER_ID);
            user.setUpdateTime(LocalDateTime.now());
            user.setUpdateUserId(SystemConstant.SYSTEM_CREATE_USER_ID);
            userRepository.save(user);
            if (L.isInfoEnabled()) {
                L.info("管理员用户[" + adminUsername + "]创建成功，用户 id 为：" + user.getId());
            }
        }
    }

    private void initializeRoleBinding() {
        // TODO: 初始化角色绑定
    }

    private void initializePermissionBinding() {
        // TODO: 初始化权限绑定
    }
}
