package com.ew.autofly.entity.mission;

import org.codehaus.jackson.annotate.JsonValue;


public enum MissionPointType {

    
    SHOT_PHOTO(0),
    
    ASSIST(1),
    
    ASSIST_WITHOUT_ACTION(2);

    private int value;

    MissionPointType(int paramInt){
        this.value = paramInt;
    }

    @JsonValue
    public int value(){
        return this.value;
    }

    public boolean _equals(int paramInt){
        return this.value == paramInt;
    }

    public static MissionPointType find(int paramInt){
        MissionPointType mMissionPointType = ASSIST;
        for (int i = 0; i < values().length; i++) {
            if (values()[i]._equals(paramInt)) {
                mMissionPointType = values()[i];
                break;
            }
        }
        return mMissionPointType;
    }
}
