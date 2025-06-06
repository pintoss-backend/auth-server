package com.pintoss.auth.module.user.api;

import com.pintoss.auth.common.dto.ApiResponse;
import com.pintoss.auth.common.exception.ErrorCode;
import com.pintoss.auth.common.exception.client.BadRequestException;
import com.pintoss.auth.common.util.HttpServletUtils;
import com.pintoss.auth.module.user.api.dto.LoginRequest;
import com.pintoss.auth.module.user.api.dto.LoginResponse;
import com.pintoss.auth.module.user.api.dto.RegisterRequest;
import com.pintoss.auth.module.user.api.dto.ReissueResponse;
import com.pintoss.auth.module.user.usecase.LoginUseCase;
import com.pintoss.auth.module.user.usecase.RegisterUseCase;
import com.pintoss.auth.module.user.usecase.ReissueUseCase;
import com.pintoss.auth.module.user.usecase.dto.LoginResult;
import com.pintoss.auth.module.user.usecase.dto.ReissueCommand;
import com.pintoss.auth.module.user.usecase.dto.ReissueResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final ReissueUseCase reissueUseCase;
    private final HttpServletUtils servletUtils;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        registerUseCase.register(request.to());

        return ApiResponse.ok(null);
    }


    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse servletResponse) {
        LoginResult result = loginUseCase.login(request.to());

        servletUtils.addCookie(servletResponse, "RefreshToken", result.getRefreshToken(), (int) 1000000000L);

        return ApiResponse.ok(new LoginResponse(result.getAccessToken()));
    }

    @PostMapping("/reissue")
    public ApiResponse<ReissueResponse> reissue(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String accessToken = servletUtils.getAccessToken(servletRequest)
            .orElseThrow(() -> new BadRequestException(ErrorCode.AUTH_MISSING_ACCESS_TOKEN));

        String refreshToken = servletUtils.getCookie(servletRequest, "RefreshToken")
            .map(Cookie::getValue)
            .orElseThrow(() -> new BadRequestException(ErrorCode.AUTH_MISSING_REFRESH_TOKEN));

        ReissueCommand command = new ReissueCommand(accessToken, refreshToken);
        ReissueResult result = reissueUseCase.reissue(command);

        servletUtils.addCookie(servletResponse, "RefreshToken", result.getRefreshToken(), (int) 1000000000L);

        return ApiResponse.ok(new ReissueResponse(result.getAccessToken()));
    }

}
