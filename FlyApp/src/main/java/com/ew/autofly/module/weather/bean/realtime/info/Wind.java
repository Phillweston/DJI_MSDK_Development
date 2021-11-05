
package com.ew.autofly.module.weather.bean.realtime.info;

/**
 *  实时天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */

public class Wind {

    private float direction;
    private float speed;
    public void setDirection(float direction) {
         this.direction = direction;
     }
     public float getDirection() {
         return direction;
     }

    public void setSpeed(float speed) {
         this.speed = speed;
     }
     public float getSpeed() {
         return speed;
     }

}