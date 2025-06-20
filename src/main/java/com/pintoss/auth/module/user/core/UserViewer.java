package com.pintoss.auth.module.user.core;

import com.pintoss.auth.common.exception.ErrorCode;
import com.pintoss.auth.common.exception.client.BadRequestException;
import com.pintoss.auth.module.user.domain.User;
import com.pintoss.auth.module.user.domain.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserViewer {

    private final UserRepository userRepository;

    public UserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(
            ErrorCode.USER_NOT_FOUND));
        return UserInfo.builder()
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone().getValue())
            .build();
    }
}
