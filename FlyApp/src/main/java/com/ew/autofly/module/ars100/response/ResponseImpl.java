package com.ew.autofly.module.ars100.response;


import android.util.Log;

import com.ew.autofly.BuildConfig;
import com.ew.autofly.module.ars100.ARS100Command;
import com.ew.autofly.module.ars100.ARSConstant;
import com.ew.autofly.module.ars100.response.result.BaseResult;
import com.ew.autofly.module.ars100.response.result.control.BaseControlResult;
import com.ew.autofly.module.ars100.response.result.monitor.PosDataResult;
import com.ew.autofly.module.ars100.response.result.monitor.WarnResult;
import com.ew.autofly.module.ars100.response.result.monitor.WorkStatusResult;
import com.ew.autofly.module.ars100.response.result.push.AuthorityResult;
import com.ew.autofly.module.ars100.response.result.service.ConnectServiceResult;

import java.util.Arrays;



public class ResponseImpl extends ARS100Command implements IResponse {

    BaseResult result;

    private int responseId = ResponseID.NO_RESPONSE;

    public ResponseImpl(byte[] data) {
        try {
            responseId = checkValid(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int checkValid(byte[] data) throws Exception{
        if (data == null || data.length < fixed_length + 1) {
            return ResponseID.NO_RESPONSE;
        }


        if (!Arrays.equals(subBytes(data, 0, 5), head_start)) {
            return ResponseID.NO_RESPONSE;
        }

        int len = bytesToUInt16(subBytes(data, 5, 2));


        if (!Arrays.equals(subBytes(data, len - 2, 2), tail_end)) {
            return ResponseID.NO_RESPONSE;
        }


        byte[] crc_code = subBytes(data, len - 4, 2);
        byte[] crc_data = subBytes(data, 5, len - 9);

        if (!Arrays.equals(crc_code, UInt16ToBytes(evalCRC16(crc_data)))) {
            return ResponseID.ERROR_CRC;
        }

        byte[] package_id = subBytes(data, 7, 2);
        int responseId = bytesToUInt16(package_id);


        ARSConstant.PACKAGE_COUNT = bytesToUInt16(subBytes(data, 11, 2));


        byte[] body = subBytes(data, 13, len - fixed_length);

        result = translateBody(responseId, body);

        return responseId;
    }


    private BaseResult translateBody(int responseId, byte[] data) {
        BaseResult result = null;

        switch (responseId) {
            case ResponseID.SERVICE_CONNECT:
                result = new ConnectServiceResult(data);
                break;
            case ResponseID.CONTROL_CONNECT_SENSOR:
            case ResponseID.CONTROL_START_POS:
            case ResponseID.CONTROL_STOP_POS:
                result = new BaseControlResult(data);
                break;
            case ResponseID.PUSH_AUTHORITY:
                 result=new AuthorityResult(data);
                break;
            case ResponseID.PUSH_WORK_STATUS:
            case ResponseID.MONITOR_WORK_STATUS:
                result = new WorkStatusResult(data);
                break;
            case ResponseID.PUSH_POS_DATA:
            case ResponseID.MONITOR_POS_DATA:
                result = new PosDataResult(data);
                break;
            case ResponseID.MONITOR_WARN:
                result = new WarnResult(data);
                if (BuildConfig.DEBUG) {
                    Log.d("ARS100", ((WarnResult) result).getType()
                            + ":" + ((WarnResult) result).getMessage());

                }
                break;
        }

        return result;
    }

    @Override
    public int getResponseId() {
        return responseId;
    }

    @Override
    public BaseResult getResult() {
        return result;
    }
}
