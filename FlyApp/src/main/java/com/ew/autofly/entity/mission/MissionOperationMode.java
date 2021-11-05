package com.ew.autofly.entity.mission;

import org.codehaus.jackson.annotate.JsonValue;


public enum MissionOperationMode {

    NEW(0),//新建
    APPEND(1),//追加
    IMPORT(2);

    private int value;

    MissionOperationMode(int paramInt) {
        this.value = paramInt;
    }

    @JsonValue
    public int value() {
        return this.value;
    }

    public boolean _equals(int paramInt) {
        return this.value == paramInt;
    }

    public static MissionOperationMode find(int paramInt) {
        MissionOperationMode mMissionOperationMode = NEW;
        for (int i = 0; i < values().length; i++) {
            if (values()[i]._equals(paramInt)) {
                mMissionOperationMode = values()[i];
                break;
            }
        }
        return mMissionOperationMode;
    }
}
