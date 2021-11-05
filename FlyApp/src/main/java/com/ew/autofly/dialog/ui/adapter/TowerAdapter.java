package com.ew.autofly.dialog.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.entity.Tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class TowerAdapter extends BaseAdapter {
    private ArrayList<Tower> towerList = new ArrayList<>();
    private Context mContext;
    private Map<Integer, Boolean> map = new HashMap<>();

    private boolean isShowGridLineName = true;

    public TowerAdapter(Context context, ArrayList<Tower> list) {
        this.mContext = context;
        this.towerList = list;
    }

    public void setShowGridLineName(boolean showGridLineName) {
        this.isShowGridLineName = showGridLineName;
    }

    public void invalidate(){
        for (Tower tower : towerList) {
            tower.setChecked(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return towerList.size();
    }

    @Override
    public Object getItem(int position) {
        return towerList == null || towerList.size() == 0 ? null : towerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_tower, parent, false);
            holder = new ViewHolder();
            holder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_check);
            holder.tvTowerNo = (TextView) convertView.findViewById(R.id.tv_tower_no);
            holder.tvLineName = (TextView) convertView.findViewById(R.id.tv_tower_linename);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Tower tower = towerList.get(position);
        String towerNo=tower.getTowerNo();

        String gridLineName = tower.getGridLineName();
        if (gridLineName != null && isShowGridLineName) {

            towerNo+="  ( " + gridLineName.replace(".kml", "") + " )";
        }

        holder.tvTowerNo.setText(towerNo);

        if (tower.isChecked()) {
            map.put(position, true);
            holder.ivCheck.setActivated(true);
            convertView.setBackgroundColor(mContext.getResources().getColor(
                    R.color.menu_item_selected));
        } else {
            map.put(position, false);
            holder.ivCheck.setActivated(false);
            convertView.setBackgroundColor(mContext.getResources().getColor(
                    R.color.menu_item_normal));
        }
        holder.ivCheck.setActivated(map.get(position));
        return convertView;
    }

    static class ViewHolder {
        ImageView ivCheck;
        TextView tvTowerNo;
        TextView tvLineName;
    }
}
