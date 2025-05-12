package com.taskmanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    protected static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * 发送成功响应
     * @param response HTTP响应对象
     * @param data 响应数据
     * @throws IOException 如果写入响应时发生错误
     */
    protected void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        sendJsonResponse(response, result);
    }

    /**
     * 发送错误响应
     * @param response HTTP响应对象
     * @param message 错误信息
     * @param code 错误代码
     * @throws IOException 如果写入响应时发生错误
     */
    protected void sendErrorResponse(HttpServletResponse response, String message, int code) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("code", code);
        sendJsonResponse(response, result);
    }

    /**
     * 发送JSON响应
     * @param response HTTP响应对象
     * @param data 要发送的数据
     * @throws IOException 如果写入响应时发生错误
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    /**
     * 获取请求体中的JSON数据
     * @param request HTTP请求对象
     * @param classOfT 目标类型
     * @return 解析后的对象
     * @throws IOException 如果读取请求时发生错误
     */
    protected <T> T getJsonFromRequest(javax.servlet.http.HttpServletRequest request, Class<T> classOfT) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), classOfT);
    }

    /**
     * 获取请求参数，如果参数不存在则返回默认值
     * @param request HTTP请求对象
     * @param paramName 参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    protected String getParameter(javax.servlet.http.HttpServletRequest request, String paramName, String defaultValue) {
        String value = request.getParameter(paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整数类型的请求参数，如果参数不存在或格式错误则返回默认值
     * @param request HTTP请求对象
     * @param paramName 参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    protected Integer getIntParameter(javax.servlet.http.HttpServletRequest request, String paramName, Integer defaultValue) {
        String value = request.getParameter(paramName);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
} 