package com.icuxika.auditing;

import com.icuxika.constant.SystemConstant;
import com.icuxika.util.SecurityUtil;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId;
        try {
            userId = SecurityUtil.getUserId();
        } catch (Exception e) {
            // 未登录的情况下，需要新增或更新继承了BaseEntity的Entity并且Application启用了JPAAudit的情况下会遇到以下错误
            // Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction
            userId = SystemConstant.SYSTEM_CREATE_USER_ID;
        }
        return Optional.ofNullable(userId);
    }
}
