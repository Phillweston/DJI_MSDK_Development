package com.ew.autofly.module.media.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class TitleBean extends BaseDataBean {

    private String time;

    @Override
    public int initType() {
        return BaseDataBean.TYPE_TITLE;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
