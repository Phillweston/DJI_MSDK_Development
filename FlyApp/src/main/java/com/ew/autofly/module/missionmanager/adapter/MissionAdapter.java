package com.ew.autofly.module.missionmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.db.entity.MissionBase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class MissionAdapter extends BaseAdapter {
    private Context mContext;
    private List<MissionBase> missionList;

    private int mSelectedPosition = -1;

    public MissionAdapter(Context c, List<MissionBase> data) {
        this.mContext = c;
        this.missionList = data;
    }

    /**
     * 获取选中任务
     *
     * @return 无选中返回null
     */
    public MissionBase getSelectedMission(int selectedPosition) {

        setSelectedPosition(selectedPosition);

        if (this.mSelectedPosition >= 0 && this.mSelectedPosition < getCount()) {
            return missionList.get(this.mSelectedPosition);
        }

        return null;
    }

    public void setSelectedPosition(int selectedPosition) {
        if (selectedPosition >= 0 && selectedPosition < getCount()
                && selectedPosition != mSelectedPosition) {
            this.mSelectedPosition = selectedPosition;
            notifyDataSetChanged();
        } else {
            this.mSelectedPosition = -1;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return null == missionList ? 0 : missionList.size();
    }

    @Override
    public Object getItem(int position) {
        return (missionList == null || missionList.size() == 0) ? null : missionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mission, null);
            holder = new ViewHolder();
            holder.cbCheck = (CheckBox) convertView.findViewById(R.id.cb_check);
            holder.tvFlightNum = (TextView) convertView.findViewById(R.id.tv_flight_num);
            holder.tvStartDateTime = (TextView) convertView.findViewById(R.id.tv_start_time);
            holder.tvFinishDateTime = (TextView) convertView.findViewById(R.id.tv_end_time);
            holder.tvAltitude = (TextView) convertView.findViewById(R.id.tv_altitude);
            holder.tvOverlap = (TextView) convertView.findViewById(R.id.tv_overlap);
            holder.tvPhotoNum = (TextView) convertView.findViewById(R.id.tv_photo_num);
            holder.tvFinishStatus = (TextView) convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        MissionBase mission = missionList.get(position);

        holder.tvFlightNum.setText(String.valueOf(position + 1));
        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
        if (null != mission.getStartTime())
            holder.tvStartDateTime.setText(format.format(mission.getStartTime()));
        if (null != mission.getEndTime())
            holder.tvFinishDateTime.setText(format.format(mission.getEndTime()));
        holder.tvAltitude.setText((mission.getAltitude() == MissionBase.NaN ? "无" : (mission.getAltitude()
                + "m")) + "/" + (mission.getResolutionRate() == MissionBase.NaN ? "无" :
                (new DecimalFormat("##.##").format((mission.getResolutionRate())) + "cm")));
        holder.tvOverlap.setText(mission.getRouteOverlap() == MissionBase.NaN ? "无" : (mission.getRouteOverlap()
                + "%") + "/" + (mission.getSideOverlap() == MissionBase.NaN ? "无" : (mission.getSideOverlap() + "%")));
        int photoNum = 0;
        if (mission.getEndPhotoIndex() != 0 && mission.getEndPhotoIndex() >= mission.getStartPhotoIndex()) {
            photoNum = mission.getEndPhotoIndex() - mission.getStartPhotoIndex() + 1;
        }
        holder.tvPhotoNum.setText(String.valueOf(photoNum));
        int status = mission.getStatus();
        holder.tvFinishStatus.setText(status == 0 ? "未完成" : "已完成");
        holder.tvFinishStatus.setTextColor(status == 0 ?
                mContext.getResources().getColor(R.color.default_red) : mContext.getResources().getColor(R.color.white));

        holder.cbCheck.setChecked(mSelectedPosition == position);
        convertView.setBackgroundColor(mSelectedPosition == position ? mContext.getResources().getColor(
                R.color.menu_item_selected) : mContext.getResources().getColor(
                R.color.transparent));

        return convertView;
    }

    static class ViewHolder {
        CheckBox cbCheck;
        TextView tvFlightNum;
        TextView tvStartDateTime;
        TextView tvFinishDateTime;
        TextView tvAltitude;
        TextView tvOverlap;
        TextView tvPhotoNum;
        TextView tvFinishStatus;
    }

    public void updateData(List<MissionBase> data) {
        missionList.clear();
        missionList.addAll(data);
        notifyDataSetChanged();
    }
}