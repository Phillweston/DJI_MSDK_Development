package com.ew.autofly.module.ars100.request.request.control;

import com.ew.autofly.module.ars100.request.RequestImpl;

import java.util.ArrayList;
import java.util.List;



public abstract class BaseControlRequest extends RequestImpl{


    @Override
    public List<byte[]> createBody() {

        byte[] body_reserve = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        List<byte[]> bodyBytes = new ArrayList<>();
        bodyBytes.add(body_reserve);

        return bodyBytes;
    }
}
