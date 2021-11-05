package com.ew.autofly.module.ars100.request.request.monitor;

import com.ew.autofly.module.ars100.request.RequestImpl;
import com.ew.autofly.module.ars100.request.RequestID;

import java.util.ArrayList;
import java.util.List;



public class WorkStatusRequest extends RequestImpl {

    @Override
    public int setPackageId() {
        return RequestID.MONITOR_WORK_STATUS;
    }

    @Override
    public List<byte[]> createBody() {
        byte[] body_reserve = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        List<byte[]> bodyBytes = new ArrayList<>();
        bodyBytes.add(body_reserve);

        return bodyBytes;
    }
}
