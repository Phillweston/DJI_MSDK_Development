package com.ew.autofly.widgets.component.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;

import dji.common.camera.SettingsDefinitions;
import dji.keysdk.CameraKey;
import dji.keysdk.DJIKey;
import dji.keysdk.KeyManager;
import dji.keysdk.ProductKey;
import dji.sdk.camera.Camera;
import dji.ux.base.FrameLayoutWidget;
import dji.ux.model.base.BaseWidgetAppearances;


public class CameraZoomWidget extends FrameLayoutWidget implements View.OnClickListener, View.OnTouchListener {

    private ImageView mZoomInBtn;
    private TextView mZoomFactorTv;
    private ImageView mZoomOutBtn;
    private TextView mZoomResetBtn;
    private TextView mZoomTypeTv;

    private boolean isVisible = false;
    private float mOpticalZoomFactor = 1.0f;
    private float mDigitalZoomFactor = 1.0f;

    private DJIKey opticalZoomFactorKey;
    private DJIKey digitalZoomFactorKey;
    private DJIKey displayNameKey;
    private DJIKey connectionKey;

    public CameraZoomWidget(Context context) {
        this(context, null);
    }

    public CameraZoomWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CameraZoomWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public void initView(Context context, AttributeSet attributeSet, int i) {
        super.initView(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.layout_widget_camera_zoom, this);
        mZoomInBtn = (ImageView) findViewById(R.id.btn_zoom_in);
        mZoomFactorTv = (TextView) findViewById(R.id.tv_zoom_factor);
        mZoomTypeTv = (TextView) findViewById(R.id.tv_zoom_type);
        mZoomOutBtn = (ImageView) findViewById(R.id.btn_zoom_out);
        mZoomResetBtn = (TextView) findViewById(R.id.btn_zoom_reset);

        mZoomInBtn.setOnTouchListener(this);
        mZoomOutBtn.setOnTouchListener(this);
        mZoomResetBtn.setOnClickListener(this);

        updateVisibility();
    }

    @Override
    public void initKey() {
        opticalZoomFactorKey = CameraKey.create(CameraKey.OPTICAL_ZOOM_SCALE);
        digitalZoomFactorKey = CameraKey.create(CameraKey.DIGITAL_ZOOM_FACTOR);
        displayNameKey = CameraKey.create(CameraKey.DISPLAY_NAME);
        connectionKey = ProductKey.create(ProductKey.CONNECTION);
        addDependentKey(opticalZoomFactorKey);
        addDependentKey(digitalZoomFactorKey);
        addDependentKey(displayNameKey);
        addDependentKey(connectionKey);
    }

    @Override
    public void transformValue(Object o, DJIKey djiKey) {
        if (djiKey.equals(opticalZoomFactorKey)) {
            mOpticalZoomFactor = (float) o;
        } else if (djiKey.equals(digitalZoomFactorKey)) {
            mDigitalZoomFactor = (float) o;
        } else if (djiKey.equals(displayNameKey)) {
            String displayName = (String) o;
            isVisible = Camera.DisplayNameZ30.equals(displayName);
        } else if (djiKey.equals(connectionKey)) {
            boolean isConnected = (boolean) o;
            if (!isConnected) {
                isVisible = false;
            }
        }
    }

    @Override
    public void updateWidget(DJIKey djiKey) {
        updateVisibility();
        if (isVisible) {
            updateZoomFactorView();
        }
    }

    @Override
    protected BaseWidgetAppearances getWidgetAppearances() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_zoom_reset:
                KeyManager.getInstance().performAction(CameraKey.create(CameraKey.START_CONTINUOUS_OPTICAL_ZOOM),
                        null, SettingsDefinitions.ZoomDirection.ZOOM_OUT, SettingsDefinitions.ZoomSpeed.FAST);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btn_zoom_in:
                touchZoom(true, event);
                return true;
            case R.id.btn_zoom_out:
                touchZoom(false, event);
                return true;
        }

        return false;
    }

    private void touchZoom(boolean isZoomIn, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            KeyManager.getInstance().performAction(CameraKey.create(CameraKey.START_CONTINUOUS_OPTICAL_ZOOM), null, isZoomIn ? SettingsDefinitions.ZoomDirection.ZOOM_IN :
                            SettingsDefinitions.ZoomDirection.ZOOM_OUT,
                    SettingsDefinitions.ZoomSpeed.FAST);

            if (isZoomIn) {
                mZoomInBtn.setImageResource(R.drawable.ic_camera_zoom_in_pressed);
            } else {
                mZoomOutBtn.setImageResource(R.drawable.ic_camera_zoom_out_pressed);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            KeyManager.getInstance().performAction(CameraKey.create(CameraKey.STOP_CONTINUOUS_OPTICAL_ZOOM), null);
            if (isZoomIn) {
                mZoomInBtn.setImageResource(R.drawable.ic_camera_zoom_in);
            } else {
                mZoomOutBtn.setImageResource(R.drawable.ic_camera_zoom_out);
            }
        }

    }

    private void updateZoomFactorView() {

        if (mDigitalZoomFactor > 1 && mOpticalZoomFactor == 30) {
            mZoomTypeTv.setText("数码变焦");
            mZoomTypeTv.setTextColor(getResources().getColor(R.color.blue));
            mZoomFactorTv.setText(String.format("%.1f", mOpticalZoomFactor * mDigitalZoomFactor) + "X");
        } else {
            mZoomTypeTv.setText("光学变焦");
            mZoomTypeTv.setTextColor(getResources().getColor(R.color.white));
            mZoomFactorTv.setText(String.format("%.1f", mOpticalZoomFactor) + "X");
        }
    }

    private void updateVisibility() {
        setVisibility(isVisible ? VISIBLE : GONE);
    }
}
