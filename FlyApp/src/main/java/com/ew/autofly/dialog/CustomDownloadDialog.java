package com.ew.autofly.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ew.autofly.R;



public class CustomDownloadDialog extends Dialog {

    private ProgressBar mPb;
    private TextView mPbTv;

    public CustomDownloadDialog(@NonNull Context context) {
        this(context, R.style.CustomDownloadDialogTheme);
    }

    public CustomDownloadDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public CustomDownloadDialog(Context context, Builder builder) {
        this(context);
        setContentView(builder.p.layout);
        mPb = (ProgressBar) findViewById(R.id.progressBar);
        mPbTv = (TextView) findViewById(R.id.tv_progress);
        if (mPb != null) {
            mPb.setMax(builder.p.progressMax);
            mPb.setProgress(builder.p.progress);
        }

        if (mPbTv != null) {
            mPbTv.setText(builder.p.progress + "%");
        }

        TextView cancelBtn = (TextView) findViewById(R.id.tv_download_cancel);
        if (cancelBtn != null && builder.p.cancelListener != null) {
            cancelBtn.setOnClickListener(builder.p.cancelListener);
        }

        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        if (titleTv != null) {
            titleTv.setText(builder.p.title);
        }
    }

    public void changeProgress(int progress) {
        if (mPb != null) {
            mPb.setProgress(progress);
        }

        if (mPbTv != null) {
            mPbTv.setText(progress + "%");
        }
    }

    public static class Builder {

        private Params p;

        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
            p = new Params();
        }

        public Builder setContentView(int resId) {
            p.layout = resId;
            return this;
        }

        public Builder setTitle(String title) {
            p.title = title;
            return this;
        }

        public Builder setProgressView(int resId) {
            p.progressLayout = resId;
            return this;
        }

        public Builder setProgress(int progress) {
            p.progress = progress;
            return this;
        }

        public Builder setProgressMax(int progressMax) {
            p.progressMax = progressMax;
            return this;
        }

        public Builder setOnCancelListener(View.OnClickListener cancelListener) {
            p.cancelListener = cancelListener;
            return this;
        }

        public CustomDownloadDialog create() {
            return new CustomDownloadDialog(mContext, this);
        }

    }

    static class Params {
        int layout = R.layout.dialog_download;
        String title;
        int progressLayout;
        int progress = 0;
        int progressMax = 100;
        View.OnClickListener cancelListener;
    }

}
