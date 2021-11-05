package com.ew.autofly.internal.key.callback;

import androidx.annotation.Nullable;

public interface KeyListener {
    void onValueChange(@Nullable Object oldValue, @Nullable Object newValue);
}
