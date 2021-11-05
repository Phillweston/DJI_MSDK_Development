package com.ew.autofly.module.flightrecord.presenter;

import com.ew.autofly.db.entity.FlightRecord;



public interface IRecordPresenter {


    void loadUserInfo();


    void loadFlightRecordList();


    void deleteFlightRecord(FlightRecord record);


    void uploadFlightRecord(int position, FlightRecord record, String userId, String userName);
}
