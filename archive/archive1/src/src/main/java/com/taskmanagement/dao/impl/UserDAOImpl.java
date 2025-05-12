package com.taskmanagement.dao.impl;

import com.taskmanagement.dao.UserDAO;
import com.taskmanagement.model.User;
import com.taskmanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问实现类
 */
public class UserDAOImpl extends BaseDAOImpl<User, Integer> implements UserDAO {

    @Override
    protected User convertToEntity(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRegistrationDate(rs.getTimestamp("registration_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setStatus(rs.getBoolean("status"));
        return user;
    }

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected String getPrimaryKeyColumn() {
        return "user_id";
    }

    @Override
    protected Object[] getEntityValues(User entity) {
        return new Object[]{
            entity.getUsername(),
            entity.getPassword(),
            entity.getEmail(),
            entity.getRegistrationDate(),
            entity.getLastLogin(),
            entity.getStatus()
        };
    }

    @Override
    protected String[] getEntityColumns() {
        return new String[]{
            "username",
            "password",
            "email",
            "registration_date",
            "last_login",
            "status"
        };
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertToEntity(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("根据用户名查询用户失败", e);
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertToEntity(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("根据邮箱查询用户失败", e);
        }
    }

    @Override
    public int updateLastLogin(Integer userId) {
        String sql = "UPDATE " + getTableName() + " SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新用户最后登录时间失败", e);
        }
    }

    @Override
    public int updateStatus(Integer userId, boolean status) {
        String sql = "UPDATE " + getTableName() + " SET status = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新用户状态失败", e);
        }
    }

    @Override
    public int updatePassword(Integer userId, String newPassword) {
        String sql = "UPDATE " + getTableName() + " SET password = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新用户密码失败", e);
        }
    }

    @Override
    public int insert(User entity) {
        // 设置注册时间为当前时间
        if (entity.getRegistrationDate() == null) {
            entity.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        }
        // 设置默认状态为活跃
        if (entity.getStatus() == null) {
            entity.setStatus(true);
        }
        return super.insert(entity);
    }
} 