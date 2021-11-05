package com.flycloud.autofly.ux.view.dragview;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;


public class DragRecyAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mDataList;

    public DragRecyAdapter(List<T> dataList) {
        mDataList = dataList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

}
