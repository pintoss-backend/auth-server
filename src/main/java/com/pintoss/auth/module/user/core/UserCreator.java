package com.pintoss.auth.module.user.core;

import com.pintoss.auth.module.user.domain.LoginType;
import com.pintoss.auth.module.user.domain.Phone;
import com.pintoss.auth.module.user.domain.User;
import com.pintoss.auth.module.user.domain.UserRole;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreator {

    private final PasswordEncoder passwordEncoder;

    public User register(String email, String password, String name, String phoneValue, LoginType loginType, Set<UserRole> roles) {
        Phone phone = new Phone(phoneValue);

        return User.register(
            email,
            passwordEncoder.encode(password),
            name,
            phone,
            loginType,
            roles
        );
    }

}
