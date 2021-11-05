package com.flycloud.autofly.base.secure;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密算法
 * Created by fly on 2016/6/1.
 */
public class MD5 {
  
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public MD5() {
    }

  
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
      
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

  
    private static String byteToNum(byte bByte) {
        int iRet = bByte;

        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

  
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String GetMD5Code(String strObj) {
        String resultString = "";
        if (TextUtils.isEmpty(strObj)) {
            return resultString;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
          
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
}
