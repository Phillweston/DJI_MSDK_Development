package com.ew.autofly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;

import java.util.ArrayList;
import java.util.List;



public class LeftMenuAdapter2 extends BaseAdapter {
    private Context mContext;
    private String[] mTitle;
    private int[] mDrawableId;

    public LeftMenuAdapter2(Context c, int[] drawableId, String[] title) {
        this.mContext = c;
        this.mTitle = title;
        this.mDrawableId = drawableId;
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return (mTitle == null || mTitle.length == 0) ? null : mTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_item, null);
            holder = new ViewHolder();
            holder.titleItem = (TextView) convertView.findViewById(R.id.item_textview);
            holder.imgItem = (ImageView) convertView.findViewById(R.id.item_imageview);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.titleItem.setText(mTitle[position]);
        holder.imgItem.setImageResource(mDrawableId[position]);
        return convertView;
    }

    static class ViewHolder {
        TextView titleItem;
        ImageView imgItem;
    }
}