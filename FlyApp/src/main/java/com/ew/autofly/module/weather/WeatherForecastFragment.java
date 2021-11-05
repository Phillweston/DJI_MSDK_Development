package com.ew.autofly.module.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.module.weather.bean.forecast.Daily;
import com.ew.autofly.module.weather.bean.forecast.WeatherForecastInfo;
import com.ew.autofly.module.weather.bean.forecast.info.Precipitation;
import com.ew.autofly.module.weather.bean.forecast.info.Skycon;
import com.ew.autofly.module.weather.bean.forecast.info.Temperature;
import com.ew.autofly.module.weather.bean.realtime.WeatherRealTimeInfo;
import com.ew.autofly.module.weather.util.WeatherUtil;
import com.ew.autofly.module.weather.view.IWeatherView;
import com.ew.autofly.module.weather.widget.WeatherLineView;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class WeatherForecastFragment extends BaseWeatherFragment implements IWeatherView{

    private TextView mTvAddress;

    private TextView mTime;

    private LinearLayout mLLForecastDay;
    private WeatherLineView mWLForecastLine;

    private RelativeLayout mLoading;

    public static WeatherForecastFragment newInstance(LatLngInfo location) {

        Bundle args = new Bundle();
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        fragment.mLocation=location;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setRootViewId() {
        return R.layout.fragment_weather_forecast;
    }

    @Override
    protected void initRootView(View rootView) {

        mLoading= (RelativeLayout) rootView.findViewById(R.id.rl_loading);

        mTvAddress= (TextView) rootView.findViewById(R.id.tv_address);
        mTime= (TextView) rootView.findViewById(R.id.tv_time);
        mLLForecastDay= (LinearLayout) rootView.findViewById(R.id.ll_forecast_day);

        mWLForecastLine = (WeatherLineView) rootView.findViewById(R.id.wl_forecast_line);
        mWLForecastLine.setHighTempLineColor(getResources().getColor(R.color.red));
        mWLForecastLine.setLowTempLineColor(getResources().getColor(R.color.default_blue));

        setTime();
    }

    @Override
    protected void loadTask() {
        mPresenter.loadForecastWeather();
        mPresenter.loadLocationAddress();
    }

    @Override
    public void showRealTimeWeather(WeatherRealTimeInfo weatherRealTimeInfo) {

    }

    @Override
    public void showForecastWeather(WeatherForecastInfo weatherForecastInfo) {
        Daily daily=weatherForecastInfo.getResult().getDaily();
        List<Temperature> temperatures=daily.getTemperature();
        List<Skycon> skycons=daily.getSkycon();
        List<Precipitation> precipitations=daily.getPrecipitation();

        refreshDailySkycon(skycons,precipitations);
        refreshTemperatureLine(temperatures);

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

    
    private void refreshDailySkycon(List<Skycon> skycons, List<Precipitation> precipitations){

        mLLForecastDay.removeAllViews();

        int size=skycons.size();
        for (int i=0;i<size;i++) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_weather_forecast, mLLForecastDay,false);

            if(i==size-1){
                itemView.setLayoutParams(new ViewGroup
                        .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            Date date = skycons.get(i).getDate();

            int weekNumber=date.getDay();
            int monthNumber =date.getMonth()+1;
            int dayNumber=date.getDate();

            TextView tvWeekNumber = (TextView) itemView.findViewById(R.id.tv_week);

            String chineseWeekDayStr;
            if (i == 0) {
                chineseWeekDayStr = "今天";
            } else {
                chineseWeekDayStr = getChineseWeekDay(weekNumber);
            }
            tvWeekNumber.setText(chineseWeekDayStr);

            TextView tvDate = (TextView) itemView.findViewById(R.id.tv_date);

            tvDate.setText(monthNumber + "月" + dayNumber + "日");

            Logger.i("weather " + date + "  " + weekNumber + " " + monthNumber + "月" + dayNumber + "日");

            String skyIconValue = skycons.get(i).getValue();
            float precipitation = precipitations.get(i).getAvg();

            ImageView ivSkyIcon = (ImageView) itemView.findViewById(R.id.iv_sky_icon);
            ivSkyIcon.setImageDrawable(mContext.getResources()
                    .getDrawable(WeatherUtil.getSkyiconId(skyIconValue, precipitation,1)));

            mLLForecastDay.addView(itemView);
        }
    }

    /**
     * 温度曲线图
     * @param temperatures
     */
    private void refreshTemperatureLine(List<Temperature> temperatures){

        List<Integer> highTemperatures=new ArrayList<>();
        List<Integer> lowTemperatures=new ArrayList<>();

        for (Temperature temperature:temperatures){

            float maxTemp=temperature.getMax();

            float minTemp=temperature.getMin();

            highTemperatures.add((int)maxTemp);
            lowTemperatures.add((int)minTemp);
        }

        mWLForecastLine.setTemperatureData(highTemperatures,lowTemperatures);

    }

    
    private void setTime(){
        mTime.setText(WeatherUtil.getCurrentTime());
    }

    
    private String getChineseWeekDay(int weekNumber){

        String chineseWeekDayStr="";

        String[] chineseWeekDays={"周日","周一","周二","周三","周四","周五","周六"};
        if(weekNumber>=0&&weekNumber<chineseWeekDays.length){
            chineseWeekDayStr=chineseWeekDays[weekNumber];
        }
        return chineseWeekDayStr;
    }

}
