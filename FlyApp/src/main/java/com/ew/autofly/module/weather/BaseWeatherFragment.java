package com.ew.autofly.module.weather;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.ew.autofly.base.BaseMvpFragment;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.module.weather.presenter.WeatherPresenter;
import com.ew.autofly.module.weather.view.IWeatherView;



public abstract class BaseWeatherFragment extends BaseMvpFragment<IWeatherView,WeatherPresenter> {

    protected LatLngInfo mLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected WeatherPresenter createPresenter() {
        return new WeatherPresenter(mLocation);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadTask();
    }

    protected abstract void loadTask();
}
