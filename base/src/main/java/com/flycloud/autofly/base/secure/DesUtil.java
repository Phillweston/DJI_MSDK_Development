package com.flycloud.autofly.base.secure;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;



public class DesUtil {

    public static String decrypt(String key, String src) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("utf-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.DECRYPT_MODE, securekey);

        byte[] retByte = cipher.doFinal(Base64.getDecoder().decode(src.getBytes("utf-8")));
        return new String(retByte, "utf-8");
    }

    public static String decrypt(String key, byte[] bytes) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("utf-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.DECRYPT_MODE, securekey);

        byte[] retByte = cipher.doFinal(Base64.getDecoder().decode(bytes));
        return new String(retByte, "utf-8");
    }

    public static String encrypt(String key, String src) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("utf-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);

        byte[] retByte = cipher.doFinal(src.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(retByte);
    }
}
