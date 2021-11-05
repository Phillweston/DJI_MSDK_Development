package com.ew.autofly.model;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.useraccount.UserAccountManager;


public class UserManager {

    private AtomicBoolean isAlreadyLoginCall = new AtomicBoolean(false);

    public UserManager() {
    }

    public static UserManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final UserManager INSTANCE = new UserManager();

        private LazyHolder() {
        }
    }

    
    public void logIntoDJIUserAccount(Context context, CommonCallbacks.CompletionCallbackWith<UserAccountState>
            callback, boolean recall) {

        if (recall) {
            isAlreadyLoginCall.set(false);
        } else {
            if (!isNeedToLoginToDji()) {
                return;
            }
        }

        if (isAlreadyLoginCall.compareAndSet(false, true)) {
            UserAccountManager.getInstance().logIntoDJIUserAccount(context, new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                @Override
                public void onSuccess(UserAccountState userAccountState) {
                    isAlreadyLoginCall.set(false);
                    if (callback != null) {
                        callback.onSuccess(userAccountState);
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                    isAlreadyLoginCall.set(false);
                    if (callback != null) {
                        callback.onFailure(djiError);
                    }
                }
            });
        }
    }

    public void logIntoDJIUserAccount(Context context, CommonCallbacks.CompletionCallbackWith<UserAccountState>
            callback) {
        logIntoDJIUserAccount(context, callback, false);

    }

    public void logoutOfDJIUserAccount(CommonCallbacks.CompletionCallback callback) {
        isAlreadyLoginCall.set(false);
        UserAccountManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (callback != null) {
                    callback.onResult(djiError);
                }
            }
        });
    }

    private boolean isNeedToLoginToDji() {
        boolean isNeed = true;
        UserAccountState userAccountState = UserAccountManager.getInstance().getUserAccountState();
        switch (userAccountState) {
            case AUTHORIZED:
            case NOT_AUTHORIZED:
                isNeed = false;
                break;
        }
        return isNeed;
    }
}
