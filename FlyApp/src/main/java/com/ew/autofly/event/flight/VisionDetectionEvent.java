package com.ew.autofly.event.flight;

import dji.common.flightcontroller.VisionDetectionState;


public class VisionDetectionEvent {

    private VisionDetectionState visionDetectionState;

    public VisionDetectionEvent(VisionDetectionState visionDetectionState) {
        this.visionDetectionState = visionDetectionState;
    }

    public VisionDetectionState getVisionDetectionState() {
        return visionDetectionState;
    }

    public void setVisionDetectionState(VisionDetectionState visionDetectionState) {
        this.visionDetectionState = visionDetectionState;
    }
}
