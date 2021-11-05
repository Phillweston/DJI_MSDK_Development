package com.flycloud.autofly.base.base;

import androidx.fragment.app.FragmentActivity;

import com.flycloud.autofly.base.util.UmengUtils;
import com.umeng.analytics.MobclickAgent;



public class BaseFragmentActivity extends FragmentActivity{

    @Override
    protected void onResume() {
        super.onResume();
        UmengUtils.startStatistics(getClass());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengUtils.endStatistics(getClass());
        MobclickAgent.onPause(this);
    }
}
