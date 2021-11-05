package com.ew.autofly.struct.presenter.interfaces;


public interface IBaseFlightPresenter {

    /**
     * 判断飞机是否已连接
     *
     * @return
     */
    boolean isAirCraftConnect();

    
    void initAirCraftStateCallback();

    
    void removeAircraftStateCallback();

    
    void setUpKeys();

    
    void tearDownKeys();

    void onDestroy();
}
