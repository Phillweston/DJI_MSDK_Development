package com.ew.autofly.module.ars100.request.request.control;

import com.ew.autofly.module.ars100.request.RequestID;



public class StopPointCloudRequest extends BaseControlRequest {

    @Override
    public int setPackageId() {
        return RequestID.CONTROL_STOP_POINTCLOUD;
    }
}
