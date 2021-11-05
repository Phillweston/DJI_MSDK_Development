
package com.ew.autofly.module.weather.bean.realtime;
import com.ew.autofly.module.weather.bean.realtime.info.Precipitation;
import com.ew.autofly.module.weather.bean.realtime.info.Wind;

/**
 *  实时天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */
public class WeatherRealTimeInfo {

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

        private String status;
        private int o3;
        private float co;
        private float temperature;
        private int pm10;
        private String skycon;
        private float cloudrate;
        private int aqi;
        private int no2;
        private float humidity;
        private float pres;
        private int pm25;
        private int so2;
        private Precipitation precipitation;
        private Wind wind;
        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

        public int getO3() {
            return o3;
        }

        public void setO3(int o3) {
            this.o3 = o3;
        }

        public float getCo() {
            return co;
        }

        public void setCo(float co) {
            this.co = co;
        }

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }

        public int getPm10() {
            return pm10;
        }

        public void setPm10(int pm10) {
            this.pm10 = pm10;
        }

        public String getSkycon() {
            return skycon;
        }

        public void setSkycon(String skycon) {
            this.skycon = skycon;
        }

        public float getCloudrate() {
            return cloudrate;
        }

        public void setCloudrate(float cloudrate) {
            this.cloudrate = cloudrate;
        }

        public int getAqi() {
            return aqi;
        }

        public void setAqi(int aqi) {
            this.aqi = aqi;
        }

        public int getNo2() {
            return no2;
        }

        public void setNo2(int no2) {
            this.no2 = no2;
        }

        public float getHumidity() {
            return humidity;
        }

        public void setHumidity(float humidity) {
            this.humidity = humidity;
        }

        public float getPres() {
            return pres;
        }

        public void setPres(float pres) {
            this.pres = pres;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }

        public int getSo2() {
            return so2;
        }

        public void setSo2(int so2) {
            this.so2 = so2;
        }

        public Precipitation getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(Precipitation precipitation) {
            this.precipitation = precipitation;
        }

        public Wind getWind() {
            return wind;
        }

        public void setWind(Wind wind) {
            this.wind = wind;
        }
    }
}