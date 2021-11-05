package com.ew.autofly.entity;

import java.io.Serializable;


public class DeviceInfo implements Serializable {

  
    private String timestamp;
  
    private String imei;
  
    private int os;
  
    private int version_no;
  
    private String version_name;
  
    private String channel_name;
  
    private String app_code;
  
    private String app_name;
  
    private String phone_uid;
  
    private String sdk_version;
  
    private String phone_fingerprint;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public int getVersion_no() {
        return version_no;
    }

    public void setVersion_no(int version_no) {
        this.version_no = version_no;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getApp_code() {
        return app_code;
    }

    public void setApp_code(String app_code) {
        this.app_code = app_code;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getPhone_uid() {
        return phone_uid;
    }

    public void setPhone_uid(String phone_uid) {
        this.phone_uid = phone_uid;
    }

    public String getSdk_version() {
        return sdk_version;
    }

    public void setSdk_version(String sdk_version) {
        this.sdk_version = sdk_version;
    }

    public String getPhone_fingerprint() {
        return phone_fingerprint;
    }

    public void setPhone_fingerprint(String phone_fingerprint) {
        this.phone_fingerprint = phone_fingerprint;
    }
}
