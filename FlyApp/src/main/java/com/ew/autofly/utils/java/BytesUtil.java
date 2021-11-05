package com.ew.autofly.utils.java;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;




public class BytesUtil {

    /**
     * 向将bytes添加到另一个bytes结尾，并返回位置
     *
     * @param buff 目标数组
     * @param pos  目标数组放置的起始位置
     * @param lens 添加的长度
     * @param dx   要添加的数组
     * @return lens添加的长度
     */
    public static int addToBuff(byte[] buff, int pos, int lens, byte[] dx) {
        System.arraycopy(dx, 0, buff, pos, lens);
        return lens;
    }

    /**
     * 两个数组合并
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    /**
     * 获得bytes的一段数据
     *
     * @param source   原byte数组
     * @param startPos 起始位置
     * @param length   获取的长度
     * @return 返回获得的byte数组
     */
    public static byte[] readBytes(byte[] source, int startPos, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, startPos, result, 0, length);
        return result;
    }

    /**
     * double转byte数组，小端模式
     *
     * @param d
     * @return
     */
    public static byte[] doubleToBytes_Little(double d) {
        long l = Double.doubleToLongBits(d);
        byte b[] = new byte[8];
        b[7] = (byte) (0xff & (l >> 56));
        b[6] = (byte) (0xff & (l >> 48));
        b[5] = (byte) (0xff & (l >> 40));
        b[4] = (byte) (0xff & (l >> 32));
        b[3] = (byte) (0xff & (l >> 24));
        b[2] = (byte) (0xff & (l >> 16));
        b[1] = (byte) (0xff & (l >> 8));
        b[0] = (byte) (0xff & l);
        return b;
    }

    /**
     * double转byte数组，大端模式
     *
     * @param d
     * @return
     */
    public static byte[] doubleToBytes_Big(double d) {
        long l = Double.doubleToLongBits(d);
        byte b[] = new byte[8];
        b[0] = (byte) (0xff & (l >> 56));
        b[1] = (byte) (0xff & (l >> 48));
        b[2] = (byte) (0xff & (l >> 40));
        b[3] = (byte) (0xff & (l >> 32));
        b[4] = (byte) (0xff & (l >> 24));
        b[5] = (byte) (0xff & (l >> 16));
        b[6] = (byte) (0xff & (l >> 8));
        b[7] = (byte) (0xff & l);
        return b;
    }

    public static float bytesToFloat_Little(byte[] bytes) {
        return bytesToFloat(bytes, true);
    }

    public static float bytesToFloat_Big(byte[] bytes) {
        return bytesToFloat(bytes, false);
    }


    public static float bytesToFloat(byte[] bytes, boolean littleEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 4);
        if (littleEndian) {
          
          
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        int l = buffer.getInt();
        return Float.intBitsToFloat(l);
    }


    public static double bytesToDouble(byte[] bytes, boolean littleEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 8);
        if (littleEndian) {
          
          
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        long l = buffer.getLong();
        return Double.longBitsToDouble(l);
    }

    /**
     * long转byte数组，小端模式
     *
     * @param l
     * @return
     */
    public static byte[] longToBytes_Little(long l) {
        byte b[] = new byte[8];
        b[7] = (byte) (0xff & (l >> 56));
        b[6] = (byte) (0xff & (l >> 48));
        b[5] = (byte) (0xff & (l >> 40));
        b[4] = (byte) (0xff & (l >> 32));
        b[3] = (byte) (0xff & (l >> 24));
        b[2] = (byte) (0xff & (l >> 16));
        b[1] = (byte) (0xff & (l >> 8));
        b[0] = (byte) (0xff & l);
        return b;
    }

    /**
     * long转byte数组，大端模式
     *
     * @param l
     * @return
     */
    public static byte[] longToBytes_Big(long l) {
        byte b[] = new byte[8];
        b[0] = (byte) (0xff & (l >> 56));
        b[1] = (byte) (0xff & (l >> 48));
        b[2] = (byte) (0xff & (l >> 40));
        b[3] = (byte) (0xff & (l >> 32));
        b[4] = (byte) (0xff & (l >> 24));
        b[5] = (byte) (0xff & (l >> 16));
        b[6] = (byte) (0xff & (l >> 8));
        b[7] = (byte) (0xff & l);
        return b;
    }

    /**
     * byte数组转long
     *
     * @param bytes        8位的byte数组
     * @param littleEndian 是否是小端模式
     * @return
     * @throws Exception
     */
    public static long bytesToLong(byte[] bytes, boolean littleEndian) throws Exception {
        if (bytes.length != 8) {
            throw new Exception("参数错误，无法解析。");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 8);
        if (littleEndian) {
          
          
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buffer.getLong();
    }

    /**
     * int转byte数组  ,小端
     *
     * @param num
     * @return
     */
    public static byte[] intToBytes_Little(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 0) & 0xff);
        result[1] = (byte) ((num >>> 8) & 0xff);
        result[2] = (byte) ((num >>> 16) & 0xff);
        result[3] = (byte) ((num >>> 24) & 0xff);
        return result;
    }

    /**
     * int转byte数组 ,大端
     *
     * @param num
     * @return
     */
    public static byte[] intToBytes_Big(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 24) & 0xff);
        result[1] = (byte) ((num >>> 16) & 0xff);
        result[2] = (byte) ((num >>> 8) & 0xff);
        result[3] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    /**
     * byte数组转int,小端
     *
     * @param bytes
     * @return
     */
    public static int bytesToInt_Little(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 0;
            int b = (bytes[1] & 0xff) << 8;
            int c = (bytes[2] & 0xff) << 16;
            int d = (bytes[3] & 0xff) << 24;
            result = a | b | c | d;
        }
        return result;
    }

    /**
     * byte数组转int,大端
     *
     * @param bytes
     * @return
     */
    public static int bytesToInt_Big(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff) << 0;
            result = a | b | c | d;
        }
        return result;
    }

    /**
     * byte数组转short int,小端
     *
     * @param bytes
     * @return
     */
    public static int bytesToShort_Little(byte[] bytes) {
        int result = 0;
        if (bytes.length == 2) {
            int a = (bytes[0] & 0xff) << 0;
            int b = (bytes[1] & 0xff) << 8;
            result = a | b ;
        }
        return result;
    }

    /**
     * short转byte数组  ,小端
     *
     * @param num
     * @return
     */
    public static byte[] shortToBytes_Little(int num) {
        byte[] result = new byte[2];
        result[0] = (byte) ((num >>> 0) & 0xff);
        result[1] = (byte) ((num >>> 8) & 0xff);
        return result;
    }

    /**
     * byte数组转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }

    /**
     * 十六进制转byte数组
     *
     * @param str
     * @return
     */
    public static byte[] hexToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public static String getString(byte[] bytes) {
        if (null == bytes) {
            return "";
        }
      
        byte zero = 0x00;
        byte no = (byte) 0xFF;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == zero || bytes[i] == no) {
                bytes = readBytes(bytes, 0, i);
                break;
            }
        }
        return getString(bytes, "GBK");
    }

    private static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static byte[] getBytes(String data) {
        return getBytes(data, "GBK");
    }


    private static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }


    public static String getStringUTF8(byte[] bytes, int start, int length) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
      
        byte zero = 0x00;
        for (int i = start; i < length && i < bytes.length; i++) {
            if (bytes[i] == zero) {
                length = i - start;
                break;
            }
        }
        return getString(bytes, start, length, "UTF-8");
    }

    private static String getString(byte[] bytes, int start, int length, String charsetName) {
        return new String(bytes, start, length, Charset.forName(charsetName));
    }
}
