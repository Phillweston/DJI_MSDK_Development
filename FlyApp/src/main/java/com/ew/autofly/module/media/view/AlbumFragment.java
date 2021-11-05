package com.ew.autofly.module.media.view;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;

import com.ew.autofly.R;
import com.ew.autofly.adapter.decoration.GridSpacingItemDecoration;
import com.ew.autofly.base.BaseMvpFragment;
import com.ew.autofly.interfaces.OnRecyclerViewItemClickListener;
import com.ew.autofly.module.media.adapter.AlbumAdapter;
import com.ew.autofly.module.media.bean.BaseDataBean;
import com.ew.autofly.module.media.presenter.AlbumPresenter;
import com.flycloud.autofly.base.util.DensityUtils;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;


public class AlbumFragment extends BaseMvpFragment<IAlbumView, AlbumPresenter> implements IAlbumView {

    private RecyclerView mRcAlbumList;
    private AlbumAdapter mAlbumAdapter;
    private ArrayList<BaseDataBean> mDataList = new ArrayList<>();
    private ProgressBar mPbLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AlbumPresenter createPresenter() {
        return new AlbumPresenter();
    }

    @Override
    protected int setRootViewId() {
        return R.layout.fragment_media_album;
    }

    @Override
    protected void initRootView(View view) {
        mRcAlbumList = (RecyclerView) view.findViewById(R.id.rc_album_list);
        initRecycle();
        mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.init(mDataList);
    }

    private void initRecycle() {
        final int gridColumn = 6;
        mAlbumAdapter = new AlbumAdapter(getContext(), mDataList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), gridColumn);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAlbumAdapter.getItemViewType(position) == BaseDataBean.TYPE_TITLE) {
                    return gridColumn;
                }
                return 1;
            }
        });
        mRcAlbumList.addItemDecoration(new GridSpacingItemDecoration(gridColumn, DensityUtils.dip2px(getActivity(), 4), false));

        mRcAlbumList.setLayoutManager(gridLayoutManager);
        mRcAlbumList.setAdapter(mAlbumAdapter);

        mAlbumAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = mRcAlbumList.getChildAdapterPosition(view);
                mPresenter.enterPreview(getActivity(), position);
            }

            @Override
            public void onItemLongClick(View view) {
            }
        });
    }

    @Override
    public void refreshData(ArrayList<BaseDataBean> dataList) {


        mAlbumAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String toast) {
        ToastUtil.show(getContext(), toast);
    }

    @Override
    public void showLoading(boolean isShow, String loadingMsg) {
        mPbLoading.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(boolean isShow, String errorMsg) {

    }

    @Override
    public void showEmpty(boolean isShow, String emptyMsg) {

    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }
}
