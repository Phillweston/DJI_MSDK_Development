package com.flycloud.autofly.base.util;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
    public static boolean isEmptyOrNull(String s) {
        return (s == null || s.equalsIgnoreCase(""));
    }

    public static String newGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getTimeStamp() {
        return System.currentTimeMillis() + "";
    }

    public static String getNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(System.currentTimeMillis());
    }

    public static String getNow2() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return formatter.format(System.currentTimeMillis());
    }

    public static String convertTimestampToHourAndMins(long timeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(timeStamp);
    }

    public static String convertTimestampToDate(long timeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.format(timeStamp);
    }

    public static String convertTimestampToYYYYMMDDHHmm(long timeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(timeStamp);
    }

    public static String convertNum2f(double num) {
        return String.format("%.2f", num);


    }

    public static String convertNum(double num) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        return df.format(num);
    }

    /**
     * 判断是否为合法的日期时间字符串
     *
     * @param str_input
     * @param rDateFormat
     * @return boolean;符合为true,不符合为false
     */
    public static boolean isDate(String str_input, String rDateFormat) {
        if (!isEmptyOrNull(str_input)) {
            SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
            formatter.setLenient(false);
            try {
                formatter.format(formatter.parse(str_input));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }


    /**
     * 将指定字符串首字母转换成大写字母
     *
     * @param str 指定字符串
     * @return 返回首字母大写的字符串
     */
    public static String firstCharUpperCase(String str) {
        StringBuffer buffer = new StringBuffer(str);
        if (buffer.length() > 0) {
            char c = buffer.charAt(0);
            buffer.setCharAt(0, Character.toUpperCase(c));
        }
        return buffer.toString();
    }

 /*   public static boolean isURL(String str){

        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return match( regex ,str );


    }*/


    public final static String SPECIAL_CHAR_REGEX="[\\\\/:*?\"<>|]";
    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        Pattern p = Pattern.compile(SPECIAL_CHAR_REGEX);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
