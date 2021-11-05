
package com.ew.autofly.module.weather.bean.forecast;
import java.util.List;

/**
 *  预报天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */
public class Minutely {

    private String status;
    private String description;

    private List<Float> precipitation_2h;
    private List<Float> precipitation;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Float> getPrecipitation_2h() {
        return precipitation_2h;
    }

    public void setPrecipitation_2h(List<Float> precipitation_2h) {
        this.precipitation_2h = precipitation_2h;
    }

    public List<Float> getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(List<Float> precipitation) {
        this.precipitation = precipitation;
    }
}