package com.flycloud.autofly.design.view.setting;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.flycloud.autofly.R;
import com.flycloud.autofly.ux.view.PopSpinnerView;

import java.util.List;


public class SettingSpinnerView extends BaseSettingView {

    private PopSpinnerView mPsvOption;

    private int mOptionWidth = 0;

    public SettingSpinnerView(Context context) {
        this(context, null);
    }

    public SettingSpinnerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int setRootView() {
        return R.layout.view_setting_spinner;
    }

    @Override
    protected void initXmlConfigAttr(AttributeSet attrs) {
        super.initXmlConfigAttr(attrs);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SettingSpinnerView);

        mOptionWidth = ta.getDimensionPixelSize(R.styleable.SettingSpinnerView_design_ssv_option_width, 0);

        ta.recycle();
    }


    @Override
    public void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mPsvOption = findViewById(R.id.psv_option);
        if (mOptionWidth > 0) {
            setOptionWidth(mOptionWidth);
        }
    }

    private void setOptionWidth(int width) {
        ViewGroup.LayoutParams layoutParams = mPsvOption.getLayoutParams();
        layoutParams.width = width;
        mPsvOption.setLayoutParams(layoutParams);
    }


    public void init(String defaultText, final List<String> itemList, PopSpinnerView.OnSelectCallback callback) {

        mPsvOption.setContent(defaultText);
        mPsvOption.init(itemList.size(), new PopSpinnerView.NameFilter() {
            @Override
            public String filter(int position) {
                return itemList.get(position);
            }
        }, callback);
    }

    
    public void setSelectIndex(int index) {
        mPsvOption.setSelectIndex(index);
    }

    /**
     * 选中item
     *
     * @return
     */
    public void setSelect(int index) {
        mPsvOption.setSelect(index);
    }

    public void setCurrentContent(String content) {
        mPsvOption.setContent(content);
    }

}
