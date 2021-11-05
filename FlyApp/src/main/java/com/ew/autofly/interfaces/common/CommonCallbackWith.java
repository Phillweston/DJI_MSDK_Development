package com.ew.autofly.interfaces.common;

import com.ew.autofly.internal.common.error.FlyError;

import io.reactivex.annotations.Nullable;


public interface CommonCallbackWith<T> {
    void onSuccess(T result);

    void onFailure(@Nullable FlyError error);
}
