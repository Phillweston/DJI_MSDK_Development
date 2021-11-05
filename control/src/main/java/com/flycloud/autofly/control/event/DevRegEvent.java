package com.flycloud.autofly.control.event;

import com.flycloud.autofly.control.event.message.EventMsg;



public class DevRegEvent {

    public final static EventMsg SUCCESS=new EventMsg(0,"获取设备注册信息成功");
    public final static EventMsg SUCCESS_LOCAL=new EventMsg(1,"本地注册检查成功");
    public final static EventMsg FAIL_NO_VERIFY=new EventMsg(2,"管理员未审核");
    public final static EventMsg FAIL_EXPIRE =new EventMsg(3,"注册已过期，请联系管理员审核");
    public final static EventMsg FAIL_REFUSE=new EventMsg(4,"管理员拒绝了你的申请，请联系管理员");
    public final static EventMsg FAIL_UNKNOW=new EventMsg(5,"注册失效，请联系管理员审核");
    public final static EventMsg FAIL_NO_APPLY=new EventMsg(6,"未提交激活申请");
    public final static EventMsg FAIL_SERVER_ERROR=new EventMsg(6,"获取注册信息失败:服务器错误");
    public final static EventMsg FAIL_NETWORK_ERROR=new EventMsg(7,"获取注册信息失败:网络错误");

    public EventMsg msg;

    public DevRegEvent( EventMsg eventMsg) {
        this.msg = eventMsg;
    }
}
