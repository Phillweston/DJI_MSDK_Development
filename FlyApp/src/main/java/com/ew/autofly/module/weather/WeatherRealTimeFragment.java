package com.ew.autofly.module.weather;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.module.weather.bean.forecast.Minutely;
import com.ew.autofly.module.weather.bean.forecast.WeatherForecastInfo;
import com.ew.autofly.module.weather.bean.realtime.WeatherRealTimeInfo;
import com.ew.autofly.module.weather.util.WeatherUtil;
import com.ew.autofly.module.weather.view.IWeatherView;
import com.ew.autofly.module.weather.widget.WeatherChartView;
import com.ew.autofly.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.List;



public class WeatherRealTimeFragment extends BaseWeatherFragment implements IWeatherView {

    private TextView mTvAddress;

    private TextView mTime;

    private ImageView mIvSkycon;

    private TextView mTvTemperature;

    private TextView mTvSkyStatus;

    private TextView mTvMinutelyDescription;

    private WeatherChartView mWeatherChartView;

    private RelativeLayout mLoading;

    public static WeatherRealTimeFragment newInstance(LatLngInfo location) {

        Bundle args = new Bundle();
        WeatherRealTimeFragment fragment = new WeatherRealTimeFragment();
        fragment.mLocation = location;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setRootViewId() {
        return R.layout.fragment_weather_real_time;
    }

    @Override
    protected void initRootView(View rootView) {

        mLoading= (RelativeLayout) rootView.findViewById(R.id.rl_loading);

        mTvAddress= (TextView) rootView.findViewById(R.id.tv_address);
        mTime= (TextView) rootView.findViewById(R.id.tv_time);
        mIvSkycon = (ImageView) rootView.findViewById(R.id.iv_sky_icon);
        mTvSkyStatus= (TextView) rootView.findViewById(R.id.tv_sky_desc);
        mTvTemperature = (TextView) rootView.findViewById(R.id.tv_temperature);
        mTvMinutelyDescription = (TextView) rootView.findViewById(R.id.tv_minutely_description);

        mWeatherChartView = (WeatherChartView) rootView.findViewById(R.id.wc_two_hour);
        mWeatherChartView.setLineColor(getResources().getColor(R.color.default_blue));

        setTime();
    }

    protected void loadTask() {

        mPresenter.loadRealTimeWeather();
        mPresenter.loadForecastWeather();
        mPresenter.loadLocationAddress();

    }

    @Override
    public void showRealTimeWeather(WeatherRealTimeInfo weatherRealTimeInfo) {

            WeatherRealTimeInfo.Result result = weatherRealTimeInfo.getResult();
            String skyconValue = result.getSkycon();
            float precipitation = result.getPrecipitation().getLocal().getIntensity();
            mIvSkycon.setImageDrawable(mContext.getResources()
                    .getDrawable(WeatherUtil.getSkyiconId(skyconValue, precipitation,0)));
            float temperature = result.getTemperature();
            mTvTemperature.setText((int) temperature + "°");

            float windSpeed=result.getWind().getSpeed();
            windSpeed/=3.6;

            setSkyDesc(skyconValue,precipitation,windSpeed);

    }

    @Override
    public void showForecastWeather(WeatherForecastInfo weatherForecastInfo) {

        Minutely minutely = weatherForecastInfo.getResult().getMinutely();
        List<Float> precipitations = minutely.getPrecipitation_2h();

        mWeatherChartView.refreshView(precipitations);
        String desc = minutely.getDescription();
        mTvMinutelyDescription.setText(desc);

    }

    @Override
    public void showLocationAddress(String address) {
        mTvAddress.setText(address);
    }

    @Override
    public void showToast(String toast) {

    }

    @Override
    public void showLoading(boolean isShow, String loadingMsg) {
        mLoading.setVisibility(isShow?View.VISIBLE:View.GONE);
    }

    @Override
    public void showError(boolean visible, String errorMsg) {
        if(visible){
            mLoading.findViewById(R.id.pb_weather_progress).setVisibility(View.GONE);
            TextView errorTv= (TextView) mLoading.findViewById(R.id.tv_weather_error);
            errorTv.setText(mContext.getString(R.string.weather_load_error));
        }
        ToastUtil.show(mContext, errorMsg);
    }

    @Override
    public void showEmpty(boolean isShow, String emptyMsg) {

    }

    
    private void setTime(){
        mTime.setText(WeatherUtil.getCurrentTime());
    }


    private void setSkyDesc( String skyconValue,float precipitation,float speed){
        String desc=WeatherUtil.getSkyStatus(skyconValue,precipitation,0)
                +" | 风速"+new DecimalFormat("#0.00").format(speed)+"m/s";
        mTvSkyStatus.setText(desc);
    }

}
