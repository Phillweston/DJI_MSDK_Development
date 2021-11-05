package com.ew.autofly.module.ars100.request.request.control;

import androidx.annotation.NonNull;

import com.ew.autofly.module.ars100.request.RequestID;

import java.util.ArrayList;
import java.util.List;



public class StartPointCloudRequest extends BaseControlRequest {

    private String workName;

    public StartPointCloudRequest(@NonNull String workName) {
        this.workName = workName;
    }

    @Override
    public int setPackageId() {
        return RequestID.CONTROL_START_POINTCLOUD;
    }

    @Override
    public List<byte[]> createBody() {

        byte[] work_name = workName.getBytes();
        long work_name_len = work_name.length;
        byte[] body_reserve = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        List<byte[]> bodyBytes = new ArrayList<>();
        bodyBytes.add(UInt32ToBytes(work_name_len));
        bodyBytes.add(work_name);
        bodyBytes.add(body_reserve);

        return bodyBytes;
    }
}
