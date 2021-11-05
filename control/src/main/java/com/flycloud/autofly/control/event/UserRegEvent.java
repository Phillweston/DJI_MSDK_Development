package com.flycloud.autofly.control.event;

import com.flycloud.autofly.control.event.message.EventMsg;



public class UserRegEvent {

    public final static EventMsg REGISTER_SUCCESS=new EventMsg(0,"注册成功");
    public final static EventMsg REGISTER_AND_LOGIN_SUCCESS=new EventMsg(1,"注册并登录成功");

    public EventMsg msg;

    public UserRegEvent(EventMsg msg) {
        this.msg = msg;
    }
}
