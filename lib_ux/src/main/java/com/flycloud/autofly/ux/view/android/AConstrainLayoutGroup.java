package com.flycloud.autofly.ux.view.android;

import android.content.Context;
import android.os.Build;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import android.util.AttributeSet;
import android.view.View;


public class AConstrainLayoutGroup extends Group {
    public AConstrainLayoutGroup(Context context) {
        super(context);
    }

    public AConstrainLayoutGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AConstrainLayoutGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void updatePreLayout(ConstraintLayout container) {
        int visibility = this.getVisibility();
        float elevation = 0.0F;
        if (Build.VERSION.SDK_INT >= 21) {
            elevation = this.getElevation();
        }

        for (int i = 0; i < this.mCount; ++i) {
            int id = this.mIds[i];
            View view = container.getViewById(id);
            if (view != null && view.getVisibility() != GONE) {
                view.setVisibility(visibility);
                if (elevation > 0.0F && Build.VERSION.SDK_INT >= 21) {
                    view.setElevation(elevation);
                }
            }
        }
    }
}
