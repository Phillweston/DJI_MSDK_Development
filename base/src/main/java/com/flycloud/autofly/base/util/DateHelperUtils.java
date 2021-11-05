package com.flycloud.autofly.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelperUtils {

    
    private static final int HOUR_SECOND = 60 * 60;

    
    private static final int MINUTE_SECOND = 60;


    public static Date string2DateTime(String strDate) {
        if (strDate == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = null;
        try {
            dt = sdf.parse(strDate);
        } catch (ParseException localParseException) {
        }
        return dt;
    }

    public static String format(Date date){
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String format) {
        if (date == null)
            return null;
        if ((format == null) || (format == "")) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        String result = null;
        DateFormat df = new SimpleDateFormat(format);
        result = df.format(date);
        return result;
    }

    public static String getSystemTime() {
        return format(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateSeries() {
        return format(new Date(), "yyyyMMddHHmmss");
    }

    public static String getMonthDayHourMinute() {
        return format(new Date(), "MM/dd HH:mm");
    }

    public static String getYearMonthDay() {
        return format(new Date(), "yyyy-MM-dd");
    }

    /**
     * 转换为0小时0分0秒格式
     * @return
     */
    public static String formatTimeByHMS(int second){
        if(second>3600){
            return second/3600+"小时"+(second%3600)/60+"分"+(second%3600)%60+"秒";
        }else if(second>60){
            return second/60+"分"+second%60+"秒";
        }else if(second>0){
            return second+"秒";
        }else {
            return "0秒";
        }
    }

    /**
     * 转换为0小时0分钟格式
     * @return
     */
    public static String formatTimeByHM(int second){
        if(second>3600){
            return second/3600+"小时"+(second%3600)/60+"分";
        }else if(second>60){
            return second/60+"分钟";
        }else {
            return "0分钟";
        }
    }

    /**
     * 根据秒数获取时间串
     * @param second (eg: 100s)
     * @return (eg:00:01:40)
     */
    public static String formatTimeByHMS00(long second) {
        if (second <= 0) {

            return "00:00:00";
        }

        int hours = (int) (second / HOUR_SECOND);
        if (hours > 0) {

            second -= hours * HOUR_SECOND;
        }

        int minutes = (int) (second / MINUTE_SECOND);
        if (minutes > 0) {

            second -= minutes * MINUTE_SECOND;
        }

        return (hours >= 10 ? (hours + "")
                : ("0" + hours) + ":" + (minutes >= 10 ? (minutes + "") : ("0" + minutes)) + ":"
                + (second >= 10 ? (second + "") : ("0" + second)));
    }

    /**
     * 转换成年月日格式
     * @param time
     * @return
     */
    public static String formatTimeByYMD(long time){
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         return dateFormat.format(time);
    }

}