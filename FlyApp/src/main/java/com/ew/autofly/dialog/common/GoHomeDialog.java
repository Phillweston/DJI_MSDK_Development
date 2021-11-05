package com.ew.autofly.dialog.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.xflyer.utils.DialogUtils;



public class GoHomeDialog {

    private boolean isShow = false;

    private Context mContext;
    private TextView tvDuration = null;
    private Dialog durationDialog = null;
    private CountDownTimer timer;

    private GoHomeListener mGoHomeListener;

    private String mTitleStr = "";
    private String mMessageStr = "";

    public interface GoHomeListener {

        void confirm();

        void cancel();
    }

    public GoHomeDialog(Context context) {
        mContext = context;
        tvDuration = new TextView(mContext);
        tvDuration.setPadding(20,20,20,20);
    }

    public void setTitle(String title) {
        mTitleStr = title;
    }

    public void setMessage(String message) {
        mMessageStr = message;
    }

    /**
     * 更新信息
     * @param message
     */
    public void updateMessage(String message){
        tvDuration.setText(message);
    }

    public void setGoHomeListener(GoHomeListener goHomeListener) {
        mGoHomeListener = goHomeListener;
    }

    public boolean isShowing() {
        return durationDialog != null && durationDialog.isShowing() && isShow;
    }

    public void show() {

        show(mTitleStr,mMessageStr,true);
    }

    public void show(boolean autoTimer) {

        show(mTitleStr,mMessageStr,autoTimer);
    }


    public void show(String title,String message,boolean autoTimer){
        if (!isShowing()) {
            if (durationDialog == null) {
                createDialog();
            }
            if (autoTimer) {
                createTimer();
            }
            durationDialog.setTitle(title);
            tvDuration.setText(message);
            durationDialog.show();
            isShow = true;
        }
    }



    public void dismiss() {
        if (isShowing()) {
            durationDialog.dismiss();
            isShow = false;
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    private void createDialog() {
        tvDuration.setText(mMessageStr);
        tvDuration.setTextColor(mContext.getResources().getColor(R.color.yellow_dark));
        durationDialog = DialogUtils.createCustomDialog(mContext, -1, mTitleStr, tvDuration, "立即返航", "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mGoHomeListener != null) {
                            mGoHomeListener.confirm();
                        }
                        dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mGoHomeListener != null) {
                            mGoHomeListener.cancel();
                        }
                        dismiss();
                    }
                });
    }

    private void createTimer() {
        /** 倒计时10秒，一次1秒 */
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateMessage(mTitleStr + "，" + millisUntilFinished / 1000 + "秒后自动返航");
            }

            @Override
            public void onFinish() {
                if (mGoHomeListener != null) {
                    mGoHomeListener.confirm();
                }
                dismiss();
            }
        }.start();
    }
}
