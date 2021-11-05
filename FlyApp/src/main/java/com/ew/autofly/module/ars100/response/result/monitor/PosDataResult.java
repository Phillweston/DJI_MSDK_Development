package com.ew.autofly.module.ars100.response.result.monitor;

import com.ew.autofly.module.ars100.response.result.BaseResult;

import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt8;
import static com.ew.autofly.module.ars100.ARS100Command.subBytes;



public class PosDataResult extends BaseResult{

    private int IMUStatus=-1;
    private int GNSSStatus=-1;


    public PosDataResult(byte[] data) {
        super(data);
        IMUStatus=bytesToUInt8(subBytes(data, 72, 1));
        GNSSStatus=bytesToUInt8(subBytes(data, 73, 1));
    }

    public String getIMUStatusMessage(){
        String msg="N/A";
        switch (IMUStatus) {
            case 0:
                msg="GNSS和IMU未完成对准，GNSS定位有效";
                break;
            case 1:
                msg="GNSS和IMU正在对准过程中";
                break;
            case 2:
                msg="GNSS和IMU未完成对准，但通过磁力计可以输出姿态";
                break;
            case 3:
                msg="GNSS和IMU完成对准";
                break;
            case 4:
                msg="POS系统状态非常好";
                break;
        }
        return msg;
    }

    public String getGNSSStatusMessage(){
        String msg="N/A";
        switch (GNSSStatus) {
            case 0:
                msg="无有效GNSS定位";
                break;
            case 1:
                msg="GNSS标准定位模式";
                break;
            case 2:
                msg="GNSS差分定位";
                break;
            case 3:
                msg="GNSS精密定位模式";
                break;
            case 4:
                msg="固定解";
                break;
            case 5:
                msg="浮动解";
                break;
            case 6:
                msg="正在估算";
                break;
            case 7:
                msg="人工输入";
                break;
            case 8:
                msg="模拟模式";
                break;
        }
        return msg;
    }
}
