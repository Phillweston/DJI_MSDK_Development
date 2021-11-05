package com.ew.autofly.module.setting.fragment.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.module.setting.event.EnterSecondMenuEvent;


import org.greenrobot.eventbus.EventBus;

import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;


public abstract class BaseSettingFragment extends BaseSettingUiFragment {

    protected FrameLayout mMainMenu;
    protected BaseSettingSecondFragment mSecondMenuFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setRootView(), container, false);
        initView(view);
        return view;
    }

    /**
     * 设置布局
     *
     * @return @resId
     */
    protected abstract int setRootView();

    protected void initView(View rootView) {
        mMainMenu = rootView.findViewById(R.id.fl_main_menu);
    }

    
    public void goBackToMainMenu() {

        if (mSecondMenuFragment != null) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.setting_side_right_in, R.anim.setting_sdie_right_out);
            ft.remove(mSecondMenuFragment);
            ft.commit();
        }

        if (mMainMenu != null) {
            mMainMenu.setVisibility(View.VISIBLE);
        }
    }

    
    public void enterToSecondMenu(BaseSettingSecondFragment secondFragment,String title) {

        try {
            if (secondFragment != null) {
                mSecondMenuFragment=secondFragment;
                mMainMenu.setVisibility(View.GONE);
                FragmentManager fm = getChildFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.setting_side_right_in, R.anim.setting_sdie_right_out);
                ft.replace(R.id.fl_second_menu, mSecondMenuFragment);
                ft.commit();
                EventBus.getDefault().post(new EnterSecondMenuEvent(title));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected FlightController getFlightController() {
        Aircraft aircraft = EWApplication.getAircraftInstance();
        FlightController flightController = null;

        if (aircraft != null && aircraft.isConnected()) {
            flightController = aircraft.getFlightController();
        }
        return flightController;
    }




}
