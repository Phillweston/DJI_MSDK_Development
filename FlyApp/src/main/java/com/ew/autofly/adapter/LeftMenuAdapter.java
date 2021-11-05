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



public class LeftMenuAdapter extends BaseAdapter {
    private Context mContext;


    private List<String> titleList = new ArrayList<>();
    private List<Integer> iconList = new ArrayList<>();







    public LeftMenuAdapter(Context c, List<Integer> icons, List<String> titles) {
        this.mContext = c;
        this.iconList = icons;
        this.titleList = titles;
    }

    @Override
    public int getCount() {
        return iconList.size();
    }

    @Override
    public Object getItem(int position) {
        return (titleList == null || titleList.size() == 0) ? null : titleList.get(position);
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


        holder.titleItem.setText(titleList.get(position));
        holder.imgItem.setImageResource(iconList.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView titleItem;
        ImageView imgItem;
    }
}