package com.ew.autofly.module.flightrecord.presenter;




public interface IPlayBackPresenter {

    void loadDetailListByRecordId(String recordId);

    void loadDetailListByMissionId(String missionId);

    void play();

    void pause();

    void stop();

    void changeProgress(int progress);

    void changeSpeed(int speed);

}
