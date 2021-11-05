package com.ew.autofly.module.ars100.response.result.service;

import com.ew.autofly.module.ars100.ARSConstant;
import com.ew.autofly.module.ars100.response.result.BaseResult;

import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt16;
import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt8;
import static com.ew.autofly.module.ars100.ARS100Command.subBytes;



public class ConnectServiceResult extends BaseResult {

    private boolean isSuccess;

    private byte[] clientId;

    private byte[] communicateVersion;

    public ConnectServiceResult(byte[] data) {
        super(data);
        int connectResult=bytesToUInt8(subBytes(data, 0, 1));
        isSuccess= connectResult == 0;
        ARSConstant.CLIENT_ID=bytesToUInt16(subBytes(data, 1, 2));
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
