package com.ew.autofly.widgets.ai;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ew.autofly.R;
import com.ew.autofly.widgets.dji.DjiCameraControlsWidget;
import com.flycloud.autofly.ux.view.button.ExpandableBreathingButton;


public class AICameraControlsPanel extends FrameLayout implements View.OnClickListener {

    public final static int STATUS_AI_FREE = 1;
    public final static int STATUS_AI_CAPTURING = 2;

    private DjiCameraControlsWidget mNormalCameraCaptureWidget;
    private ProgressBar mAICaptureProgress;
    private ExpandableBreathingButton mAICaptureBtn;
    private RelativeLayout mAiControlRl;

    public AICameraControlsPanel(Context context) {
        this(context, null);
    }

    public AICameraControlsPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AICameraControlsPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initAiButton(context);
    }

    private void initAiButton(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_ai_capture, this);
        mAiControlRl = (RelativeLayout) this.findViewById(R.id.rl_ai_control);
        mNormalCameraCaptureWidget = (DjiCameraControlsWidget) this.findViewById(R.id.CameraCaptureWidget);
        mAICaptureProgress = (ProgressBar) this.findViewById(R.id.progress_capture);
        mAICaptureBtn = (ExpandableBreathingButton) this.findViewById(R.id.btn_ai);
        mAICaptureBtn.enableExpand(false);

        mAICaptureBtn.setOnButtonItemClickListener(new ExpandableBreathingButton.OnButtonItemClickListener() {
            @Override
            public void onButtonItemClick(int position) {

            }

            @Override
            public void onExpandClick() {
                if (mOnAICameraControlListener != null) {
                    mOnAICameraControlListener.startCapture();
                }
            }
        });

        enableAI(false);
    }

    @Override
    public void onClick(View v) {

    }


    public void finishCapture() {

    }

    public void enableAI(boolean enable) {
        mNormalCameraCaptureWidget.enableCameraCaptureWidget(!enable);
        mNormalCameraCaptureWidget.enablePictureVideoSwitch(!enable);
        mAiControlRl.setVisibility(enable ? VISIBLE : GONE);
    }

    public void startCapture() {

    }

    /**
     *
     * @param status
     */
    public void updateStatus(int status) {
        switch (status){
            case STATUS_AI_FREE:
                mAICaptureProgress.setVisibility(View.GONE);
                mAICaptureBtn.disPlayButtonText(true);
                break;
            case STATUS_AI_CAPTURING:
                mAICaptureProgress.setVisibility(View.VISIBLE);
                mAICaptureBtn.disPlayButtonText(false);
                break;
        }
    }

    private OnAICameraControlListener mOnAICameraControlListener;

    public void setOnAICameraControlListener(OnAICameraControlListener onAICameraControlListener) {
        mOnAICameraControlListener = onAICameraControlListener;
    }

    public interface OnAICameraControlListener {

        void startCapture();
    }
}
