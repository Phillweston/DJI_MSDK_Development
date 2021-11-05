package com.flycloud.autofly.control.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;



public interface IUserService extends IProvider {

    boolean isSkipLogin();

    void login(Context context, String phone, String password);

    void logout();

    String getUserName();

    String getUserToken();

    /**
     * 获取用户头像
     * @return
     */
    String getUserAvatar();

    long getUserID();
}
