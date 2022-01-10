package com.icuxika.repository;

import com.icuxika.constant.SystemConstant;
import com.icuxika.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save() {
        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User user = new User();
        user.setUsername("icuxika");
        user.setPassword(passwordEncoder.encode("rbj549232512"));
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
    }

}