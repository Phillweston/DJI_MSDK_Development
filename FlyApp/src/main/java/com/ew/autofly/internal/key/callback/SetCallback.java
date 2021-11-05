package com.ew.autofly.internal.key.callback;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.common.error.FlyError;

public interface SetCallback {

    void onSuccess();

    void onFailure(@NonNull FlyError var1);
}
