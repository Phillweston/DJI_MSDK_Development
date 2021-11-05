package com.ew.autofly.dialog.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.entity.mission.PatrolMainJob;
import com.flycloud.autofly.ux.view.progress.ProgressRing;

import java.util.List;


public class PatrolMainJobAdapter extends BaseAdapter {

    private List<PatrolMainJob> mJobList;

    public PatrolMainJobAdapter(List<PatrolMainJob> jobList) {
        mJobList = jobList;
    }

    @Override
    public int getCount() {
        return mJobList == null ? 0 : mJobList.size();
    }

    @Override
    public Object getItem(int position) {
        return mJobList == null ? null : mJobList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patrol_main_job, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PatrolMainJob patrolMainJob = mJobList.get(position);
        holder.tv_worksheetNum.setText(patrolMainJob.getWorksheetNum());
        holder.tv_lineName.setText(patrolMainJob.getLineName());
        holder.tv_worksheetStartToEnd.setText(String.format("%s~%s", patrolMainJob.getWorksheetStart(), patrolMainJob.getWorksheetEnd()));
        holder.pr_inspectRate.setPercent(patrolMainJob.getInspectRate());
        return convertView;
    }


    public static class ViewHolder {

        public View rootView;
        public TextView tv_worksheetNum;
        public TextView tv_lineName;
        public TextView tv_inspectType;
        public TextView tv_worksheetStartToEnd;
        public ProgressRing pr_inspectRate;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.pr_inspectRate = (ProgressRing) rootView.findViewById(R.id.pr_inspectRate);
            this.tv_worksheetNum = (TextView) rootView.findViewById(R.id.tv_worksheetNum);
            this.tv_lineName = (TextView) rootView.findViewById(R.id.tv_lineName);
            this.tv_inspectType = (TextView) rootView.findViewById(R.id.tv_inspectType);
            this.tv_worksheetStartToEnd = (TextView) rootView.findViewById(R.id.tv_worksheetStartToEnd);
        }

    }
}
