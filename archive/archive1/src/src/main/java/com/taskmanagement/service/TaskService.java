package com.taskmanagement.service;

import com.taskmanagement.model.Task;
import java.util.Date;
import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {
    /**
     * 创建新任务
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param title 任务标题
     * @param content 任务内容
     * @param priority 优先级（1:低，2:中，3:高）
     * @param dueDate 截止日期
     * @return 创建的任务对象
     * @throws IllegalArgumentException 如果用户或分类不存在
     */
    Task createTask(Integer userId, Integer categoryId, String title, String content, 
                   Integer priority, Date dueDate);

    /**
     * 更新任务信息
     * @param taskId 任务ID
     * @param title 新标题
     * @param content 新内容
     * @param priority 新优先级
     * @param dueDate 新截止日期
     * @param categoryId 新分类ID
     * @return 更新后的任务对象
     * @throws IllegalArgumentException 如果任务不存在或分类不存在
     */
    Task updateTask(Integer taskId, String title, String content, Integer priority, 
                   Date dueDate, Integer categoryId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态（0:未完成，1:已完成）
     * @return 更新后的任务对象
     * @throws IllegalArgumentException 如果任务不存在
     */
    Task updateTaskStatus(Integer taskId, Integer status);

    /**
     * 更新任务优先级
     * @param taskId 任务ID
     * @param priority 新优先级（1:低，2:中，3:高）
     * @return 更新后的任务对象
     * @throws IllegalArgumentException 如果任务不存在
     */
    Task updateTaskPriority(Integer taskId, Integer priority);

    /**
     * 更新任务截止日期
     * @param taskId 任务ID
     * @param dueDate 新截止日期
     * @return 更新后的任务对象
     * @throws IllegalArgumentException 如果任务不存在
     */
    Task updateTaskDueDate(Integer taskId, Date dueDate);

    /**
     * 更新任务分类
     * @param taskId 任务ID
     * @param categoryId 新分类ID
     * @return 更新后的任务对象
     * @throws IllegalArgumentException 如果任务或分类不存在
     */
    Task updateTaskCategory(Integer taskId, Integer categoryId);

    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 是否删除成功
     * @throws IllegalArgumentException 如果任务不存在
     */
    boolean deleteTask(Integer taskId);

    /**
     * 获取任务信息
     * @param taskId 任务ID
     * @return 任务对象
     * @throws IllegalArgumentException 如果任务不存在
     */
    Task getTask(Integer taskId);

    /**
     * 获取用户的所有任务
     * @param userId 用户ID
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> getUserTasks(Integer userId);

    /**
     * 获取用户指定分类的任务
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户或分类不存在
     */
    List<Task> getTasksByCategory(Integer userId, Integer categoryId);

    /**
     * 获取用户指定状态的任务
     * @param userId 用户ID
     * @param status 任务状态（0:未完成，1:已完成）
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> getTasksByStatus(Integer userId, Integer status);

    /**
     * 获取用户指定优先级的任务
     * @param userId 用户ID
     * @param priority 优先级（1:低，2:中，3:高）
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> getTasksByPriority(Integer userId, Integer priority);

    /**
     * 获取用户指定日期的任务
     * @param userId 用户ID
     * @param date 日期
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> getTasksByDate(Integer userId, Date date);

    /**
     * 获取用户即将到期的任务
     * @param userId 用户ID
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> getUpcomingTasks(Integer userId);

    /**
     * 获取用户已过期的任务
     * @param userId 用户ID
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> getOverdueTasks(Integer userId);

    /**
     * 搜索任务
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 任务列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Task> searchTasks(Integer userId, String keyword);

    /**
     * 删除用户的所有任务
     * @param userId 用户ID
     * @return 删除的任务数量
     * @throws IllegalArgumentException 如果用户不存在
     */
    int deleteUserTasks(Integer userId);
} 