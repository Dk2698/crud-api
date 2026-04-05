package com.kumar.crudapi.base.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if (token == null || !token.equals("valid-token")) {
            response.setStatus(401);
            response.getWriter().write("Unauthorized");
            return false;
        }

        return true;
    }

}