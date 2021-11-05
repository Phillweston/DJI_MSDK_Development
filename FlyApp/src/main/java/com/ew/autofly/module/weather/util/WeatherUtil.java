package com.ew.autofly.module.weather.util;

import android.text.format.Time;

import com.ew.autofly.R;



public class WeatherUtil {

    public final static String SKYCON_CLEAR_DAY = "CLEAR_DAY";
    public final static String SKYCON_CLEAR_NIGHT = "CLEAR_NIGHT";
    public final static String SKYCON_PARTLY_CLOUDY_DAY = "PARTLY_CLOUDY_DAY";
    public final static String SKYCON_PARTLY_CLOUDY_NIGHT = "CLOUDY_NIGHT";
    public final static String SKYCON_CLOUDY = "CLOUDY";
    public final static String SKYCON_RAIN = "RAIN";
    public final static String SKYCON_SNOW = "SNOW";
    public final static String SKYCON_WIND = "WIND";
    public final static String SKYCON_FOG = "FOG";

    /**
     * 获取对应的天气图标
     *
     * @param skyValue
     * @param precipitation
     * @param precipitationType 因为彩云天气接口降雨量标准不统一，第一种标准（0）：0.03-0.25是小雨，0.25-0.35是中雨, 0.35以上是大雨；
     *                          第二种标准（1）：0.05 ~ 0.9 小雨 0.9 ~ 2.87 中雨 >2.87大雨 *具体选择哪一种标准看接口.........无解
     * @return
     */
    public static int getSkyiconId(String skyValue, float precipitation, int precipitationType) {
        int skyIcon = 0;

        switch (skyValue) {
            case SKYCON_CLEAR_DAY:
                skyIcon = R.drawable.skyicon_sunshine;
                break;
            case SKYCON_CLEAR_NIGHT:
                skyIcon = R.drawable.skyicon_sunshine_night;
                break;
            case SKYCON_PARTLY_CLOUDY_DAY:
                skyIcon = R.drawable.skyicon_partly_cloud;
                break;
            case SKYCON_PARTLY_CLOUDY_NIGHT:
                skyIcon = R.drawable.skyicon_partly_cloud_night;
                break;
            case SKYCON_CLOUDY:
                skyIcon = R.drawable.skyicon_cloud;
                break;
            case SKYCON_RAIN:
                if (precipitationType == 0) {
                    if (precipitation <= 0.25f) {
                        skyIcon = R.drawable.skyicon_rain_light;
                    } else if (precipitation <= 0.35f) {
                        skyIcon = R.drawable.skyicon_rain_middle;
                    } else if (precipitation > 0.35f) {
                        skyIcon = R.drawable.skyicon_rain_heavy;
                    }
                } else if (precipitationType == 1) {
                    if (precipitation <= 0.9f) {
                        skyIcon = R.drawable.skyicon_rain_light;
                    } else if (precipitation <= 2.87f) {
                        skyIcon = R.drawable.skyicon_rain_middle;
                    } else if (precipitation > 2.87f) {
                        skyIcon = R.drawable.skyicon_rain_heavy;
                    }
                }

                break;
            case SKYCON_SNOW:
                skyIcon = R.drawable.skyicon_snow;
                break;
            default:
                skyIcon = R.drawable.skyicon_cloud;
                break;
        }

        return skyIcon;
    }

    /**
     * 获取天气状况
     *
     * @param skyValue
     * @param precipitation
     * @param precipitationType 因为彩云天气接口降雨量标准不统一，第一种标准（0）：0.03-0.25是小雨，0.25-0.35是中雨, 0.35以上是大雨；
     *                          第二种标准（1）：0.05 ~ 0.9 小雨 0.9 ~ 2.87 中雨 >2.87大雨 *具体选择哪一种标准看接口.........无解
     * @return
     */
    public static String getSkyStatus(String skyValue, float precipitation, int precipitationType) {
        String skyStatus = "多云";

        switch (skyValue) {
            case SKYCON_CLEAR_DAY:
                skyStatus = "晴天";
                break;
            case SKYCON_CLEAR_NIGHT:
                skyStatus = "晴夜";
                break;
            case SKYCON_PARTLY_CLOUDY_DAY:
                skyStatus = "多云";
                break;
            case SKYCON_PARTLY_CLOUDY_NIGHT:
                skyStatus = "多云";
                break;
            case SKYCON_CLOUDY:
                skyStatus = "阴";
                break;
            case SKYCON_RAIN:
                if (precipitationType == 0) {
                    if (precipitation <= 0.25f) {
                        skyStatus = "小雨";
                    } else if (precipitation <= 0.35f) {
                        skyStatus = "中雨";
                    } else if (precipitation > 0.35f) {
                        skyStatus = "大雨";
                    }
                } else if (precipitationType == 1) {
                    if (precipitation <= 0.9f) {
                        skyStatus = "小雨";
                    } else if (precipitation <= 2.87f) {
                        skyStatus = "中雨";
                    } else if (precipitation > 2.87f) {
                        skyStatus = "大雨";
                    }
                }
                break;
            case SKYCON_SNOW:
                skyStatus = "雪";
                break;
            default:
                skyStatus = "多云";
                break;
        }

        return skyStatus;
    }

    /**
     * 获取当前时间 格式：下午 3:00
     *
     * @return
     */
    public static String getCurrentTime() {
        Time t = new Time();
        t.setToNow();
        int hour = t.hour;
        int minute = t.minute;

        StringBuilder timeStr = new StringBuilder();

        if (hour > 12) {
            hour = hour - 12;
            timeStr.append("下午 ");
        } else {
            timeStr.append("上午 ");
        }

        timeStr.append(hour).append(":");

        if (minute < 10) {
            timeStr.append("0");
        }

        timeStr.append(minute);

        return timeStr.toString();
    }

}
