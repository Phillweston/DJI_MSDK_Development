package com.ew.autofly.internal.common.error;


public class FlyError {

    public static final FlyError COMMON_SET_VALUE = new FlyError("值设置错误");
    public static final FlyError COMMON_UNSUPPORT = new FlyError("不支持");

    private String description;

    public FlyError(String paramString)
    {
        this.description = paramString;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String paramString)
    {
        this.description = paramString;
    }

}
