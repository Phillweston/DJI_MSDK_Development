package com.ew.autofly.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ew.autofly.R;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.event.ui.topbar.TopTitleChangeEvent;
import com.ew.autofly.mode.manualcollect.ManualCollectFragment;
import com.ew.autofly.struct.controller.BaseFlightFragment;
import com.ew.autofly.struct.controller.BaseTopFragment;
import com.ew.autofly.struct.view.IBaseFlightView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;




public class MainFragment extends Fragment {

    public static String ARG_PARAM_MAP_FRAGMENT_MODE = "MAP_FRAGMENT_MODE";
    public static String ARG_PARAM_MAP_FRAGMENT_TITLE = "MAP_FRAGMENT_TITLE";

    private View view;

    private int mMode;

    private IBaseFlightView mFlightView;

    private BaseTopFragment mTopBar;

    private String mTitleStr;

    public static MainFragment newInstance(int mode, String title) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_MAP_FRAGMENT_MODE, mode);
        args.putString(ARG_PARAM_MAP_FRAGMENT_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMode = getArguments().getInt(ARG_PARAM_MAP_FRAGMENT_MODE);
        mTitleStr = getArguments().getString(ARG_PARAM_MAP_FRAGMENT_TITLE);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, null);
        initView(view);
        addMapFragment();
        return view;
    }

    private void initView(View view) {
        mTopBar = (BaseTopFragment) getChildFragmentManager().findFragmentById(R.id.id_fragment_topbar);
        mTopBar.setTitleTex(mTitleStr);
    }

    private void addMapFragment() {
        String tag = "";
        switch (mMode) {
            case FlyCollectMode.Manual:
                mFlightView = new ManualCollectFragment();
                tag = "ManualCollectFragment";
                break;

        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.id_fragment_map, (BaseFlightFragment) mFlightView, tag);
        ft.commit();
    }

    public void removeMapFragment() {
        if (mFlightView != null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove((BaseFlightFragment) mFlightView);
            ft.commit();
        }
    }

    public boolean isCanReplace() {
        return mFlightView != null && mFlightView.isTaskCanTerminate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopTitleChangeEvent(TopTitleChangeEvent event) {
        mTopBar.setTitleTex(event.getTitle());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
