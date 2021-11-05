package com.ew.autofly.module.flightrecord.view;

import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.base.IBaseMvpView;

import java.util.List;



public interface IRecordView extends IBaseMvpView{

    void onLoadRecordList(List<FlightRecord> flightRecordList);

    void onDeleteRecord(FlightRecord record);

    void showUploadProgress(boolean isShow);

    void showUploadSuccess(int position);

}
