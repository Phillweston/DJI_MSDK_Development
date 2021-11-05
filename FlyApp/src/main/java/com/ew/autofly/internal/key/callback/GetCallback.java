package com.ew.autofly.internal.key.callback;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.common.error.FlyError;

public interface GetCallback {

    void onSuccess(@NonNull Object paramObject);

    void onFailure(@NonNull FlyError paramError);
}
