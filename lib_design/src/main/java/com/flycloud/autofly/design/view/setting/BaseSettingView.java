package com.flycloud.autofly.design.view.setting;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flycloud.autofly.ux.base.BaseWidget;
import com.flycloud.autofly.R;


public abstract class BaseSettingView extends BaseWidget {

    private TextView mMainTitle;

    private TextView mDescription;

    public BaseSettingView(Context context) {
        super(context);
    }

    public BaseSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mMainTitle = findViewById(R.id.tv_main_title);
        mDescription = findViewById(R.id.tv_description);
        initXmlConfigAttr(attrs);
    }

    /**
     * xml属性
     *
     * @param attrs
     */
    protected void initXmlConfigAttr(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BaseSettingView);

        String mainTitleStr = ta.getString(R.styleable.BaseSettingView_design_bsv_main_title);
        if (!TextUtils.isEmpty(mainTitleStr)) {
            setMainTitle(mainTitleStr);
        }

        String descriptionStr = ta.getString(R.styleable.BaseSettingView_design_bsv_description);
        if (!TextUtils.isEmpty(descriptionStr)) {
            setDescription(descriptionStr);
            mDescription.setVisibility(VISIBLE);
        }

        ta.recycle();
    }

    public void setMainTitle(String mainTitle) {
        if (mMainTitle != null) {
            mMainTitle.setText(mainTitle);
        }
    }

    public void setDescription(String description) {
        if (mDescription != null) {
            mDescription.setText(description);
        }
    }
}
