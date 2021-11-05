package com.ew.autofly.module.missionmanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ew.autofly.R;

public class ButtonProgressDialog extends Dialog {

    private Context mContext;

    private TextView mMessageTv;

    private TextView mTitleTv;

    private Button mFinishBtn;

    private Button mServiceBtn;

    private Button mStopStitchBtn;

    private LinearLayout mLlService;

    private ProgressBar mProgressBar;

    public ButtonProgressDialog(Context context) {
        this(context, R.style.ButtonProgressDialogTheme);
    }

    public ButtonProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext=context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_button_progress);
        mMessageTv = (TextView) findViewById(R.id.tv_message);
        mFinishBtn = (Button) findViewById(R.id.btn_finish);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTitleTv = (TextView) findViewById(R.id.tv_title);

        mServiceBtn= (Button) findViewById(R.id.btn_service);
        mStopStitchBtn= (Button) findViewById(R.id.btn_stop);
        mLlService= (LinearLayout) findViewById(R.id.ll_service);
    }

    public void setMessage(final String message) {

        if (mMessageTv != null && message != null) {
            if (!message.equals(mMessageTv.getText())) {
                mMessageTv.setText(message);
            }
        }
    }

    public void setTitle(final String title) {

        if (mTitleTv != null && title != null) {
            mTitleTv.setText(title);
        }
    }

    public void showBtn(final String message,final OnClickButton clickButton) {

        if (mFinishBtn != null) {
            if (!TextUtils.isEmpty(message)) {
                mFinishBtn.setText(message);
            } else {
                mFinishBtn.setText("确定");
            }

            mFinishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(clickButton!=null){
                        clickButton.onClickButton();
                    }
                }
            });

            mFinishBtn.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mLlService.setVisibility(View.GONE);
        }
    }

    public boolean isFinish() {
        if (mFinishBtn.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void showServiceBtn(final OnClickService clickService){
        if(mLlService!=null&&clickService!=null){
            mLlService.setVisibility(View.VISIBLE);
            mServiceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        clickService.enterService();
                    }
                });
                mStopStitchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        clickService.stopStitch();
                    }
                });

        }
    }

    public interface OnClickButton{
        void onClickButton();
    }

    public interface OnClickService{
        void enterService();
        void stopStitch();
    }

    @Override
    public void show() {
     /*   WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.height = DensityUtils.dip2px(mContext,190);
        layoutParams.width = DensityUtils.dip2px(mContext,300);
        getWindow().setAttributes(layoutParams);*/
        super.show();
    }
}
