
package com.ew.autofly.module.weather.bean.realtime.info;

/**
 *  实时天气  (详细参数定义请访问:http://wiki.swarma.net/index.php/彩云天气API/v2)
 *  Created by HYH on 2017/8/29.
 */

public class Precipitation {

    private Nearest nearest;
    private Local local;
    public void setNearest(Nearest nearest) {
         this.nearest = nearest;
     }
     public Nearest getNearest() {
         return nearest;
     }

    public void setLocal(Local local) {
         this.local = local;
     }
     public Local getLocal() {
         return local;
     }

}