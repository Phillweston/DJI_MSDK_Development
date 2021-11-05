package com.ew.autofly.module.weather;


import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ew.autofly.R;
import com.ew.autofly.adapter.BaseViewPagerFragmentAdapter;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.LatLngInfo;

import java.util.ArrayList;
import java.util.List;



public class WeatherInfoDlgFragment extends BaseDialogFragment {

    private TabLayout mTLSwitchContent;

    private BaseViewPagerFragmentAdapter mViewPagerFragmentAdapter;

    private List<CharSequence> mListTitle;

    private List<Fragment> mListData;

    
    private WeatherRealTimeFragment mRealTimeWeather;

    
    private WeatherForecastFragment mForecastWeather;

    private ViewPager mViewPager;

   /* private Timer mTimer;


    */
    /*
    private TimerTask mRefreshTimerTask = new TimerTask() {
        @Override
        public void run() {

        }
    };
*/
    private LatLngInfo mLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = (LatLngInfo) getArguments().getSerializable("location");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_weather_info, container, false);
        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTLSwitchContent = (TabLayout) view.findViewById(R.id.tl_switch_content);
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText(getText(R.string.weather_current)));
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText(getText(R.string.weather_forecast)));
        mViewPager = (ViewPager) view.findViewById(R.id.vp_weather);
        initWeatherView();
        return view;
    }

    private void initWeatherView() {
        mListTitle = new ArrayList<>();
        mListTitle.add(getText(R.string.weather_current));
        mListTitle.add(getText(R.string.weather_forecast));

        mRealTimeWeather = WeatherRealTimeFragment.newInstance(mLocation);
        mForecastWeather = WeatherForecastFragment.newInstance(mLocation);

        mListData = new ArrayList<>();
        mListData.add(mRealTimeWeather);
        mListData.add(mForecastWeather);

        mViewPagerFragmentAdapter = new BaseViewPagerFragmentAdapter(getChildFragmentManager(), mListData, mListTitle);
        mViewPager.setAdapter(mViewPagerFragmentAdapter);
        mTLSwitchContent.setupWithViewPager(mViewPager);

    }
}
