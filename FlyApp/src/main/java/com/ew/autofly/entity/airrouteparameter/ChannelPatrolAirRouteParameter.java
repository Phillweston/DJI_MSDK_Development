package com.ew.autofly.entity.airrouteparameter;

import com.ew.autofly.entity.AirRouteParameter;



public class ChannelPatrolAirRouteParameter extends AirRouteParameter{

    /* 是否反转航线 */
    private boolean isReverse = false;
    /* （通道）末端延长(m) */
    private int channelEndExtend = 10;

    public ChannelPatrolAirRouteParameter(String cameraName) {
        super(cameraName);
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public int getChannelEndExtend() {
        return channelEndExtend;
    }

    public void setChannelEndExtend(int channelEndExtend) {
        this.channelEndExtend = channelEndExtend;
    }
}
