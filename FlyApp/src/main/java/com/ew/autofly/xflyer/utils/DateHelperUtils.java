package com.ew.autofly.xflyer.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Deprecated
public class DateHelperUtils {

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

    public static  String getFormatMMSS(Long needFromatTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(needFromatTime);
    }

}