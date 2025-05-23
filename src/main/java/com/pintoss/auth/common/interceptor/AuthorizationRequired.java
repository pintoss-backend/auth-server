package com.pintoss.auth.common.interceptor;

import com.pintoss.auth.common.security.UserRoleEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpStatus;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizationRequired {
    UserRoleEnum[] value();

    String failureMessage() default "접근 권한이 없습니다.";

    HttpStatus status() default HttpStatus.UNAUTHORIZED;
}
