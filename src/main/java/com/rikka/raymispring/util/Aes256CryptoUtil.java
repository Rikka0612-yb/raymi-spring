package com.rikka.raymispring.util;



import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author 晏波
 * 2025/12/24 13:15
 * AES256/CBC/PKCS5Padding 加密工具类
 */
@Component
public class Aes256CryptoUtil {

    // AES密钥，应该是32字节（256位）
    @Value("${encryption.aes.key:ZefCRrRl/AoyAiTbICOeu46yIwNlWkxb}")
    private String aesKey;

    // 向量长度，CBC模式需要16字节
    private static final int IV_LENGTH = 16;

    // 算法/模式/填充
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    /**
     * 加密数据
     * @param plainText 明文
     * @return Base64编码的密文（包含IV）
     */
    public String encrypt(String plainText) {
        try {
            // 验证密钥长度
            validateKey();
            // 生成随机IV
            byte[] iv = generateIv();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // 创建密钥
            SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(CHARSET), ALGORITHM);
            // 初始化加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            // 加密数据
            byte[] encrypted = cipher.doFinal(plainText.getBytes(CHARSET));
            // 将IV和密文拼接，然后Base64编码
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64.encodeBase64String(combined);
        } catch (Exception e) {
            throw new CryptoException("加密失败", e);
        }
    }

    /**
     * 解密数据
     * @param encryptedText Base64编码的密文（包含IV）
     * @return 明文
     */
    public String decrypt(String encryptedText) {
        try {
            // 验证密钥长度
            validateKey();
            // Base64解码
            byte[] combined = Base64.decodeBase64(encryptedText);
            // 提取IV（前16字节）
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // 提取实际密文
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);
            // 创建密钥
            SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(CHARSET), ALGORITHM);
            // 初始化解密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            // 解密数据
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, CHARSET);
        } catch (Exception e) {
            throw new CryptoException("解密失败", e);
        }
    }

    /**
     * 生成随机IV
     */
    private byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 验证密钥长度
     */
    private void validateKey() {
        if (aesKey == null || aesKey.length() != 32) {
            throw new CryptoException("AES密钥必须是32个字符（256位）");
        }
    }

    /**
     * 生成随机的32位密钥（用于初始化配置）
     */
    public static String generateRandomKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.encodeBase64String(keyBytes).substring(0, 32);
    }

    /**
     * 自定义加密异常
     */
    public static class CryptoException extends RuntimeException {
        public CryptoException(String message) {
            super(message);
        }

        public CryptoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
