package com.ew.autofly.module.ars100;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;



public class ARS100Command {


    protected final int head_length = 13;

    protected final int tail_length = 4;

    protected final int fixed_length = head_length + tail_length;


    protected final static byte[] head_start = {0x3C, 0x24, 0x41, 0x52, 0x53};

    protected final static byte[] tail_end = {0x23, 0x3E};


    protected static byte[] createCommand(List<byte[]> header, List<byte[]> body) {

        List<byte[]> commandBytes = new ArrayList<>();

        byte[] crcByte = calculateCRC(header, body);

        commandBytes.add(head_start);
        commandBytes.addAll(header);
        commandBytes.addAll(body);
        commandBytes.add(crcByte);
        commandBytes.add(tail_end);

        ByteBuffer bb = ByteBuffer.allocate(calculateByteLength(commandBytes));
        bb.order(ByteOrder.BIG_ENDIAN);

        for (byte[] b : commandBytes) {
            bb.put(b);
        }
        return bb.array();
    }


    private static byte[] calculateCRC(List<byte[]> header, List<byte[]> body) {

        ByteBuffer bb = ByteBuffer.allocate(calculateByteLength(header) + calculateByteLength(body));
        bb.order(ByteOrder.BIG_ENDIAN);

        for (byte[] b : header) {
            bb.put(b);
        }

        for (byte[] b : body) {
            bb.put(b);
        }

        return UInt16ToBytes(evalCRC16(bb.array()));
    }

    /**
     * 计算byte数组长度
     *
     * @param bytes
     * @return
     */
    protected static int calculateByteLength(List<byte[]> bytes) {
        int totalLen = 0;
        for (byte[] b : bytes) {
            totalLen += b.length;
        }
        return totalLen;
    }

    /**
     * 小字节序(小端模式)
     * <p>
     * 将int数值转换为占2个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    protected static byte[] UInt16ToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 小字节序(小端模式)
     * <p>
     * 将long数值转换为占4个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    protected static byte[] UInt32ToBytes(long value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToUInt8(byte[] src) {
        int value = 0;

        if (src.length > 1) {
            return -1;
        }

        value = src[0] & 0xFF;

        return value;
    }

    /**
     * 小字节序(小端模式)
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序
     *
     * @param src byte数组
     * @return int数值
     */
    public static int bytesToUInt16(byte[] src) {
        int value = 0;

        if (src.length > 2) {
            return -1;
        }

        for (int i = 0; i < src.length; i++) {
            if (i == 0) {
                value = src[0] & 0xFF;
            } else {
                value = value | (src[i] & 0xFF) << i * 8;
            }
        }

        return value & 0x0FFFFFFF;
    }

    /**
     * 小字节序(小端模式)
     * byte数组中取long数值，本方法适用于(低位在前，高位在后)的顺序
     *
     * @param src byte数组
     * @return
     */
    public static long bytesToUInt32(byte[] src) {

        long value = 0;

        if (src.length > 4) {
            return -1;
        }

        for (int i = 0; i < src.length; i++) {
            if (i == 0) {
                value = src[0] & 0xFF;
            } else {
                value = value | (src[i] & 0xFF) << i * 8;
            }
        }

        return value & 0x0FFFFFFFFFFFFFFFL;
    }

    public static long bytesToLong(byte[] input) {

        if (input.length > 8) {
            return -1;
        }


        ByteBuffer buffer = ByteBuffer.wrap(input, 0, 8);



        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return buffer.getLong();
    }

    /**
     * CRC循环校验
     *
     * @param data
     * @return
     */
    public static int evalCRC16(byte[] data) {
        int crc = 0x0000;
        for (int i = 0; i < data.length; i++) {
            crc = (data[i] << 8) ^ crc;
            for (int j = 0; j < 8; ++j)
                if ((crc & 0x8000) != 0)
                    crc = (crc << 1) ^ 0x8005;
                else
                    crc <<= 1;
        }

        return (crc ^ 0x0000) & 0xFFFF;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }
}
