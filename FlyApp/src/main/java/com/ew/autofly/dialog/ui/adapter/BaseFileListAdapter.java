package com.ew.autofly.dialog.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ew.autofly.utils.io.file.FileInfo;

import java.util.List;


public abstract class BaseFileListAdapter extends BaseAdapter {

    protected List<FileInfo> fileInfoList;
    protected Context mContext;

    public BaseFileListAdapter(Context context, List<FileInfo> fileInfoList) {
        mContext = context;
        this.fileInfoList = fileInfoList;
    }

    @Override
    public int getCount() {
        return fileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void onItemClick(int position) {
        FileInfo fileInfo = (FileInfo) getItem(position);
        if (mOnFileSelectListener != null) {
            mOnFileSelectListener.onSelected(fileInfo);
        }
    }

    private OnFileSelectListener mOnFileSelectListener;

    public void setOnFileSelectListener(OnFileSelectListener onFileSelectListener) {
        mOnFileSelectListener = onFileSelectListener;
    }

    public interface OnFileSelectListener {

        void onSelected(FileInfo fileInfo);
    }
}
