package com.ew.autofly.module.media.bean;

import android.os.Parcel;
import android.os.Parcelable;

import dji.sdk.media.MediaFile;


 public class AlbumBean extends BaseDataBean {

    private MediaFile mediaFile;
    private boolean isChecked;


    @Override
    public int initType() {
        return BaseDataBean.TYPE_MEDIA;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
