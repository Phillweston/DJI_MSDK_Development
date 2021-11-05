package com.ew.autofly.module.ars100.request.request.sevice;

import com.ew.autofly.module.ars100.request.RequestImpl;
import com.ew.autofly.module.ars100.request.RequestID;

import java.util.ArrayList;
import java.util.List;



public class ConnectServiceRequest extends RequestImpl {

    @Override
    public int setPackageId() {
        return RequestID.SERVICE_CONNECT;
    }

    @Override
    public List<byte[]> createBody() {


        byte[] body_connect_type = new byte[]{0x00};
        byte[] body_communicate_version = new byte[]{0x03, 0x00, 0x00, 0x00, 0x30, 0x2E, 0x31};
        byte[] body_reserve = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        List<byte[]> bodyBytes = new ArrayList<>();
        bodyBytes.add(body_connect_type);
        bodyBytes.add(body_communicate_version);
        bodyBytes.add(body_reserve);

        return bodyBytes;
    }
}
