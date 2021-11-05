package com.ew.autofly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ew.autofly.R;



public class DroneModelAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mData;

    public DroneModelAdapter(Context c, String[] data) {
        this.mContext = c;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public Object getItem(int position) {
        return (mData == null || mData.length == 0) ? null : mData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.model_item, null);
            holder = new ViewHolder();
            holder.modelItem = (TextView) convertView.findViewById(R.id.tv_model);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.modelItem.setText(mData[position]);
        return convertView;
    }

    static class ViewHolder {
        TextView modelItem;
    }

    public void updateData(String[] data) {
        mData = data;
        notifyDataSetChanged();
    }
}
