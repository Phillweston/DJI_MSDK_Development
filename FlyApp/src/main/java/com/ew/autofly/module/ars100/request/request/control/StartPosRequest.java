package com.ew.autofly.module.ars100.request.request.control;

import com.ew.autofly.module.ars100.request.RequestID;
import com.ew.autofly.module.ars100.request.RequestImpl;

import java.util.ArrayList;
import java.util.List;



public class StartPosRequest extends BaseControlRequest {

    @Override
    public int setPackageId() {
        return RequestID.CONTROL_START_POS;
    }
}
