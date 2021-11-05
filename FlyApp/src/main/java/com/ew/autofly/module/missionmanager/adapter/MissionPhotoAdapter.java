package com.ew.autofly.module.missionmanager.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.utils.IOUtils;

import java.io.File;
import java.util.List;



public class MissionPhotoAdapter extends BaseAdapter {

    private List<MissionPhoto> mPhotoList;
    private Context mContext;
    private String rootPath;

    public MissionPhotoAdapter(Context context, List<MissionPhoto> list) {
        mContext = context;
        this.mPhotoList = list;
        rootPath = IOUtils.getRootStoragePath(mContext);
    }
    @Override
    public int getCount() {
        return mPhotoList ==null ? 0:mPhotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPhotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MissionPhoto missionPhoto = mPhotoList.get(position);
        PhotoViewHolder photoViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mission_photo,null);
            photoViewHolder = new PhotoViewHolder();
            photoViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_shot_photo);
            photoViewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_shot_photo_name);
            photoViewHolder.mAlreadyDownloadIv = (ImageView) convertView.findViewById(R.id.iv_already_dowload);

            convertView.setTag(photoViewHolder);
        }else {
            photoViewHolder = (PhotoViewHolder) convertView.getTag();
        }

        if(missionPhoto.getPhotoPath()!=null){
            photoViewHolder.mTextView .setText(missionPhoto.getPhotoPath());
        }else {
            photoViewHolder.mTextView .setText("暂无图片");
        }

        String photoPath = "file://" + rootPath + AppConstant.DIR_DRONE_PHOTO
                + "/" +missionPhoto.getMissionId()+ "/" + missionPhoto.getPhotoPath();

        photoPath = photoPath.replace(".mov",".jpg");
        photoPath = photoPath.replace(".mp4",".jpg");

        Glide.with(mContext).load(photoPath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(photoViewHolder.mImageView);

        if (!TextUtils.isEmpty(missionPhoto.getBigPhotoPath())){
            String filePath = rootPath + AppConstant.DIR_DRONE_PHOTO
                    + "/" +missionPhoto.getMissionId()+ "/" + missionPhoto.getBigPhotoPath();

            File file = new File(filePath);
            if (file.exists()) {
                photoViewHolder.mAlreadyDownloadIv.setVisibility(View.VISIBLE);
            }else {
                photoViewHolder.mAlreadyDownloadIv.setVisibility(View.GONE);
            }
        }else {
            photoViewHolder.mAlreadyDownloadIv.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class PhotoViewHolder{
        ImageView mImageView;

        TextView mTextView;

        ImageView mAlreadyDownloadIv;
    }


}
