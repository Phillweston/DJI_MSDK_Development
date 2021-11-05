package com.flycloud.autofly.base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SysUtils {
    private static final String ROOTNAME = "LIGHT";

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    public static void writeCache(Context context, String name, String value) {
        SharedPreferences pref = context.getSharedPreferences(ROOTNAME, Context.MODE_PRIVATE);
        pref.edit().putString(name, value).commit();
    }

    public static String readCache(Context context, String name) {
        SharedPreferences pref = context.getSharedPreferences(ROOTNAME, Context.MODE_PRIVATE);
        return pref.getString(name, "");
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return width of the screen.
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return heiht of the screen.
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    public static void toggleInputMethod(Context context) {
        InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showInputMethod(View view) {
        view.requestFocus();
        InputMethodManager im = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideInputMethod(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(
                    v.getApplicationWindowToken(), 0);
        }
    }

    public static String newGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static boolean isAvilible(Context context, String packageName) {
      
        final PackageManager packageManager = context.getPackageManager();
      
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
      
        List<String> packageNames = new ArrayList<String>();
      
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
      
        return packageNames.contains(packageName);
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT<= Build.VERSION_CODES.P){
            String imeiString = tManager.getDeviceId();
            if (!StringUtils.isEmptyOrNull(imeiString)) {
                return imeiString;
            }

            String imsiString = tManager.getSubscriberId();
            if (!StringUtils.isEmptyOrNull((imsiString))) {
                return imsiString;
            }
        }

        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (!StringUtils.isEmptyOrNull((androidId))) {
            return androidId;
        }

        String serialNum = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialNum = (String) (get.invoke(c, "ro.serialno", "UNKNOWN"));
        } catch (Exception ignored) {
            return "XINTU";
        }
        if (!StringUtils.isEmptyOrNull((serialNum))) {
            return serialNum;
        }
        return "XINTU";
    }

    public static String getFormatDeviceId(Context context) {
        String uid = SysUtils.getDeviceId(context);
        String fdId = SysUtils.stringToMD5(uid);

        try {
            fdId = fdId.substring(0, 4) + "-"
                    + fdId.substring(4, 8) + "-"
                    + fdId.substring(8, 12) + "-"
                    + fdId.substring(12, 16);
            fdId = fdId.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fdId;
    }

    /**
     * 获取随机生成的设备id,概率为10^9分之一
     *
     * @return
     */
    public static int getRandomDeviceId() {
        int idNum = 0;
        for (int i = 0; i < 9; i++) {
            int rand = (int) (Math.random() * 10);
            idNum += rand * Math.pow(10, i);
        }
        return idNum;
    }

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        String md5Str = hex.toString();
        int length = md5Str.length();
        if (length < 16) {
            md5Str = "1BF29B766F14C2DA";
        }
        return md5Str;
    }

    /**
     * 判断是否支持DroidSansFallback.ttf字体
     *
     * @return
     */
    public static boolean isSupportDroidSans() {
        String brand = android.os.Build.BRAND;
        return !(brand != null && (brand.equalsIgnoreCase("oppo") || brand.equalsIgnoreCase("vivo")));
    }

    /**
     * 是否是正确的手机号码
     *
     * @param num
     * @return
     */
    public static boolean isMobileNum(String num) {
        String reg = "^0?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[0-9])[0-9]{8}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(num);
        return m.matches();
    }

    public static int getMetaDataInt(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getApplicationContext().getPackageManager()
                    .getApplicationInfo(context.getApplicationContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            int logo = appInfo.metaData.getInt(name);
            return logo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
