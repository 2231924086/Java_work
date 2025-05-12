package com.taskmanagement.dao;

import com.taskmanagement.model.Task;
import java.util.Date;
import java.util.List;

/**
 * 任务数据访问接口
 */
public interface TaskDAO extends BaseDAO<Task, Integer> {
    /**
     * 获取用户的所有任务
     * @param userId 用户ID
     * @return 任务列表
     */
    List<Task> findByUserId(Integer userId);

    /**
     * 获取用户指定分类的任务
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 任务列表
     */
    List<Task> findByUserIdAndCategoryId(Integer userId, Integer categoryId);

    /**
     * 获取用户指定状态的任务
     * @param userId 用户ID
     * @param status 任务状态（0:未完成，1:已完成）
     * @return 任务列表
     */
    List<Task> findByUserIdAndStatus(Integer userId, Integer status);

    /**
     * 获取用户指定优先级的任务
     * @param userId 用户ID
     * @param priority 优先级（1:低，2:中，3:高）
     * @return 任务列表
     */
    List<Task> findByUserIdAndPriority(Integer userId, Integer priority);

    /**
     * 获取用户指定截止日期的任务
     * @param userId 用户ID
     * @param dueDate 截止日期
     * @return 任务列表
     */
    List<Task> findByUserIdAndDueDate(Integer userId, Date dueDate);

    /**
     * 获取用户即将到期的任务（截止日期在当前日期之后）
     * @param userId 用户ID
     * @return 任务列表
     */
    List<Task> findUpcomingTasks(Integer userId);

    /**
     * 获取用户已过期的任务（截止日期在当前日期之前且未完成）
     * @param userId 用户ID
     * @return 任务列表
     */
    List<Task> findOverdueTasks(Integer userId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态（0:未完成，1:已完成）
     * @return 影响的行数
     */
    int updateStatus(Integer taskId, Integer status);

    /**
     * 更新任务优先级
     * @param taskId 任务ID
     * @param priority 新优先级（1:低，2:中，3:高）
     * @return 影响的行数
     */
    int updatePriority(Integer taskId, Integer priority);

    /**
     * 更新任务截止日期
     * @param taskId 任务ID
     * @param dueDate 新截止日期
     * @return 影响的行数
     */
    int updateDueDate(Integer taskId, Date dueDate);

    /**
     * 更新任务分类
     * @param taskId 任务ID
     * @param categoryId 新分类ID
     * @return 影响的行数
     */
    int updateCategory(Integer taskId, Integer categoryId);

    /**
     * 搜索任务（根据标题和内容）
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 任务列表
     */
    List<Task> searchTasks(Integer userId, String keyword);

    /**
     * 删除用户的所有任务
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Integer userId);

    /**
     * 删除分类下的所有任务
     * @param categoryId 分类ID
     * @return 影响的行数
     */
    int deleteByCategoryId(Integer categoryId);
} 