package com.flycloud.autofly.base.secure;

import java.io.UnsupportedEncodingException;


public class XXTeaLib {


    public static String encrypt(String text, String appSecret) throws UnsupportedEncodingException {
        return XXTea.encrypt(text, "UTF-8",
                ByteFormat.toHex(appSecret.getBytes()));
    }

    public static String decrypt(String text, String appSecret) throws UnsupportedEncodingException {
        return XXTea.decrypt(text, "UTF-8",
                ByteFormat.toHex(appSecret.getBytes()));

    }


}
