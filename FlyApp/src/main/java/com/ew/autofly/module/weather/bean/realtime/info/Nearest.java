
package com.ew.autofly.module.weather.bean.realtime.info;

/**
 *  实时天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */

public class Nearest {

    private String status;
    private float distance;
    private float intensity;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}