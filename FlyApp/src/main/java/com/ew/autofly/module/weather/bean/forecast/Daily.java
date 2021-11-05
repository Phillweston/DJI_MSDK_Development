
package com.ew.autofly.module.weather.bean.forecast;
import com.ew.autofly.module.weather.bean.forecast.info.Precipitation;
import com.ew.autofly.module.weather.bean.forecast.info.Skycon;
import com.ew.autofly.module.weather.bean.forecast.info.Temperature;

import java.util.List;

/**
 *  预报天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */
public class Daily {

    private String status;
    private List<Temperature> temperature;
    private List<Skycon> skycon;

    private List<Precipitation> precipitation;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Temperature> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<Temperature> temperature) {
        this.temperature = temperature;
    }

    public List<Skycon> getSkycon() {
        return skycon;
    }

    public void setSkycon(List<Skycon> skycon) {
        this.skycon = skycon;
    }

    public List<Precipitation> getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(List<Precipitation> precipitation) {
        this.precipitation = precipitation;
    }
}