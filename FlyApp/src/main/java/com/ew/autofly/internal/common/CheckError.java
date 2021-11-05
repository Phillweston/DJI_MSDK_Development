package com.ew.autofly.internal.common;

import com.ew.autofly.internal.common.error.FlyError;


public class CheckError extends FlyError {

    
    private boolean isIgnoreError;

    public CheckError(String paramString) {
        super(paramString);
    }


    public boolean isIgnoreError() {
        return isIgnoreError;
    }

    public void setIgnoreError(boolean ignoreError) {
        isIgnoreError = ignoreError;
    }
}
