package com.ew.autofly.module.weather.view;

import com.ew.autofly.base.IBaseMvpView;
import com.ew.autofly.module.weather.bean.forecast.WeatherForecastInfo;
import com.ew.autofly.module.weather.bean.realtime.WeatherRealTimeInfo;



public interface IWeatherView extends IBaseMvpView{

    void showRealTimeWeather(WeatherRealTimeInfo weatherRealTimeInfo);

    void showForecastWeather(WeatherForecastInfo weatherForecastInfo);

    void showLocationAddress(String address);

}
