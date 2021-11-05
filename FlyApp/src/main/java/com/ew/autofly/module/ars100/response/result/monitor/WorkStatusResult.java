package com.ew.autofly.module.ars100.response.result.monitor;

import com.ew.autofly.module.ars100.response.result.BaseResult;

import static com.ew.autofly.module.ars100.ARS100Command.bytesToLong;
import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt32;
import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt8;
import static com.ew.autofly.module.ars100.ARS100Command.subBytes;



public class WorkStatusResult extends BaseResult {

    public final static int WORK_STATUS_NO_CONNECT = 0;
    public final static int WORK_STATUS_FREE = 1;
    public final static int WORK_STATUS_POS = 2;
    public final static int WORK_STATUS_POINTCLOUD = 3;

    private int workStatus = 0;
    private long posTime = 0;
    private long pointCloudTime = 0;
    private long diskRemainSize = 0;

    public WorkStatusResult(byte[] data) {
        super(data);
        workStatus = bytesToUInt8(subBytes(data, 0, 1));

        posTime = bytesToUInt32(subBytes(data, 1, 4));
        pointCloudTime = bytesToUInt32(subBytes(data, 5, 4));
        diskRemainSize = bytesToLong(subBytes(data, 9, 8));

    }

    public int getWorkStatus() {
        return workStatus;
    }

    public String getWorkStatusMessage() {
        String msg = "未连接";
        switch (workStatus) {
            case WORK_STATUS_NO_CONNECT:
                msg = "未连接";
                break;
            case WORK_STATUS_FREE:
                msg = "空闲";
                break;
            case WORK_STATUS_POS:
                msg = "采集POS数据";
                break;
            case WORK_STATUS_POINTCLOUD:
                msg = "采集点云数据";
                break;
        }
        return msg;
    }

    public long getPosTime() {
        return posTime;
    }

    public long getPointCloudTime() {
        return pointCloudTime;
    }

    public long getDiskRemainSize() {
        return diskRemainSize;
    }
}
