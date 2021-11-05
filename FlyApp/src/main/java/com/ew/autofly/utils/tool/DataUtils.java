package com.ew.autofly.utils.tool;

import android.text.TextUtils;

import com.ew.autofly.utils.java.BytesUtil;
import com.flycloud.autofly.base.secure.DesUtil;
import com.flycloud.autofly.base.util.DateHelperUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class DataUtils {

  
  

    private final static String splitSign = "@%&";
    private final static String desKey = "pIbJLMNbrRcMyULMYTfBVjcf";
  
    private final static String dataKey = "Mirupdqo";

    /**
     * 读取加密数据
     *
     * @param filePath 文件路径
     * @return
     */
    public static boolean saveData(String filePath, String data) {
        boolean isWriteSuccess = false;
        try {
            String encrypt = DataUtils.encrypt(data);
            File encFile = new File(filePath);
            if (!encFile.exists()) {
                encFile.createNewFile();
            }

            OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(encFile), "GBK");
            fos.write(encrypt);

            fos.flush();
            fos.close();

            isWriteSuccess = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isWriteSuccess;
    }

    /**
     * 读取加密数据
     *
     * @param filePath 文件路径
     * @return
     */
    public static Data readData(String filePath) {

        int headParamsLength = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            StringBuilder fileSB = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                fileSB.append(str);
            }
            String fileStr = fileSB.toString();
            if (fileStr != null) {
                String[] split = fileStr.split(splitSign);
                if (split.length == 3) {

                    headParamsLength = fileStr.length() - split[split.length - 1].length();

                    if (split[0].equals("<HEAD>")) {
                        String params = split[1];
                        String data = split[2];

                        if (TextUtils.isEmpty(params) || TextUtils.isEmpty(data)) {
                            return null;
                        }

                        String paramsDec = DesUtil.decrypt(desKey, params);

                        if (TextUtils.isEmpty(paramsDec)) {
                            return null;
                        }

                        String[] paramsSplit = paramsDec.split("&");

                        if (paramsSplit.length >= 3) {
                            String version = paramsSplit[0];
                            String timestamp = paramsSplit[1];
                            String appid = paramsSplit[2];


                            Data d = new Data();
                            if (paramsSplit.length >= 4) {
                                String isMultiMissionStr = paramsSplit[3];
                                if ("isMultiMission=true".equals(isMultiMissionStr)) {
                                    d.setMultiMission(true);
                                } else {
                                    d.setMultiMission(false);
                                }
                            }

                            byte[] dataFromFile = getDataFromFile(filePath);

                            byte[] dataBytes = BytesUtil.readBytes(dataFromFile, headParamsLength, dataFromFile.length - headParamsLength);

                            String xor = new String(xorDecrypt(dataBytes, dataKey.getBytes("GBK")), "GBK");
                            d.setData(xor);
                            return d;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Data readDataNoEncrypt(String filePath) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            StringBuilder fileSB = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                fileSB.append(str);
            }
            String fileStr = fileSB.toString();
            Data d = new Data();
            d.setMultiMission(true);
            d.setData(fileStr);
            return d;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 加密字符串
     *
     * @param jsonData 加密的内容
     * @return
     */
    private static String encrypt(String jsonData) throws Exception {

        String timestamp = DateHelperUtils.getDateSeries();

        String paramsStr = "version=V1.4&timestamp=" + timestamp + "&appid=" + "FLY-PRO" + "&isMultiMission=false";
        String params = DesUtil.encrypt(desKey, paramsStr);
        String data = new String(xorEncrypt(jsonData.getBytes("GBK"), dataKey.getBytes("GBK")), "GBK");
        String encryptStr = "<HEAD>" + splitSign + params + splitSign + data;
        return encryptStr;
    }


    private static byte[] xorEncrypt(byte[] bytes, byte[] key) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] ^= key[i % key.length];
        }
        return bytes;
    }


    private static byte[] xorDecrypt(byte[] bytes, byte[] key) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] ^= key[i % key.length];
        }
        return bytes;
    }

    private static byte[] getDataFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
      
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }


    public static class Data {
        private boolean isMultiMission;
        private String data;

        public boolean isMultiMission() {
            return isMultiMission;
        }

        public void setMultiMission(boolean multiMission) {
            isMultiMission = multiMission;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

}
