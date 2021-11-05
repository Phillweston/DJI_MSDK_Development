
package com.ew.autofly.module.weather.bean.forecast.info;
import java.util.Date;

/**
 *  预报天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */
public class Skycon {

    private Date date;
    private String value;
    public void setDate(Date date) {
         this.date = date;
     }
     public Date getDate() {
         return date;
     }

    public void setValue(String value) {
         this.value = value;
     }
     public String getValue() {
         return value;
     }

}