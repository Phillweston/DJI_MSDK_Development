package com.ew.autofly.module.ars100.request;


import android.util.Log;

import com.ew.autofly.module.ars100.ARS100Command;
import com.ew.autofly.module.ars100.ARSConstant;
import com.ew.autofly.module.ars100.request.IRequest;

import java.util.ArrayList;
import java.util.List;



public abstract class RequestImpl extends ARS100Command implements IRequest {

    private List<byte[]> headerBytes = new ArrayList<>();

    private List<byte[]> bodyBytes = new ArrayList<>();

    public RequestImpl() {
    }

    @Override
    public List<byte[]> createHeader() {

      
        byte[] head_package_len = calculatePackageLength();
        byte[] head_package_id = UInt16ToBytes(setPackageId());
        byte[] head_client_id = UInt16ToBytes(ARSConstant.CLIENT_ID);
        byte[] head_package_count = UInt16ToBytes(ARSConstant.PACKAGE_COUNT);

        List<byte[]> headerBytes = new ArrayList<>();
        headerBytes.add(head_package_len);
        headerBytes.add(head_package_id);
        headerBytes.add(head_client_id);
        headerBytes.add(head_package_count);

        return headerBytes;
    }

    @Override
    public byte[] getData() {
        this.bodyBytes = createBody();
        this.headerBytes = createHeader();
        return createCommand(headerBytes, bodyBytes);
    }

    private byte[] calculatePackageLength() {
        return UInt16ToBytes(fixed_length + calculateByteLength(bodyBytes));
    }

}
