package com.ew.autofly.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ew.autofly.R;
import com.ew.autofly.widgets.CustomSeekbar.BubbleSeekBar;



public class CustomBar extends RelativeLayout implements View.OnClickListener {

    private final int max = 100;
    private final int min = 1;
    private final int progress = 1;
    private final int step = 1;
    public BubbleSeekBar bubbleSeekBar;
    private boolean enable = true;

    private View view;

    public CustomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = View.inflate(context, R.layout.setting_dialog_seekbar, this);
        init();
    }

    private void init() {
        if (view == null) {
            return;
        }
        Button add = view.findViewById(R.id.btn_settingbar_add);
        Button reduce = view.findViewById(R.id.btn_settingbar_reduct);

        add.setBackgroundResource(isEnable() ? R.drawable.icon_custom_seekbar_add : R.drawable
                .icon_custom_seekbar_add_unable);
        reduce.setBackgroundResource(isEnable() ? R.drawable.icon_custom_seekbar_reduct : R.drawable
                .icon_custom_seekbar_reduct_unable);
        add.setOnClickListener(this);
        reduce.setOnClickListener(this);

        bubbleSeekBar = (BubbleSeekBar) view.findViewById(R.id.setting_bar);
        bubbleSeekBar.getConfigBuilder().max(max)
                .min(min)
                .progress(progress)
                .sectionCount(step)
                .autoAdjustSectionMark()
                .showSectionMark()
                .seekEnable(isEnable())
                .secondTrackColor(Color.parseColor(isEnable() ? "#25E0E7" : "#999999"))
                .trackColor(Color.parseColor("#DADADA"))
                .bubbleColor(Color.parseColor(isEnable() ? "#25E0E7" : "#999999"))
                .trackSize(8)
                .build();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        init();
    }

    @Override
    public void onClick(View view) {
        if (!isEnable()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_settingbar_reduct:
                if (bubbleSeekBar.getProgress() > bubbleSeekBar.getConfigBuilder().getMin()) {
                    bubbleSeekBar.setProgress(bubbleSeekBar.getProgress() -
                            (bubbleSeekBar.getConfigBuilder().getMax() - bubbleSeekBar.getConfigBuilder().getMin())
                                    / bubbleSeekBar.getConfigBuilder().getSectionCount());
                }
                break;
            case R.id.btn_settingbar_add:
                if (bubbleSeekBar.getProgress() < bubbleSeekBar.getConfigBuilder().getMax()) {
                    bubbleSeekBar.setProgress(bubbleSeekBar.getProgress() +
                            (bubbleSeekBar.getConfigBuilder().getMax() - bubbleSeekBar.getConfigBuilder().getMin())
                                    / bubbleSeekBar.getConfigBuilder().getSectionCount());
                }
                break;
            default:
                break;
        }
    }
}
