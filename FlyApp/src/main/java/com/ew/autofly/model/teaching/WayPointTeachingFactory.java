package com.ew.autofly.model.teaching;

import com.ew.autofly.constant.AppConstant;

import org.codehaus.jackson.annotate.JsonValue;


public class WayPointTeachingFactory {


   public static WayPointTeachingDataModel createDataModel(Type type){
       String path="";
       switch (type) {
           case NONE:
               break;
           case RIVER:
               path= AppConstant.DIR_MISSION.RIVER_PATROL_DATA;
               break;
           case SUBSTATION:
               path= AppConstant.DIR_MISSION.SUBSTATION_PATROL_DATA;
               break;
       }
       return new WayPointTeachingDataModel(path);
   }

    public enum Type {

        NONE(-1),
        RIVER(0),
        SUBSTATION(1);

        private int value;

        Type(int paramInt) {
            this.value = paramInt;
        }

        @JsonValue
        public int value() {
            return this.value;
        }

        public boolean _equals(int paramInt) {
            return this.value == paramInt;
        }

        public static Type find(int paramInt) {
            Type mType = NONE;
            for (int i = 0; i < values().length; i++) {
                if (values()[i]._equals(paramInt)) {
                    mType = values()[i];
                    break;
                }
            }
            return mType;
        }
    }
}
