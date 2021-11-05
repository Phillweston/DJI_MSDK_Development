package com.ew.autofly.dialog.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.utils.io.file.FileInfo;

import java.util.ArrayList;
import java.util.List;


public class FileListCheckAdapter extends BaseFileListAdapter {

    protected boolean isSingleCheck = false;

    protected boolean isCheckModeEnable = true;

    public FileListCheckAdapter(Context context, List<FileInfo> fileInfoList) {
        super(context, fileInfoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_file, parent, false);
            holder = new ViewHolder();
            holder.cbSelect = (CheckBox) convertView.findViewById(R.id.cb_select_state);
            holder.tvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        FileInfo fileInfo = fileInfoList.get(position);
        holder.cbSelect.setChecked(fileInfo.isCheck());
        holder.tvFileName.setText(fileInfo.getFileName());
        holder.tvFileName.setSelected(true);
        convertView.setBackgroundColor(fileInfo.isCheck() ? mContext.getResources().getColor(R.color.menu_item_selected)
                : mContext.getResources().getColor(R.color.menu_item_normal));
        return convertView;
    }

    private class ViewHolder {
        CheckBox cbSelect;
        TextView tvFileName;
    }

    @Override
    public void onItemClick(int position) {
        if (isCheckModeEnable) {
            checkItem(position);
        }else {
            super.onItemClick(position);
        }
    }

    private void checkItem(int position){
        FileInfo fileInfo = (FileInfo) getItem(position);
        if (isSingleCheck) {
            clearCheck();
            fileInfo.setCheck(true);
        } else {
            fileInfo.setCheck(!fileInfo.isCheck());
        }
        notifyDataSetChanged();
    }

    public List<FileInfo> getSelectedFileInfo() {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo.isCheck()) {
                fileInfos.add(fileInfo);
            }
        }
        return fileInfos;
    }

    public void clearCheck() {
        for (FileInfo fileInfo : fileInfoList) {
            fileInfo.setCheck(false);
        }
    }

    public void checkAll(){
        for (FileInfo fileInfo : fileInfoList) {
            fileInfo.setCheck(true);
        }
    }

    public void enableSingleCheck(boolean isSingleCheck) {
        this.isSingleCheck = isSingleCheck;
    }

    public void enableCheckMode(boolean isEnable) {
        this.isCheckModeEnable = isEnable;
    }

}
