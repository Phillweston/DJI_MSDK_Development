package com.ew.autofly.xflyer.utils;

import android.view.View;

public class CommonConstants {
    public static final double MARKER_ICON_SIZE = 40.0D;

    public enum TiltStep {
        Step1, Step2, Step3, Step4, Step5
    }

    public interface MapFragmentClickListener {
        void onMapFragmentClick(View paramView);
    }

    public enum GoogleMapType {
        ImageMap, VectorMap, RoadMap
    }

    public enum MarkerFlag {
        LeftTop, RightTop, RightBottom, LeftBottom, CenterTop, CenterRight, CenterBottom, CenterLeft, MoveCenter, RotationLeft, RotationRight
    }
}