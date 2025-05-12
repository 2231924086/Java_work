package com.taskmanagement.filter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/api/*")
public class AuthenticationFilter implements Filter {
    // 不需要认证的API路径
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/users",           // 用户注册
        "/api/users/login",     // 用户登录
        "/api/users/logout"     // 用户登出
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取请求路径
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        
        // 检查是否是公开路径
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查用户是否已登录
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendUnauthorizedResponse(httpResponse);
            return;
        }

        // 用户已登录，继续处理请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    /**
     * 检查请求路径是否是公开路径
     * @param path 请求路径
     * @return 是否是公开路径
     */
    private boolean isPublicPath(String path) {
        // 检查完全匹配的路径
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }

        // 检查OPTIONS请求（用于CORS预检）
        if (path.startsWith("/api/") && path.endsWith("/")) {
            return true;
        }

        return false;
    }

    /**
     * 发送未认证响应
     * @param response HTTP响应对象
     * @throws IOException 如果写入响应时发生错误
     */
    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", "用户未登录");
        jsonResponse.addProperty("code", HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(new Gson().toJson(jsonResponse));
    }
} 