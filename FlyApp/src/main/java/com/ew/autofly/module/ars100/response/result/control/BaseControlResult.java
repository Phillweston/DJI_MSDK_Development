package com.ew.autofly.module.ars100.response.result.control;

import com.ew.autofly.module.ars100.response.result.BaseResult;

import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt8;
import static com.ew.autofly.module.ars100.ARS100Command.subBytes;



public class BaseControlResult extends BaseResult {

    private int cmdResult = -1;
    private int workStatus = 0;
    private String msg = "";

    public BaseControlResult(byte[] data) {
        super(data);
        cmdResult = bytesToUInt8(subBytes(data, 0, 1));
        workStatus = bytesToUInt8(subBytes(data, 1, 1));
        msg = new String(subBytes(data, 2, data.length - 10));
    }

    public boolean isSuccess() {
        return cmdResult == 0;
    }

    public int getCmdResult() {
        return cmdResult;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public String getMessage() {
        return msg;
    }

    public String getCmdResultMessage() {
        String msg = "内部错误";
        switch (workStatus) {
            case 0:
                msg = "成功";
                break;
            case 1:
                msg = "失败";
                break;
            case 2:
                msg = "状态错误";
                break;
            case 3:
                msg = "服务忙";
                break;
            case 4:
                msg = "拒绝";
                break;
        }
        return msg;
    }
}
