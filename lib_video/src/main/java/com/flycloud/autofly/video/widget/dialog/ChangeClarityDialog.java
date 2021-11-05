package com.flycloud.autofly.video.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycloud.autofly.video.R;
import com.flycloud.autofly.video.util.VideoUtil;

import java.util.List;


public class ChangeClarityDialog extends Dialog {

    private LinearLayout mLinearLayout;
    private int mCurrentCheckedIndex;

    public ChangeClarityDialog(Context context) {
        super(context, R.style.video_dialog_change_clarity);
        init(context);
    }

    private void init(Context context) {
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setGravity(Gravity.CENTER);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClarityNotChanged();
                }
                ChangeClarityDialog.this.dismiss();
            }
        });

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT);
        setContentView(mLinearLayout, params);

        WindowManager.LayoutParams windowParams = getWindow().getAttributes();
        windowParams.width = VideoUtil.getScreenHeight(context);
        windowParams.height = VideoUtil.getScreenWidth(context);
        getWindow().setAttributes(windowParams);
    }


    public void setClarityGrade(List<String> items, int defaultChecked) {
        mCurrentCheckedIndex = defaultChecked;
        for (int i = 0; i < items.size(); i++) {
            TextView itemView = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.video_item_change_clarity, mLinearLayout, false);
            itemView.setTag(i);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int checkIndex = (int) v.getTag();
                        if (checkIndex != mCurrentCheckedIndex) {
                            for (int j = 0; j < mLinearLayout.getChildCount(); j++) {
                                mLinearLayout.getChildAt(j).setSelected(checkIndex == j);
                            }
                            mListener.onClarityChanged(checkIndex);
                            mCurrentCheckedIndex = checkIndex;
                        } else {
                            mListener.onClarityNotChanged();
                        }
                    }
                    ChangeClarityDialog.this.dismiss();
                }
            });
            itemView.setText(items.get(i));
            itemView.setSelected(i == defaultChecked);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                    itemView.getLayoutParams();
            params.topMargin = (i == 0) ? 0 : VideoUtil.dp2px(getContext(), 16f);
            mLinearLayout.addView(itemView, params);
        }
    }

    public interface OnClarityChangedListener {
        /**
         * 切换清晰度后回调
         *
         * @param clarityIndex 切换到的清晰度的索引值
         */
        void onClarityChanged(int clarityIndex);

        
        void onClarityNotChanged();
    }

    private OnClarityChangedListener mListener;

    public void setOnClarityCheckedListener(OnClarityChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onBackPressed() {
      
        if (mListener != null) {
            mListener.onClarityNotChanged();
        }
        super.onBackPressed();
    }
}
