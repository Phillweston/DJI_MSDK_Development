
package com.ew.autofly.module.weather.bean.forecast;

/**
 *  预报天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */
public class WeatherForecastInfo {

    private String status;

    private Result result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {

        private Minutely minutely;
        private Daily daily;
        private int primary;

        public void setMinutely(Minutely minutely) {
            this.minutely = minutely;
        }
        public Minutely getMinutely() {
            return minutely;
        }

        public void setDaily(Daily daily) {
            this.daily = daily;
        }
        public Daily getDaily() {
            return daily;
        }

        public void setPrimary(int primary) {
            this.primary = primary;
        }
        public int getPrimary() {
            return primary;
        }

    }

}