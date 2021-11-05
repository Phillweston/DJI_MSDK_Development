package com.ew.autofly.module.media.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ew.autofly.R;
import com.ew.autofly.module.media.adapter.PreviewAdapter;
import com.ew.autofly.module.media.helper.MediaDataHelper;
import com.ew.autofly.module.media.helper.TaPagerHelper;
import com.flycloud.autofly.base.base.BaseFragmentActivity;

import java.util.List;

import dji.sdk.media.MediaFile;


public class PreviewMediaActivity extends BaseFragmentActivity {


    public final static String PREVIEW_POSITION = "PREVIEW_POSITION";

    private RecyclerView mRecyclerView;
    private AlbumBottomMenu mAlbumBottomMenu;

    private List<MediaFile> mMediaFiles;
    private PreviewAdapter mAdapter;
    private TaPagerHelper mTaPagerHelper = new TaPagerHelper();

    private int mPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_media_preview);
        initData();
        initView();
        initRecycle();
    }

    private void initData() {

        mMediaFiles = MediaDataHelper.getInstance().getMediaFileList();
        mPosition = getIntent().getIntExtra(PREVIEW_POSITION, 0);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rcList);
        mAlbumBottomMenu = findViewById(R.id.abMenu);
    }

    private void initRecycle() {

        if (mMediaFiles != null && !mMediaFiles.isEmpty()) {
            if (mPosition > mMediaFiles.size() - 1) {
                mPosition = mMediaFiles.size() - 1;
            } else if (mPosition < 0) {
                mPosition = 0;
            }
            mAdapter = new PreviewAdapter(mMediaFiles);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.scrollToPosition(mPosition);

            mTaPagerHelper.setListener(new TaPagerHelper.PageHelperListener() {
                @Override
                public void onPageChanged(int position) {


                }
            });
            mTaPagerHelper.attachToRecyclerView(mRecyclerView);
        }
    }
}
