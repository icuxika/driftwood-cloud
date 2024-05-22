package com.icuxika.user.repository;

import com.icuxika.framework.basic.constant.SystemConstant;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public record GeneratedUser(long id, Timestamp createTime, long createUserId, String tenantId, Timestamp updateTime,
                            long updateUserId, Timestamp deleteTime, boolean deleted, boolean isAccountNonExpired,
                            boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled,
                            String nickname, String password, String phone, String username) {

    public static class GeneratedUserScope extends StructuredTaskScope<GeneratedUser> {

        private final Collection<GeneratedUser> users = new ConcurrentLinkedDeque<>();
        private final Collection<Throwable> exceptions = new ConcurrentLinkedDeque<>();

        @Override
        protected void handleComplete(Subtask<? extends GeneratedUser> subtask) {
            switch (subtask.state()) {
                case UNAVAILABLE ->
                        throw new IllegalStateException("subtask completed or forked after the task scope was shut down");
                case SUCCESS -> this.users.add(subtask.get());
                case FAILED -> this.exceptions.add(subtask.exception());
            }
        }
    }

    public static Collection<GeneratedUser> generate(long start, long end) {
        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();
        try (var scope = new GeneratedUserScope()) {
            while (start < end) {
                long finalStart = start;
                scope.fork(() -> {
                    String password = passwordEncoder.encode(STR."password_\{finalStart}");
                    String phone = randomPhone();
                    return new GeneratedUser(finalStart, new Timestamp(System.currentTimeMillis()), 1L, SystemConstant.DEFAULT_TENANT_ID, new Timestamp(System.currentTimeMillis()), 1L, null, false, true, true, true, true, STR."nickname_\{finalStart}", password, phone, STR."username_\{finalStart}");
                });
                start++;
            }
            scope.join();
            return scope.users;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String randomPhone() {
        return STR."1\{ThreadLocalRandom.current().ints(10, 0, 10).mapToObj(String::valueOf).collect(Collectors.joining())}";
    }
}
