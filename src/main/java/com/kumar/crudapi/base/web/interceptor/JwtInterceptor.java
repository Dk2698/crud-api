package com.kumar.crudapi.base.web.interceptor;

import com.kumar.crudapi.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.getWriter().write("Invalid token");
            return false;
        }

        String username = JwtUtil.extractUsername(token);
        request.setAttribute("username", username);

        return true;
    }
}