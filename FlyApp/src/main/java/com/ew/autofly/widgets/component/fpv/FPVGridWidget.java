package com.ew.autofly.widgets.component.fpv;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ew.autofly.R;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.internal.key.callback.KeyListener;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;


public class FPVGridWidget extends ImageView {

    public FPVGridWidget(Context context) {
        this(context, null);
    }

    public FPVGridWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FPVGridWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
      
        setImageResource(R.drawable.bg_camera_grid);
        setScaleType(ScaleType.FIT_XY);
        Object isOpenGrid = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.VIDEO_GRID));
        setVisibility(isOpenGrid != null && (Boolean) isOpenGrid ? View.VISIBLE : View.GONE);
        setUpKeys();
    }

    @Override
    protected void onDetachedFromWindow() {
        tearDownKeys();
        super.onDetachedFromWindow();
    }

    protected void setUpKeys() {
        FlyKeyManager.getInstance().addListener(FlySettingConfigKey.create(FlySettingConfigKey.VIDEO_GRID), mGridListener);
    }

    protected void tearDownKeys() {
        FlyKeyManager.getInstance().removeListener(mGridListener);

    }

    private KeyListener mGridListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof Boolean) {
                setVisibility((Boolean) newValue ? View.VISIBLE : View.GONE);
            }
        }
    };
}
