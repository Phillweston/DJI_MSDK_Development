package com.flycloud.autofly.control.event;

import com.flycloud.autofly.control.event.message.EventMsg;



public class LoginEvent {

    public final static EventMsg SUCCESS=new EventMsg(0,"登录成功");
    public final static EventMsg FAIL=new EventMsg(1,"登录失败");

    public EventMsg msg;

    public LoginEvent(EventMsg msg) {
        this.msg = msg;
    }
}
