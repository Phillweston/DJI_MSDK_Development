package com.flycloud.autofly.base.base;

import androidx.fragment.app.Fragment;

import com.flycloud.autofly.base.util.UmengUtils;



public class BaseFragment extends Fragment{

    @Override
    public void onResume() {
        super.onResume();
        UmengUtils.startStatistics(getClass());
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengUtils.endStatistics(getClass());
    }
}
