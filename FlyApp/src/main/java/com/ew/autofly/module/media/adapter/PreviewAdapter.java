package com.ew.autofly.module.media.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ew.autofly.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import dji.sdk.media.MediaFile;


public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewHolder> {

    private List<MediaFile> mMediaFileList;

    public PreviewAdapter(List<MediaFile> mediaFileList) {
        mMediaFileList = mediaFileList;
    }

    @NonNull
    @Override
    public PreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PreviewHolder holder;
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_albun_preview, parent, false);

        holder = new PreviewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewHolder holder, int position) {
        MediaFile mediaFile = mMediaFileList.get(position);
        holder.pvPreview.setImageBitmap(mediaFile.getThumbnail());
        if (mediaFile.getMediaType() == MediaFile.MediaType.MOV || mediaFile.getMediaType() == MediaFile.MediaType.MP4) {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.pvPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            holder.ivPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaFileList.size();
    }

    public class PreviewHolder extends RecyclerView.ViewHolder {

        PhotoView pvPreview;
        ImageView ivPlay;


        public PreviewHolder(View itemView) {
            super(itemView);
            pvPreview = (PhotoView) itemView.findViewById(R.id.ptView);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
        }

    }
}
