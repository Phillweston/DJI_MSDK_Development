package com.flycloud.autofly.base.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.flycloud.autofly.base.secure.XXTeaLib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



public class SharedPreferencesUtil {

    private boolean HAS_SECURE = true;
    private SharedPreferences share = null;
    private final String PASSWORD = "Xh2D3heL";
    private SharedPreferences.Editor editor = null;

    public SharedPreferencesUtil(Context context, String fileName, boolean isSecure) {
        HAS_SECURE = isSecure;
        share = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = share.edit();
    }

    public void clear() {
        if (editor != null) {
            editor.clear();
            editor.commit();
        }
    }

    public String getString(String key, String defValue) {
        if (share == null) {
            return defValue;
        }
        if (HAS_SECURE) {
            String s = share.getString(key, null);
            if (s != null && !s.equals("")) {
                return decodeToString(s);
            } else {
                return defValue;
            }
        } else {
            return share.getString(key, defValue);
        }
    }

    public void putString(String key, String value) {
        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            String encodeStr = encodeString(value);
            editor.putString(key, encodeStr);
        } else {
            editor.putString(key, value);
        }
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        if (share == null) {
            return defValue;
        }
        if (HAS_SECURE) {
            String iStr = share.getString(key, null);
            if (iStr != null && !iStr.equals("")) {
                return decodeToInt(iStr);
            } else {
                return defValue;
            }
        } else {
            return share.getInt(key, defValue);
        }
    }

    public void putInt(String key, int value) {
        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            String encodeStr = encodeInt(value);
            editor.putString(key, encodeStr);
        } else {
            editor.putInt(key, value);
        }
        editor.commit();
    }

    public long getLong(String key, long defValue) {
        if (share == null) {
            return defValue;
        }
        if (HAS_SECURE) {
            String lStr = share.getString(key, null);
            if (lStr != null && !lStr.equals("")) {
                return decodeToLong(lStr);
            } else {
                return defValue;
            }
        } else {
            return share.getLong(key, defValue);
        }
    }

    public void putLong(String key, long value) {
        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            String encodeStr = encodeLong(value);
            editor.putString(key, encodeStr);
        } else {
            editor.putLong(key, value);
        }
        editor.commit();
    }

    public float getFloat(String key, float defValue) {
        if (share == null) {
            return defValue;
        }
        if (HAS_SECURE) {
            String lStr = share.getString(key, null);
            if (lStr != null && !lStr.equals("")) {
                return decodeToFloat(lStr);
            } else {
                return defValue;
            }
        } else {
            return share.getFloat(key, defValue);
        }
    }

    public void putFloat(String key, float value) {
        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            String encodeStr = encodeFloat(value);
            editor.putString(key, encodeStr);
        } else {
            editor.putFloat(key, value);
        }
        editor.commit();
    }

    public double getDouble(String key, double defValue) {
        if (share == null) {
            return defValue;
        }
        if (HAS_SECURE) {
            String lStr = share.getString(key, null);
            if (lStr != null && !lStr.equals("")) {
                return decodeToDouble(lStr);
            } else {
                return defValue;
            }
        } else {
            return getDouble(share, key, defValue);
        }
    }

    public void putDouble(String key, double value) {
        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            String encodeStr = encodeDouble(value);
            editor.putString(key, encodeStr);
        } else {
            putDouble(editor, key, value);
        }
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {

        if (share == null) {
            return defValue;
        }
        if (HAS_SECURE) {
            String boolStr = share.getString(key, null);
            if (boolStr != null && !boolStr.equals("")) {
                return decodeToBool(boolStr);
            } else {
                return defValue;
            }
        } else {
            return share.getBoolean(key, defValue);
        }
    }

    public void putBoolean(String key, boolean value) {

        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            String encodeStr = encodeBoolean(value);
            editor.putString(key, encodeStr);
        } else {
            editor.putBoolean(key, value);
        }
        editor.commit();
    }

    public void putStringSet(String key, Set<String> set) {
        if (editor == null) {
            return;
        }
        if (HAS_SECURE) {
            Set<String> enCodeSet = new HashSet<String>();
            if (set != null && set.size() > 0) {
                for (String value : set) {
                    enCodeSet.add(encodeString(value));
                }
            }
            editor.putStringSet(key, enCodeSet);
        } else {
            editor.putStringSet(key, set);
        }
        editor.commit();
    }

    public Set<String> getStringSet(String key, Set<String> defaultValut) {
        Set<String> set = share.getStringSet(key, defaultValut);
        if (HAS_SECURE) {
            Set<String> decodeSet = new HashSet<String>();
            if (set != null && set.size() > 0) {
                for (String value : set) {
                    decodeSet.add(decodeToString(value));
                }
            }
            return decodeSet;
        } else {
            return set;
        }
    }


    public synchronized <T> void putDataList(String key, ArrayList<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        try {
            Gson gson = new Gson();
          
            String value = gson.toJson(datalist);
            if (HAS_SECURE) {
                String encodeStr = encodeString(value);
                editor.putString(key, encodeStr);
            } else {
                editor.putString(key, value);
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取List
     *
     * @param key
     * @return
     */
    public synchronized <T> ArrayList<T> getDataList(String key) {
        ArrayList<T> datalist = new ArrayList<T>();
        String strJson = share.getString(key, null);
        if (null == strJson) {
            return datalist;
        }

        try {
            if (HAS_SECURE) {
                strJson = decodeToString(strJson);
            }
            Gson gson = new Gson();
            datalist = gson.fromJson(strJson, new TypeToken<ArrayList<T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datalist;
    }

    private String encodeBoolean(boolean bool) {
        String boolStr = String.valueOf(bool);
        return encodeString(boolStr);
    }

    private String encodeInt(int i) {
        String iStr = String.valueOf(i);
        return encodeString(iStr);
    }

    private String encodeLong(long l) {
        String lStr = String.valueOf(l);
        return encodeString(lStr);
    }

    private String encodeFloat(float f) {
        String fStr = String.valueOf(f);
        return encodeString(fStr);
    }

    private String encodeDouble(double f) {
        String fStr = String.valueOf(f);
        return encodeString(fStr);
    }

    private String encodeString(String msg) {
        if (msg == null) {
            msg = "";
        }
        try {
            msg = msg.trim();
            return XXTeaLib.encrypt(msg, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean decodeToBool(String msg) {
        String boolStr = decodeToString(msg);
        if (boolStr == null || boolStr.equals("")) {
            return false;
        }
        return Boolean.valueOf(boolStr);
    }

    private int decodeToInt(String msg) {
        String iStr = decodeToString(msg);

        if (iStr == null || iStr.equals("")) {
            return 0;
        }
        return Integer.valueOf(iStr);
    }

    private long decodeToLong(String msg) {
        String lStr = decodeToString(msg);
        if (lStr == null || lStr.equals("")) {
            return 0l;
        }
        return Long.valueOf(lStr);
    }

    private float decodeToFloat(String msg) {
        String lStr = decodeToString(msg);
        if (lStr == null || lStr.equals("")) {
            return 0f;
        }
        return Float.valueOf(lStr);
    }

    private double decodeToDouble(String msg) {
        String lStr = decodeToString(msg);
        if (lStr == null || lStr.equals("")) {
            return 0d;
        }
        return Double.valueOf(lStr);
    }

    private String decodeToString(String msg) {
        if (msg == null) {
            return "";
        }
        try {
            msg = msg.trim();
            return XXTeaLib.decrypt(msg, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        if (!prefs.contains(key))
            return defaultValue;
        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }
}
