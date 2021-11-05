package com.ew.autofly.dialog.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.utils.io.file.FileInfo;

import java.util.List;


public class FolderListAdapter extends BaseFileListAdapter {

    public FolderListAdapter(Context context, List<FileInfo> fileInfoList) {
        super(context, fileInfoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_text, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        FileInfo fileInfo = fileInfoList.get(position);
        holder.tv_file_name.setText(fileInfo.getFileName());
        if (fileInfo.isDirectory){
            holder.iv_folder.setImageResource(R.drawable.ic_list_folder);
            holder.iv_right_arrow.setVisibility(View.VISIBLE);
        }else {
            holder.iv_folder.setImageResource(R.drawable.ic_list_file);
            holder.iv_right_arrow.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        public View rootView;
        public ImageView iv_folder;
        public TextView tv_file_name;
        public ImageView iv_right_arrow;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.iv_folder = (ImageView) rootView.findViewById(R.id.iv_file_type);
            this.tv_file_name = (TextView) rootView.findViewById(R.id.tv_file_name);
            this.iv_right_arrow = (ImageView) rootView.findViewById(R.id.iv_right_arrow);
        }

    }
}
