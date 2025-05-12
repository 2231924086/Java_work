package com.taskmanagement.dao.impl;

import com.taskmanagement.dao.TaskDAO;
import com.taskmanagement.model.Task;
import com.taskmanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务数据访问实现类
 */
public class TaskDAOImpl extends BaseDAOImpl<Task, Integer> implements TaskDAO {

    @Override
    protected Task convertToEntity(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getInt("task_id"));
        task.setUserId(rs.getInt("user_id"));
        task.setCategoryId(rs.getInt("category_id"));
        task.setTitle(rs.getString("title"));
        task.setContent(rs.getString("content"));
        task.setPriority(rs.getInt("priority"));
        task.setDueDate(rs.getDate("due_date"));
        task.setStatus(rs.getInt("status"));
        task.setCreatedDate(rs.getTimestamp("created_date"));
        task.setModifiedDate(rs.getTimestamp("modified_date"));
        return task;
    }

    @Override
    protected String getTableName() {
        return "tasks";
    }

    @Override
    protected String getPrimaryKeyColumn() {
        return "task_id";
    }

    @Override
    protected Object[] getEntityValues(Task entity) {
        return new Object[]{
            entity.getUserId(),
            entity.getCategoryId(),
            entity.getTitle(),
            entity.getContent(),
            entity.getPriority(),
            entity.getDueDate(),
            entity.getStatus(),
            entity.getCreatedDate(),
            entity.getModifiedDate()
        };
    }

    @Override
    protected String[] getEntityColumns() {
        return new String[]{
            "user_id",
            "category_id",
            "title",
            "content",
            "priority",
            "due_date",
            "status",
            "created_date",
            "modified_date"
        };
    }

    @Override
    public List<Task> findByUserId(Integer userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ? ORDER BY due_date ASC, priority DESC";
        return executeQueryWithUserId(sql, userId);
    }

    @Override
    public List<Task> findByUserIdAndCategoryId(Integer userId, Integer categoryId) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND category_id = ? ORDER BY due_date ASC, priority DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(convertToEntity(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户分类任务列表失败", e);
        }
    }

    @Override
    public List<Task> findByUserIdAndStatus(Integer userId, Integer status) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND status = ? ORDER BY due_date ASC, priority DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(convertToEntity(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户状态任务列表失败", e);
        }
    }

    @Override
    public List<Task> findByUserIdAndPriority(Integer userId, Integer priority) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND priority = ? ORDER BY due_date ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, priority);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(convertToEntity(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户优先级任务列表失败", e);
        }
    }

    @Override
    public List<Task> findByUserIdAndDueDate(Integer userId, Date dueDate) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND DATE(due_date) = DATE(?) ORDER BY priority DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, new java.sql.Date(dueDate.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(convertToEntity(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户日期任务列表失败", e);
        }
    }

    @Override
    public List<Task> findUpcomingTasks(Integer userId) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND due_date > CURRENT_DATE ORDER BY due_date ASC, priority DESC";
        return executeQueryWithUserId(sql, userId);
    }

    @Override
    public List<Task> findOverdueTasks(Integer userId) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND due_date < CURRENT_DATE AND status = 0 " +
                    "ORDER BY due_date ASC, priority DESC";
        return executeQueryWithUserId(sql, userId);
    }

    @Override
    public int updateStatus(Integer taskId, Integer status) {
        String sql = "UPDATE " + getTableName() + " SET status = ?, modified_date = CURRENT_TIMESTAMP WHERE task_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, status);
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新任务状态失败", e);
        }
    }

    @Override
    public int updatePriority(Integer taskId, Integer priority) {
        String sql = "UPDATE " + getTableName() + " SET priority = ?, modified_date = CURRENT_TIMESTAMP WHERE task_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, priority);
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新任务优先级失败", e);
        }
    }

    @Override
    public int updateDueDate(Integer taskId, Date dueDate) {
        String sql = "UPDATE " + getTableName() + " SET due_date = ?, modified_date = CURRENT_TIMESTAMP WHERE task_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(dueDate.getTime()));
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新任务截止日期失败", e);
        }
    }

    @Override
    public int updateCategory(Integer taskId, Integer categoryId) {
        String sql = "UPDATE " + getTableName() + " SET category_id = ?, modified_date = CURRENT_TIMESTAMP WHERE task_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新任务分类失败", e);
        }
    }

    @Override
    public List<Task> searchTasks(Integer userId, String keyword) {
        String sql = "SELECT * FROM " + getTableName() + 
                    " WHERE user_id = ? AND (title LIKE ? OR content LIKE ?) " +
                    "ORDER BY due_date ASC, priority DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setInt(1, userId);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(convertToEntity(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("搜索任务失败", e);
        }
    }

    @Override
    public int deleteByUserId(Integer userId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除用户任务失败", e);
        }
    }

    @Override
    public int deleteByCategoryId(Integer categoryId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除分类任务失败", e);
        }
    }

    @Override
    public int insert(Task entity) {
        // 设置创建时间和修改时间为当前时间
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(now);
        }
        if (entity.getModifiedDate() == null) {
            entity.setModifiedDate(now);
        }
        // 设置默认状态为未完成
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        return super.insert(entity);
    }

    @Override
    public int update(Task entity) {
        // 更新时自动更新修改时间
        entity.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        return super.update(entity);
    }

    /**
     * 执行带用户ID的查询
     * @param sql SQL语句
     * @param userId 用户ID
     * @return 任务列表
     */
    private List<Task> executeQueryWithUserId(String sql, Integer userId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(convertToEntity(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户任务列表失败", e);
        }
    }
} 