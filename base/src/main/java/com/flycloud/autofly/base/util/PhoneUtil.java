package com.flycloud.autofly.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class PhoneUtil {

    /**
     * 是否是正确的手机号码
     *
     * @param num
     * @return
     */
    public static boolean isMobileNum(String num) {
        String reg = "^0?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[0-9])[0-9]{8}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(num);
        return m.matches();
    }
}
