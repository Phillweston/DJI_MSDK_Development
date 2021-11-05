package com.ew.autofly.widgets.dji;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ew.autofly.event.flight.VisionDetectionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Collections;

import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.VisionSensorPosition;
import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.ux.R.id;
import dji.ux.internal.MultiAngleRadarSectionViewHolder;
import dji.ux.internal.RadarSectionViewHolder;
import dji.ux.internal.SingleAngleRadarSectionViewHolder;
import dji.ux.widget.RadarWidget;


public class DjiRadarWidget extends RadarWidget {
    private RadarSectionViewHolder[] radarSections;
    private ImageView avoidUp;
    private boolean isObstacleAbove;
    private float aspectRatio;
    private static final DJIKey VISION_DETECTION_STATE_KEY;
    private static final DJIKey IS_ASCENT_LIMITED_BY_OBSTACLE_KEY;

    public DjiRadarWidget(Context var1) {
        this(var1, (AttributeSet) null, 0);
    }

    public DjiRadarWidget(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public DjiRadarWidget(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void initView(Context var1, AttributeSet var2, int var3) {
        super.initView(var1, var2, var3);
        this.radarSections = new RadarSectionViewHolder[4];
        int[] var4 = new int[]{id.radar_forward_0, id.radar_forward_1, id.radar_forward_2, id.radar_forward_3};
        this.radarSections[VisionSensorPosition.NOSE.value()] = new MultiAngleRadarSectionViewHolder(var4, id.forward_distance, id.forward_arrow, id.radar_forward_disabled, this);
        int[] var5 = new int[]{id.radar_backward_0, id.radar_backward_1, id.radar_backward_2, id.radar_backward_3};
        this.radarSections[VisionSensorPosition.TAIL.value()] = new MultiAngleRadarSectionViewHolder(var5, id.backward_distance, id.backward_arrow, id.radar_backward_disabled, this);
        this.radarSections[VisionSensorPosition.LEFT.value()] = new SingleAngleRadarSectionViewHolder(id.radar_left, id.left_distance, id.left_arrow, this);
        this.radarSections[VisionSensorPosition.RIGHT.value()] = new SingleAngleRadarSectionViewHolder(id.radar_right, id.right_distance, id.right_arrow, this);
        RadarSectionViewHolder[] var6 = this.radarSections;
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            RadarSectionViewHolder var9 = var6[var8];
            var9.hide();
        }

        this.avoidUp = (ImageView) this.findViewById(id.avoid_up);
        this.aspectRatio = this.aspectRatio();
    }

    public void initKey() {
        this.addDependentKey(VISION_DETECTION_STATE_KEY);
        this.addDependentKey(IS_ASCENT_LIMITED_BY_OBSTACLE_KEY);
    }

    public void transformValue(Object var1, DJIKey var2) {
        if (var2.equals(VISION_DETECTION_STATE_KEY)) {
            VisionDetectionState var3 = (VisionDetectionState) var1;
            ObstacleDetectionSector[] var4 = var3.getDetectionSectors();
            if (var3.getPosition().value() < this.radarSections.length) {
                RadarSectionViewHolder var5 = this.radarSections[var3.getPosition().value()];
                if (var3.getPosition() == VisionSensorPosition.TAIL && var4 != null) {
                    Collections.reverse(Arrays.asList(var4));
                }

                var5.setDistanceInMeters(var3.getObstacleDistanceInMeters());
                var5.setSectors(var4);
            }
        } else if (var2.equals(IS_ASCENT_LIMITED_BY_OBSTACLE_KEY)) {
            this.isObstacleAbove = (Boolean) var1;
        }

    }

    public void updateWidget(DJIKey var1) {
        if (var1.equals(VISION_DETECTION_STATE_KEY)) {
            RadarSectionViewHolder[] var2 = this.radarSections;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                RadarSectionViewHolder var5 = var2[var4];
                var5.update();
            }
        } else if (var1.equals(IS_ASCENT_LIMITED_BY_OBSTACLE_KEY)) {
            this.avoidUp.setVisibility(this.isObstacleAbove ? VISIBLE : GONE);
        }

    }

    protected void onMeasure(int var1, int var2) {
        super.onMeasure(var1, var2);
        int var3 = MeasureSpec.getSize(var1);
        int var4 = MeasureSpec.getSize(var2);
        if ((float) var4 * this.aspectRatio < (float) var3) {
            var3 = (int) ((float) var4 * this.aspectRatio);
        } else {
            var4 = (int) ((float) var3 / this.aspectRatio);
        }

        this.setMeasuredDimension(resolveSize(var3, var1), resolveSize(var4, var2));
    }

    static {
        VISION_DETECTION_STATE_KEY = FlightControllerKey.createFlightAssistantKey(FlightControllerKey.VISION_DETECTION_STATE);
        IS_ASCENT_LIMITED_BY_OBSTACLE_KEY = FlightControllerKey.createFlightAssistantKey(FlightControllerKey.IS_ASCENT_LIMITED_BY_OBSTACLE);
    }

  
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisionDetectionState(VisionDetectionEvent event) {
        VisionDetectionState visionDetectionState = event.getVisionDetectionState();
        onVisionDetectionUpdate(visionDetectionState);
    }

    private void onVisionDetectionUpdate(VisionDetectionState visionDetectionState) {
        if (visionDetectionState != null) {
            transformValue(visionDetectionState, VISION_DETECTION_STATE_KEY);
            updateWidget(VISION_DETECTION_STATE_KEY);
        }
    }
}
