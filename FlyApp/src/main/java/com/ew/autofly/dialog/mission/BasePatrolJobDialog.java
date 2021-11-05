package com.ew.autofly.dialog.mission;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.flycloud.autofly.base.util.DensityUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


public class BasePatrolJobDialog extends BaseDialogFragment {

    protected SmartRefreshLayout mRefreshLayout;
    protected View mNoDataView;
    protected FrameLayout mContentView;

    protected ImageView mIvLoading;
    protected ListView mJobListLv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f, 0.9f);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_patrol_job, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitleBar();
    }

    protected void initView(@NonNull final View view) {
        mContentView = view.findViewById(R.id.content);
        mNoDataView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_empty_data,
                mContentView, false);
        mJobListLv = (ListView) view.findViewById(R.id.lv_job_list);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        addLoading();

    }

    protected void setTitleBar(){

    }

    protected void initData() {

    }


    protected void addLoading(){
        mIvLoading=new ImageView(getContext());
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(DensityUtils.dp2px(getContext(),16),DensityUtils.dp2px(getContext(),16));
        params.gravity=Gravity.CENTER;
        mIvLoading.setLayoutParams(params);
        mContentView.addView(mIvLoading);
        Glide.with(this).load(R.drawable.loading).asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mIvLoading);
    }

    protected void removeLoading(){
        mContentView.removeView(mIvLoading);
    }

    protected void showNoData(boolean visible) {
        if (mNoDataView != null) {
            mContentView.removeView(mNoDataView);
            if (visible) {
                mContentView.addView(mNoDataView);
            }
        }
    }
}
