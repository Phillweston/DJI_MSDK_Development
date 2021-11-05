package com.ew.autofly.interfaces.common;

import com.ew.autofly.internal.common.error.FlyError;

import io.reactivex.annotations.Nullable;


public interface CommonCallback {
    void onSuccess();

    void onFailure(@Nullable FlyError error);
}
