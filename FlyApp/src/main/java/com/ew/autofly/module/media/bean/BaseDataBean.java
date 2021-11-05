package com.ew.autofly.module.media.bean;


public abstract class BaseDataBean {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_MEDIA = 1;

    private int type;

    public BaseDataBean() {
        type=initType();
    }

    public int getType() {
        return type;
    }

    /**
     * 初始化类型，子类实现
     * @return
     */
    public abstract int initType();
}
