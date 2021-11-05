package com.ew.autofly.module.weather.presenter;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.base.BaseMvpPresenter;
import com.ew.autofly.entity.BaiduAddress;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.module.weather.bean.forecast.WeatherForecastInfo;
import com.ew.autofly.module.weather.bean.realtime.WeatherRealTimeInfo;
import com.ew.autofly.module.weather.view.IWeatherView;
import com.ew.autofly.net.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static com.ew.autofly.constant.AppConstant.BAIDU_CODE;
import static com.ew.autofly.constant.AppConstant.BAIDU_KEY;
import static com.ew.autofly.constant.AppConstant.BAIDU_URL;



public class WeatherPresenter extends BaseMvpPresenter<IWeatherView> {


    private final String CAIYUN_KEY = "cs2m2A5GLz8umsY-";


    private final String CAIYUN_URL = "https://api.caiyunapp.com/v2/";


    private LatLngInfo mLocation;


    private Timer mTimer;

    private boolean isLoading = true;


    public WeatherPresenter(LatLngInfo location) {
        this.mLocation = location;
    }


    public void loadRealTimeWeather() {

        if (isLoading) {
            if (isViewAttached())
                getView().showLoading(true,null);
        }

        String locationStr = mLocation.longitude + "," + mLocation.latitude;
        String realTimeWeatherUrl = CAIYUN_URL + CAIYUN_KEY + "/"
                + locationStr + "/realtime.json";

        OkHttpUtil.doGetAsyncUI(realTimeWeatherUrl, new OkHttpUtil.HttpCallBack<WeatherRealTimeInfo>() {

            @Override
            public void onSuccess(WeatherRealTimeInfo weatherRealTimeInfo) {
                if (isViewAttached()) {
                    getView().showRealTimeWeather(weatherRealTimeInfo);
                    getView().showLoading(false,"");
                }
                isLoading = false;
            }

            @Override
            public void onError(Exception e) {
                if (isViewAttached())
                    getView().showError(isLoading, EWApplication.getInstance().getString(R.string.error_network));
            }

        });
    }


    public void loadForecastWeather() {


        if (isLoading) {
            if (isViewAttached())
                getView().showLoading(true,null);
        }

        String locationStr = mLocation.longitude + "," + mLocation.latitude;
        String realTimeWeatherUrl = CAIYUN_URL + CAIYUN_KEY + "/"
                + locationStr + "/forecast.json";

        OkHttpUtil.doGetAsyncUI(realTimeWeatherUrl, new OkHttpUtil.HttpCallBack<WeatherForecastInfo>() {

            @Override
            public void onSuccess(WeatherForecastInfo weatherForecastInfo) {
                if (isViewAttached()) {
                    getView().showForecastWeather(weatherForecastInfo);
                    getView().showLoading(false,null);
                }
                isLoading = false;
            }

            @Override
            public void onError(Exception e) {
                if (isViewAttached())
                    getView().showError(isLoading, EWApplication.getInstance().getString(R.string.error_network));
            }

        });
    }


    public void loadLocationAddress() {

        String locationStr = mLocation.latitude + "," + mLocation.longitude;

        Map<String, String> params = new HashMap<>();
        params.put("ak", BAIDU_KEY);
        params.put("location", locationStr);
        params.put("coordtype", "gcj02ll");
        params.put("output", "json");
        params.put("pois", "0");
        params.put("mcode", BAIDU_CODE);



        OkHttpUtil.doPostAsyncUI(BAIDU_URL, params, new OkHttpUtil.HttpCallBack<BaiduAddress>() {
            @Override
            public void onSuccess(BaiduAddress baiduAddress) {
                if (baiduAddress != null && baiduAddress.getStatus() == 0) {
                    String address = baiduAddress.getResult().getFormatted_address();
                    if (isViewAttached())
                        getView().showLocationAddress(address);

                }
            }

            @Override
            public void onError(Exception e) {
                if (isViewAttached())
                    getView().showError(isLoading, EWApplication.getInstance().getString(R.string.error_network));
            }
        });
    }


}
