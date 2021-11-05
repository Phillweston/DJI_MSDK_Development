package com.flycloud.autofly.base.secure;


import java.io.UnsupportedEncodingException;


public class XXTea {

    /**
     * 加密
     * @param plain 明文
     * @param charset 明文字符编码
     * @param hexKey 十六进制字符串形式密钥
     * @return 十六进制字符串形式密文
     * @throws Exception
     */
    public static String encrypt(String plain, String charset, String hexKey)
            throws UnsupportedEncodingException {
        if (plain == null || charset == null || hexKey == null) {
            return null;
        }

        return ByteFormat.toHex(
                encrypt(plain.getBytes(charset), ByteFormat.hexToBytes(hexKey)));
    }

    /**
     * 解密
     * @param cipherHex 十六进制字符串形式密文
     * @param charset 明文字符编码
     * @param hexKey 十六进制字符串形式密钥
     * @return 明文
     * @throws Exception
     */
    public static String decrypt(String cipherHex, String charset, String hexKey) throws UnsupportedEncodingException {
        if (cipherHex == null || charset == null || hexKey == null) {
            return null;
        }
        return new String(
                decrypt(ByteFormat.hexToBytes(cipherHex), ByteFormat.hexToBytes(hexKey)),
                charset);

    }

    /**
     * 加密
     * @param plainData 明文
     * @param key 密钥
     * @return
     */
    public static byte[] encrypt(byte[] plainData, byte[] key) {
        if (plainData == null || plainData.length == 0 || key == null) {
            return null;
        }
        return toByteArray(
                encrypt(toIntArray(plainData, true), toIntArray(key, false)),
                false);
    }

    /**
     * 解密
     * @param cipherData 密文
     * @param key 密钥
     * @return 明文
     */
    public static byte[] decrypt(byte[] cipherData, byte[] key) {
        if (cipherData == null || cipherData.length == 0 || key == null) {
            return null;
        }
        return toByteArray(
                decrypt(toIntArray(cipherData, false), toIntArray(key, false)),
                true);
    }


    private static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n], y = v[0], delta = 0x9E3779B9, sum = 0, e;
        int p, q = 6 + 52 / (n + 1);

        while (q-- > 0) {
            sum = sum + delta;
            e = sum >>> 2 & 3;
            for (p = 0; p < n; p++) {
                y = v[p + 1];
                z = v[p] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y)
                        + (k[p & 3 ^ e] ^ z);
            }
            y = v[0];
            z = v[n] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y)
                    + (k[p & 3 ^ e] ^ z);
        }
        return v;
    }


    private static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n],y = v[0], delta = 0x9E3779B9, sum, e;
        int p, q = 6 + 52 / (n + 1);

        sum = q * delta;
        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y)
                        + (k[p & 3 ^ e] ^ z);
            }
            z = v[n];
            y = v[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y)
                    + (k[p & 3 ^ e] ^ z);
            sum = sum - delta;
        }
        return v;
    }


    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (((data.length & 3) == 0) ? (data.length >>> 2)
                : ((data.length >>> 2) + 1));
        int[] result;

        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        } else {
            result = new int[n];
        }
        n = data.length;
        for (int i = 0; i < n; i++) {
            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }
        return result;
    }


    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;
        if (includeLength) {
            int m = data[data.length - 1];

            if (m > n || m <= 0) {
                return null;
            } else {
                n = m;
            }
        }
        byte[] result = new byte[n];

        for (int i = 0; i < n; i++) {
            result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
        }
        return result;
    }

}
