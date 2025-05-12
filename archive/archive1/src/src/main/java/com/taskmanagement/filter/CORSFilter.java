package com.taskmanagement.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CORSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 允许的源
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        // 允许的HTTP方法
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 允许的请求头
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        // 允许发送认证信息（cookies等）
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        // 预检请求的缓存时间
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // 处理预检请求
        if (httpRequest.getMethod().equals("OPTIONS")) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
} 