package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.impl.TaskServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/api/tasks/*")
public class TaskController extends BaseController {
    private final TaskService taskService;

    public TaskController() {
        this.taskService = new TaskServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // 创建新任务
            handleCreateTask(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取任务列表（支持多种过滤条件）
            handleGetTasks(request, response);
        } else if (pathInfo.equals("/upcoming")) {
            // 获取即将到期的任务
            handleGetUpcomingTasks(request, response);
        } else if (pathInfo.equals("/search")) {
            // 搜索任务
            handleSearchTasks(request, response);
        } else {
            // 获取指定任务信息
            handleGetTask(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (pathInfo.equals("/status")) {
            // 更新任务状态
            handleUpdateTaskStatus(request, response);
        } else if (pathInfo.equals("/priority")) {
            // 更新任务优先级
            handleUpdateTaskPriority(request, response);
        } else {
            // 更新任务信息
            handleUpdateTask(request, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (pathInfo.equals("/user")) {
            // 删除用户的所有任务
            handleDeleteUserTasks(request, response);
        } else {
            // 删除指定任务
            handleDeleteTask(request, response);
        }
    }

    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            Task taskData = getJsonFromRequest(request, Task.class);
            Task task = taskService.createTask(
                userId,
                taskData.getTitle(),
                taskData.getContent(),
                taskData.getPriority(),
                taskData.getDueDate(),
                taskData.getCategoryId()
            );
            sendSuccessResponse(response, task);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "创建任务失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            Integer categoryId = getIntParameter(request, "categoryId", null);
            String status = getParameter(request, "status", null);
            Integer priority = getIntParameter(request, "priority", null);
            String startDate = getParameter(request, "startDate", null);
            String endDate = getParameter(request, "endDate", null);

            List<Task> tasks;
            if (categoryId != null) {
                tasks = taskService.getTasksByCategory(categoryId);
            } else if (status != null) {
                tasks = taskService.getTasksByStatus(userId, status);
            } else if (priority != null) {
                tasks = taskService.getTasksByPriority(userId, priority);
            } else if (startDate != null && endDate != null) {
                tasks = taskService.getTasksByDateRange(userId, startDate, endDate);
            } else {
                tasks = taskService.getTasksByUser(userId);
            }
            sendSuccessResponse(response, tasks);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "获取任务列表失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetUpcomingTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            Integer days = getIntParameter(request, "days", 7);
            List<Task> tasks = taskService.getUpcomingTasks(userId, days);
            sendSuccessResponse(response, tasks);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "获取即将到期任务失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSearchTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            String keyword = getParameter(request, "keyword", "");
            List<Task> tasks = taskService.searchTasks(userId, keyword);
            sendSuccessResponse(response, tasks);
        } catch (Exception e) {
            sendErrorResponse(response, "搜索任务失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        String taskIdStr = pathInfo.substring(1);
        try {
            Integer taskId = Integer.parseInt(taskIdStr);
            Task task = taskService.getTask(taskId);
            
            // 验证任务是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!task.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权访问该任务", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            sendSuccessResponse(response, task);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的任务ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            sendErrorResponse(response, "获取任务信息失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        String taskIdStr = pathInfo.substring(1);
        try {
            Integer taskId = Integer.parseInt(taskIdStr);
            Task task = taskService.getTask(taskId);
            
            // 验证任务是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!task.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权修改该任务", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Task taskData = getJsonFromRequest(request, Task.class);
            Task updatedTask = taskService.updateTask(
                taskId,
                taskData.getTitle(),
                taskData.getContent(),
                taskData.getPriority(),
                taskData.getDueDate(),
                taskData.getCategoryId()
            );
            sendSuccessResponse(response, updatedTask);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的任务ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "更新任务失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateTaskStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Task taskData = getJsonFromRequest(request, Task.class);
            Task task = taskService.getTask(taskData.getTaskId());
            
            // 验证任务是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!task.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权修改该任务状态", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Task updatedTask = taskService.updateTaskStatus(taskData.getTaskId(), taskData.getStatus());
            sendSuccessResponse(response, updatedTask);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "更新任务状态失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateTaskPriority(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Task taskData = getJsonFromRequest(request, Task.class);
            Task task = taskService.getTask(taskData.getTaskId());
            
            // 验证任务是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!task.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权修改该任务优先级", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Task updatedTask = taskService.updateTaskPriority(taskData.getTaskId(), taskData.getPriority());
            sendSuccessResponse(response, updatedTask);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "更新任务优先级失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDeleteTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        String taskIdStr = pathInfo.substring(1);
        try {
            Integer taskId = Integer.parseInt(taskIdStr);
            Task task = taskService.getTask(taskId);
            
            // 验证任务是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!task.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权删除该任务", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            boolean success = taskService.deleteTask(taskId);
            if (success) {
                sendSuccessResponse(response, null);
            } else {
                sendErrorResponse(response, "删除任务失败", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的任务ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "删除任务失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDeleteUserTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            boolean success = taskService.deleteTasksByUser(userId);
            if (success) {
                sendSuccessResponse(response, null);
            } else {
                sendErrorResponse(response, "删除用户任务失败", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            sendErrorResponse(response, "删除用户任务失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
} 