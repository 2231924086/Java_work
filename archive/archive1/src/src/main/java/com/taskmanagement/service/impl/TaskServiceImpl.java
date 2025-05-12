package com.taskmanagement.service.impl;

import com.taskmanagement.dao.TaskDAO;
import com.taskmanagement.dao.impl.TaskDAOImpl;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.CategoryService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;

import java.util.Date;
import java.util.List;

/**
 * 任务服务实现类
 */
public class TaskServiceImpl implements TaskService {
    private final TaskDAO taskDAO;
    private final UserService userService;
    private final CategoryService categoryService;

    public TaskServiceImpl() {
        this.taskDAO = new TaskDAOImpl();
        this.userService = new UserServiceImpl();
        this.categoryService = new CategoryServiceImpl();
    }

    @Override
    public Task createTask(Integer userId, Integer categoryId, String title, String content, 
                          Integer priority, Date dueDate) {
        // 验证用户是否存在
        userService.getUserById(userId);

        // 验证分类是否存在
        if (categoryId != null) {
            categoryService.getCategory(categoryId);
        }

        // 验证优先级
        validatePriority(priority);

        // 创建新任务
        Task task = new Task();
        task.setUserId(userId);
        task.setCategoryId(categoryId);
        task.setTitle(title);
        task.setContent(content);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setStatus(0); // 默认未完成

        // 保存任务
        taskDAO.insert(task);
        return task;
    }

    @Override
    public Task updateTask(Integer taskId, String title, String content, Integer priority, 
                          Date dueDate, Integer categoryId) {
        // 获取现有任务
        Task task = getTask(taskId);

        // 如果修改了分类，验证新分类是否存在
        if (categoryId != null && !categoryId.equals(task.getCategoryId())) {
            categoryService.getCategory(categoryId);
        }

        // 验证优先级
        if (priority != null) {
            validatePriority(priority);
        }

        // 更新任务信息
        if (title != null) task.setTitle(title);
        if (content != null) task.setContent(content);
        if (priority != null) task.setPriority(priority);
        if (dueDate != null) task.setDueDate(dueDate);
        if (categoryId != null) task.setCategoryId(categoryId);

        taskDAO.update(task);
        return task;
    }

    @Override
    public Task updateTaskStatus(Integer taskId, Integer status) {
        // 验证状态值
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("无效的任务状态");
        }

        // 获取任务并更新状态
        Task task = getTask(taskId);
        task.setStatus(status);
        taskDAO.updateStatus(taskId, status);
        return task;
    }

    @Override
    public Task updateTaskPriority(Integer taskId, Integer priority) {
        // 验证优先级
        validatePriority(priority);

        // 获取任务并更新优先级
        Task task = getTask(taskId);
        task.setPriority(priority);
        taskDAO.updatePriority(taskId, priority);
        return task;
    }

    @Override
    public Task updateTaskDueDate(Integer taskId, Date dueDate) {
        // 获取任务并更新截止日期
        Task task = getTask(taskId);
        task.setDueDate(dueDate);
        taskDAO.updateDueDate(taskId, dueDate);
        return task;
    }

    @Override
    public Task updateTaskCategory(Integer taskId, Integer categoryId) {
        // 验证分类是否存在
        if (categoryId != null) {
            categoryService.getCategory(categoryId);
        }

        // 获取任务并更新分类
        Task task = getTask(taskId);
        task.setCategoryId(categoryId);
        taskDAO.updateCategory(taskId, categoryId);
        return task;
    }

    @Override
    public boolean deleteTask(Integer taskId) {
        // 验证任务是否存在
        getTask(taskId);
        return taskDAO.delete(taskId) > 0;
    }

    @Override
    public Task getTask(Integer taskId) {
        Task task = taskDAO.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        return task;
    }

    @Override
    public List<Task> getUserTasks(Integer userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.findByUserId(userId);
    }

    @Override
    public List<Task> getTasksByCategory(Integer userId, Integer categoryId) {
        // 验证用户和分类是否存在
        userService.getUserById(userId);
        categoryService.getCategory(categoryId);
        return taskDAO.findByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Task> getTasksByStatus(Integer userId, Integer status) {
        // 验证状态值
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("无效的任务状态");
        }

        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Task> getTasksByPriority(Integer userId, Integer priority) {
        // 验证优先级
        validatePriority(priority);

        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.findByUserIdAndPriority(userId, priority);
    }

    @Override
    public List<Task> getTasksByDate(Integer userId, Date date) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.findByUserIdAndDueDate(userId, date);
    }

    @Override
    public List<Task> getUpcomingTasks(Integer userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.findUpcomingTasks(userId);
    }

    @Override
    public List<Task> getOverdueTasks(Integer userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.findOverdueTasks(userId);
    }

    @Override
    public List<Task> searchTasks(Integer userId, String keyword) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.searchTasks(userId, keyword);
    }

    @Override
    public int deleteUserTasks(Integer userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return taskDAO.deleteByUserId(userId);
    }

    /**
     * 验证任务优先级
     * @param priority 优先级值
     * @throws IllegalArgumentException 如果优先级无效
     */
    private void validatePriority(Integer priority) {
        if (priority == null || priority < 1 || priority > 3) {
            throw new IllegalArgumentException("无效的任务优先级，必须在1-3之间");
        }
    }
} 