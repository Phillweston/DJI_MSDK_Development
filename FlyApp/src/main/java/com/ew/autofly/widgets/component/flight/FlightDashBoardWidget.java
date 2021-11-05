package com.ew.autofly.widgets.component.flight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;

import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.ux.base.FrameLayoutWidget;
import dji.ux.model.base.BaseWidgetAppearances;


public class FlightDashBoardWidget extends FrameLayoutWidget {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private float distance, altitude, verticalSpeed, horizontalSpeed;

    private float speedX;
    private float speedY;
    private double aircraftLatitude;
    private double aircraftLongitude;
    private double homeLatitude;
    private double homeLongitude;

    private DJIKey aircraftVelocityXKey;
    private DJIKey aircraftVelocityYKey;
    private DJIKey aircraftVelocityZKey;
    private FlightControllerKey aircraftLatitudeKey;
    private FlightControllerKey aircraftLongitudeKey;
    private FlightControllerKey homeLatitudeKey;
    private FlightControllerKey homeLongitudeKey;
    private DJIKey aircraftAltitudeKey;

    private LinearLayout dashboard;
    private LinearLayout childPanel;

    private TextView mCurrentHeightTv;
    private TextView mCurrentDistanceTv;
    private TextView mCurrentVspeedTv;
    private TextView mCurrentHspeedTv;

    public FlightDashBoardWidget(Context context) {
        this(context, null);
    }

    public FlightDashBoardWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlightDashBoardWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public void initView(Context context, AttributeSet attributeSet, int i) {
        super.initView(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.layout_widget_dashboard, this);

        dashboard = findViewById(R.id.this_panel);
        childPanel = findViewById(R.id.addition_panel);
        mCurrentHeightTv = (TextView) findViewById(R.id.tv_current_height);
        mCurrentDistanceTv = (TextView) findViewById(R.id.tv_current_distance);
        mCurrentVspeedTv = (TextView) findViewById(R.id.tv_current_vspeed);
        mCurrentHspeedTv = (TextView) findViewById(R.id.tv_current_hspeed);
    }

    /**
     * 设置垂直/水平
     *
     * @param orientation {@link #HORIZONTAL}
     */
    public void setOrientation(int orientation) {
        if (dashboard != null) {
            dashboard.setOrientation(orientation);
        }
    }


    public void addChildWidget(View view, int index) {
        if (childPanel != null) {
            childPanel.addView(view, index);
        }
    }

    @Override
    public void initKey() {
        this.aircraftAltitudeKey = FlightControllerKey.create(FlightControllerKey.ALTITUDE);
        this.addDependentKey(this.aircraftAltitudeKey);

        this.aircraftLatitudeKey = FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LATITUDE);
        this.aircraftLongitudeKey = FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LONGITUDE);
        this.homeLatitudeKey = FlightControllerKey.create(FlightControllerKey.HOME_LOCATION_LATITUDE);
        this.homeLongitudeKey = FlightControllerKey.create(FlightControllerKey.HOME_LOCATION_LONGITUDE);
        this.addDependentKey(this.aircraftLatitudeKey);
        this.addDependentKey(this.aircraftLongitudeKey);
        this.addDependentKey(this.homeLatitudeKey);
        this.addDependentKey(this.homeLongitudeKey);

        this.aircraftVelocityXKey = FlightControllerKey.create(FlightControllerKey.VELOCITY_X);
        this.aircraftVelocityYKey = FlightControllerKey.create(FlightControllerKey.VELOCITY_Y);
        this.addDependentKey(this.aircraftVelocityXKey);
        this.addDependentKey(this.aircraftVelocityYKey);

        this.aircraftVelocityZKey = FlightControllerKey.create(FlightControllerKey.VELOCITY_Z);
        this.addDependentKey(this.aircraftVelocityZKey);
    }

    @Override
    public void transformValue(Object var1, DJIKey var2) {
        if (var2.equals(this.aircraftAltitudeKey)) {
            this.altitude = (Float) var1;
        } else if (var2.equals(this.aircraftLatitudeKey)) {
            this.aircraftLatitude = (Double) var1;
        } else if (var2.equals(this.aircraftLongitudeKey)) {
            this.aircraftLongitude = (Double) var1;
        } else if (var2.equals(this.homeLatitudeKey)) {
            this.homeLatitude = (Double) var1;
        } else if (var2.equals(this.homeLongitudeKey)) {
            this.homeLongitude = (Double) var1;
        } else if (var2.equals(this.aircraftVelocityXKey)) {
            this.speedX = (Float) var1;
        } else if (var2.equals(this.aircraftVelocityYKey)) {
            this.speedY = (Float) var1;
        } else if (var2.equals(this.aircraftVelocityZKey)) {
            this.verticalSpeed = Math.abs((Float) var1);
        }

        if (LocationCoordinateUtils.checkGpsCoordinate(this.homeLatitude, this.homeLongitude)
                && LocationCoordinateUtils.checkGpsCoordinate(this.aircraftLatitude, this.aircraftLongitude)) {
            this.distance = (float) LocationCoordinateUtils.getDistance(this.homeLatitude, this.homeLongitude, this.aircraftLatitude, this.aircraftLongitude);
        }

        this.horizontalSpeed = (float) Math.sqrt((double) (this.speedX * this.speedX + this.speedY * this.speedY));
    }

    @Override
    public void updateWidget(DJIKey djiKey) {
        mCurrentHeightTv.setText("高度: " + String.format("%.1f", this.altitude) + "m");
        mCurrentDistanceTv.setText("距离: " + String.format("%.1f", this.distance) + "m");
        mCurrentVspeedTv.setText("水平: " + String.format("%.1f", this.horizontalSpeed) + "m/s");
        mCurrentHspeedTv.setText("垂直: " + String.format("%.1f", this.verticalSpeed) + "m/s");
    }

    @Override
    protected BaseWidgetAppearances getWidgetAppearances() {
        return null;
    }
}
