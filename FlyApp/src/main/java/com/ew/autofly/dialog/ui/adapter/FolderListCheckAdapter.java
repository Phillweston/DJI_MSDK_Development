package com.ew.autofly.dialog.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.utils.io.file.FileInfo;

import java.util.List;


public class FolderListCheckAdapter extends FileListCheckAdapter {

    public FolderListCheckAdapter(Context context, List<FileInfo> fileInfoList) {
        super(context, fileInfoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_folder_check, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileInfo fileInfo = fileInfoList.get(position);
        holder.tv_file_name.setText(fileInfo.getFileName());
        holder.cb_select.setVisibility(isCheckModeEnable ? View.VISIBLE : View.GONE);
        if (fileInfo.isDirectory) {
            holder.iv_folder.setImageResource(R.drawable.ic_list_folder);
            holder.iv_right_arrow.setVisibility(View.VISIBLE);
        } else {
            holder.iv_folder.setImageResource(R.drawable.ic_list_file);
            holder.iv_right_arrow.setVisibility(View.INVISIBLE);
        }
        holder.cb_select.setChecked(fileInfo.isCheck());
        convertView.setBackgroundColor(mContext.getResources().getColor(
                fileInfo.isCheck() ? R.color.menu_item_selected : R.color.menu_item_normal));
        return convertView;
    }


    private class ViewHolder {
        public View rootView;
        public CheckBox cb_select;
        public ImageView iv_folder;
        public TextView tv_file_name;
        public ImageView iv_right_arrow;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.cb_select = rootView.findViewById(R.id.cb_select_state);
            this.iv_folder = (ImageView) rootView.findViewById(R.id.iv_file_type);
            this.tv_file_name = (TextView) rootView.findViewById(R.id.tv_file_name);
            this.iv_right_arrow = (ImageView) rootView.findViewById(R.id.iv_right_arrow);
        }

    }
}
