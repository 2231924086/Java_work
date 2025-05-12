package com.taskmanagement.controller;

import com.taskmanagement.model.Category;
import com.taskmanagement.service.CategoryService;
import com.taskmanagement.service.impl.CategoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/categories/*")
public class CategoryController extends BaseController {
    private final CategoryService categoryService;

    public CategoryController() {
        this.categoryService = new CategoryServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // 创建新分类
            handleCreateCategory(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取当前用户的所有分类
            handleGetUserCategories(request, response);
        } else {
            // 获取指定分类信息
            handleGetCategory(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 更新分类信息
        handleUpdateCategory(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (pathInfo.equals("/user")) {
            // 删除用户的所有分类
            handleDeleteUserCategories(request, response);
        } else {
            // 删除指定分类
            handleDeleteCategory(request, response);
        }
    }

    private void handleCreateCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            Category categoryData = getJsonFromRequest(request, Category.class);
            Category category = categoryService.createCategory(
                userId,
                categoryData.getName(),
                categoryData.getDescription()
            );
            sendSuccessResponse(response, category);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "创建分类失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetUserCategories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            List<Category> categories = categoryService.getCategoriesByUser(userId);
            sendSuccessResponse(response, categories);
        } catch (Exception e) {
            sendErrorResponse(response, "获取分类列表失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        String categoryIdStr = pathInfo.substring(1);
        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            Category category = categoryService.getCategory(categoryId);
            
            // 验证分类是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!category.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权访问该分类", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            sendSuccessResponse(response, category);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的分类ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            sendErrorResponse(response, "获取分类信息失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        String categoryIdStr = pathInfo.substring(1);
        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            Category category = categoryService.getCategory(categoryId);
            
            // 验证分类是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!category.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权修改该分类", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Category categoryData = getJsonFromRequest(request, Category.class);
            Category updatedCategory = categoryService.updateCategory(
                categoryId,
                categoryData.getName(),
                categoryData.getDescription()
            );
            sendSuccessResponse(response, updatedCategory);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的分类ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "更新分类失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDeleteCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        String categoryIdStr = pathInfo.substring(1);
        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            Category category = categoryService.getCategory(categoryId);
            
            // 验证分类是否属于当前用户
            Integer userId = (Integer) session.getAttribute("userId");
            if (!category.getUserId().equals(userId)) {
                sendErrorResponse(response, "无权删除该分类", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            boolean success = categoryService.deleteCategory(categoryId);
            if (success) {
                sendSuccessResponse(response, null);
            } else {
                sendErrorResponse(response, "删除分类失败", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的分类ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendErrorResponse(response, "删除分类失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDeleteUserCategories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, "用户未登录", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            boolean success = categoryService.deleteCategoriesByUser(userId);
            if (success) {
                sendSuccessResponse(response, null);
            } else {
                sendErrorResponse(response, "删除用户分类失败", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            sendErrorResponse(response, "删除用户分类失败：" + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
} 