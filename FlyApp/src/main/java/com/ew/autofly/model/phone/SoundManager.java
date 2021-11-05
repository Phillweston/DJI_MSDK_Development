package com.ew.autofly.model.phone;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;


public class SoundManager {

    private Context mContext;

    private SoundPool mSoundPool = null;
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();

    private long mPlayCurrentTime;

    
    private long mPlayIntervalTime = 5000;

    public SoundManager(Context context) {
        mContext = context;
    }

    
    public void init() {
        try {
            initSP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void release() {
        try {
            mSoundPool.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putSound(int tag, int resId) {
        soundID.put(tag, mSoundPool.load(mContext, resId, 1));
    }

    public void playSound(int tag) {
        try {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - mPlayCurrentTime > mPlayIntervalTime) {//1s播放一次
                mSoundPool.play(soundID.get(tag), 1, 1, 1, 0, 1);
                mPlayCurrentTime = timeMillis;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlayIntervalTime(long playIntervalTime) {
        mPlayIntervalTime = playIntervalTime;
    }

    private void initSP() throws Exception {
        SoundPool.Builder builder = new SoundPool.Builder();

        builder.setMaxStreams(1);

        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();

        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);

        builder.setAudioAttributes(attrBuilder.build());
        mSoundPool = builder.build();
    }
}
