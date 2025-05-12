package com.taskmanagement.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 */
public class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    /**
     * 生成随机盐值
     * @param length 盐值长度
     * @return 盐值字符串
     */
    private static String generateSalt(int length) {
        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            salt.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return salt.toString();
    }

    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码（包含盐值）
     */
    public static String encrypt(String password) {
        try {
            // 生成盐值
            String salt = generateSalt(16);
            
            // 使用SHA-256进行加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // 将盐值和加密后的密码组合
            String encodedPassword = Base64.getEncoder().encodeToString(hashedPassword);
            return salt + ":" + encodedPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     * @param password 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String password, String encryptedPassword) {
        try {
            // 分离盐值和加密后的密码
            String[] parts = encryptedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String salt = parts[0];
            String storedPassword = parts[1];
            
            // 使用相同的盐值加密输入的密码
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            String encodedPassword = Base64.getEncoder().encodeToString(hashedPassword);
            
            // 比较加密后的密码
            return storedPassword.equals(encodedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码验证失败", e);
        }
    }
} 