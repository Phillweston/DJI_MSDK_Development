package com.ew.autofly.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ew.autofly.R;

import java.util.HashMap;


public class SoundPlayerUtil {
    private Context context;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private int soundVolType = AudioManager.STREAM_MUSIC;
    private static SoundPlayerUtil mInstance;

    private SoundPlayerUtil(Context context) {
        this.context = context;
        soundPool = new SoundPool(20, soundVolType, 0);
        soundPoolMap = new HashMap<>();
    }

    public static SoundPlayerUtil getInstance(Context c) {
        if (mInstance == null)
            synchronized (SoundPlayerUtil.class) {
                if (mInstance == null)
                    mInstance = new SoundPlayerUtil(c);
            }
        return mInstance;
    }

    public void init() {
        putSound(R.raw.flight_check, 1);
        putSound(R.raw.no_home_point, 1);
        putSound(R.raw.capture,1);
        putSound(R.raw.record_photo_waypoint,1);
        putSound(R.raw.record_assist_waypoint,1);
        putSound(R.raw.record_waypoint,1);











    }

    private void putSound(int soundRes, int priority) {
        soundPoolMap.put(soundRes, soundPool.load(context, soundRes, priority));
    }

    public void playSound(int order, int times) {
        AudioManager am = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        float maxVolume = am.getStreamMaxVolume(soundVolType);
        float currentVolume = am.getStreamVolume(soundVolType);
        float volumeRatio = currentVolume / maxVolume;
        soundPool.play(soundPoolMap.get(order), volumeRatio, volumeRatio, 1, times, 1);
    }
}