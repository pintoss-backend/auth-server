package com.pintoss.auth.module.user.process;

import com.pintoss.auth.common.exception.ErrorCode;
import com.pintoss.auth.common.exception.client.BadRequestException;
import com.pintoss.auth.common.exception.client.DuplicateEmailException;
import com.pintoss.auth.module.user.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void duplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("이미 존재하는 회원입니다.");
        }
    }

    public void matchPassword(String rawPassword, String inputPassword) {
        if(!passwordEncoder.matches(rawPassword, inputPassword)) {
            throw new BadRequestException(ErrorCode.AUTH_PASSWROD_INVALID);
        }
    }

    public void verifyRefreshToken(String refreshToken) {
        if(!jwtProvider.validateToken(refreshToken)) {
            throw new BadRequestException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }
    }
}
