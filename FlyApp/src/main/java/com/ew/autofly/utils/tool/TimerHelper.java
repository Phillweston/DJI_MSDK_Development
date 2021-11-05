package com.ew.autofly.utils.tool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class TimerHelper {

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    
    private long triggerTimeInterval;

    private Timer timer;

    private OnTriggerListener mTriggerListener;

    public TimerHelper(long triggerTimeInterval) {
        this.triggerTimeInterval = triggerTimeInterval;
    }

    public void start() {
        if (isRunning()) {
            return;
        }
        isRunning.compareAndSet(false, true);
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mTriggerListener != null) {
                        mTriggerListener.trigger();
                    }
                }
            }, 0, triggerTimeInterval);
        }
    }

    public void stop() {
        isRunning.set(false);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void setTriggerListener(OnTriggerListener triggerListener) {
        mTriggerListener = triggerListener;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public interface OnTriggerListener {

        void trigger();
    }
}
