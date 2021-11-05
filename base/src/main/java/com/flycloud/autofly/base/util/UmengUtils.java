package com.flycloud.autofly.base.util;

import com.umeng.analytics.MobclickAgent;



public class UmengUtils {

  
    private final static boolean isOpen = true;


    public static void startStatistics(Class aClass) {
        if (isOpen) {
            MobclickAgent.onPageStart(aClass.getName());
        }
    }


    public static void endStatistics(Class aClass) {
        if (isOpen) {
            MobclickAgent.onPageEnd(aClass.getName());
        }
    }
}
