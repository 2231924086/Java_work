package com.taskmanagement.controller;

import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserController extends BaseController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // 注册新用户
            handleRegister(request, response);
        } else if (pathInfo.equals("/login")) {
            // 用户登录
            handleLogin(request, response);
        } else if (pathInfo.equals("/logout")) {
            // 用户登出
            handleLogout(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有用户
            handleGetAllUsers(request, response);
        } else if (pathInfo.equals("/current")) {
            // 获取当前登录用户信息
            handleGetCurrentUser(request, response);
        } else {
            // 获取指定用户信息
            handleGetUser(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (pathInfo.equals("/password")) {
            // 更新密码
            handleUpdatePassword(request, response);
        } else {
            // 更新用户信息
            handleUpdateUser(request, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 删除用户
        handleDeleteUser(request, response);
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User userData = getJsonFromRequest(request, User.class);
            User user = userService.register(
                userData.getUsername(),
                userData.getPassword(),
                userData.getEmail(),
                userData.getPhone()
            );
            sendSuccessResponse(response, user);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "注册失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User loginData = getJsonFromRequest(request, User.class);
            User user = userService.login(loginData.getUsername(), loginData.getPassword());
            
            // 创建会话
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            
            sendSuccessResponse(response, user);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            sendErrorResponse(response, "登录失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sendSuccessResponse(response, null);
    }

    private void handleGetAllUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<User> users = userService.getAllUsers();
            sendSuccessResponse(response, users);
        } catch (Exception e) {
            sendErrorResponse(response, "获取用户列表失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            User user = userService.getUserById(userId);
            sendSuccessResponse(response, user);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            sendErrorResponse(response, "获取用户信息失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String userIdStr = pathInfo.substring(1);
        try {
            Integer userId = Integer.parseInt(userIdStr);
            User user = userService.getUserById(userId);
            sendSuccessResponse(response, user);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的用户ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            sendErrorResponse(response, "获取用户信息失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            User userData = getJsonFromRequest(request, User.class);
            User user = userService.updateUser(userId, userData.getEmail(), userData.getPhone());
            sendSuccessResponse(response, user);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "更新用户信息失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdatePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            User passwordData = getJsonFromRequest(request, User.class);
            boolean success = userService.updatePassword(userId, passwordData.getPassword(), passwordData.getNewPassword());
            if (success) {
                sendSuccessResponse(response, null);
            } else {
                sendErrorResponse(response, "更新密码失败", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "更新密码失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            boolean success = userService.deleteUser(userId);
            if (success) {
                session.invalidate();
                sendSuccessResponse(response, null);
            } else {
                sendErrorResponse(response, "删除用户失败", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "删除用户失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
} 