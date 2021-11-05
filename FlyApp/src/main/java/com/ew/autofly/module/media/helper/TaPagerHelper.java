package com.ew.autofly.module.media.helper;

import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TaPagerHelper extends PagerSnapHelper {
    private PageHelperListener listener;

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        int pos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
        if (listener != null) {
            listener.onPageChanged(pos);
        }
        return pos;
    }

    public void setListener(PageHelperListener listener) {
        this.listener = listener;
    }

    public interface PageHelperListener {
        void onPageChanged(int position);
    }
}
