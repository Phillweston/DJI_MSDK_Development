package com.ew.autofly.widgets.dji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ew.autofly.R;


public class DjiCameraConfigWidget extends FrameLayout {

    private boolean isOpen = false;

    private ImageView mCameraConfigDrawerBtn;
    private LinearLayout mCameraConfigLl;

    public DjiCameraConfigWidget(Context context) {
        this(context, null);
    }

    public DjiCameraConfigWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DjiCameraConfigWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_widget_camera_config, this, true);
        mCameraConfigDrawerBtn = (ImageView) this.findViewById(R.id.btn_camera_config_drawer);
        mCameraConfigLl = (LinearLayout) this.findViewById(R.id.ll_camera_config);
        mCameraConfigLl.setVisibility(GONE);
        mCameraConfigDrawerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    public void toggle() {
        isOpen = !isOpen;
        mCameraConfigDrawerBtn.setActivated(isOpen);
        mCameraConfigLl.setVisibility(isOpen ? VISIBLE : GONE);
    }

}
