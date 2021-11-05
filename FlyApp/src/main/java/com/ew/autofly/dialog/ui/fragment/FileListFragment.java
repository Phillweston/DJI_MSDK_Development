package com.ew.autofly.dialog.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ew.autofly.R;
import com.ew.autofly.dialog.ui.adapter.BaseFileListAdapter;



public class FileListFragment extends Fragment {

    private ViewGroup rootView;
    private ListView mLvFileList;
    private BaseFileListAdapter mAdapter;
    private ProgressBar mLvFileProgress;
    private View mNoDataView;

    public static FileListFragment newInstance(BaseFileListAdapter adapter) {

        Bundle args = new Bundle();
        FileListFragment fragment = new FileListFragment();
        fragment.mAdapter = adapter;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.layout_file_list, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        mNoDataView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_empty_data,
                rootView, false);
        mLvFileProgress = (ProgressBar) rootView.findViewById(R.id.lv_file_progress);
        mLvFileList = (ListView) rootView.findViewById(R.id.lv_file_list);

        mLvFileList.setAdapter(mAdapter);
        mLvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.onItemClick(position);
            }
        });

        showNoData(mAdapter.isEmpty());
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        showNoData(mAdapter.isEmpty());
    }

    public void showNoData(boolean visible) {
        if (mNoDataView != null) {
            rootView.removeView(mNoDataView);
            if (visible) {
                rootView.addView(mNoDataView);
            }
        }
        showLoading(false);
    }

    public void showLoading(boolean isShow) {
        if (mLvFileProgress != null) {
            mLvFileProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }
}
