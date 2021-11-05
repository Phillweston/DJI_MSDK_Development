package com.ew.autofly.module.media.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ew.autofly.R;


 class AlbumBottomMenu extends LinearLayout {

    private ImageView iv_delete, iv_share;

  
    private TranslateAnimation mShowActionBottom;
    private TranslateAnimation mHiddenActionBottom;

    private AlubmBottomMenuListener menuListener;

    public AlbumBottomMenu(Context context) {
        this(context, null);
    }

    public AlbumBottomMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_media_album_menu, this);
        initAnimation();
        initView();
    }

    private void initView() {
        iv_delete = findViewById(R.id.iv_delete);
        iv_share = findViewById(R.id.iv_share);

        iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuListener != null) {
                    menuListener.onDeleteClick();
                }
            }
        });
        iv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuListener != null) {
                    menuListener.onShareClick();
                }
            }
        });
    }

    
    private void initAnimation() {
      
        mShowActionBottom = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowActionBottom.setDuration(500);
      
        mHiddenActionBottom = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        mHiddenActionBottom.setDuration(500);
    }

    public void setMenuListener(AlubmBottomMenuListener menuListener) {
        this.menuListener = menuListener;
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            startAnimation(mShowActionBottom);
        } else {
            startAnimation(mHiddenActionBottom);
        }
        super.setVisibility(visibility);
    }

    public interface AlubmBottomMenuListener {
        
        void onDeleteClick();

        
        void onShareClick();
    }
}
