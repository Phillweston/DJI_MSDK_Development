package com.ew.autofly.module.media.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.interfaces.OnRecyclerViewItemClickListener;
import com.ew.autofly.module.media.bean.AlbumBean;
import com.ew.autofly.module.media.bean.BaseDataBean;
import com.ew.autofly.module.media.bean.TitleBean;

import java.util.ArrayList;

import dji.sdk.media.MediaFile;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private ArrayList<BaseDataBean> mDataList;

    public AlbumAdapter(Context context, ArrayList<BaseDataBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AlbumHolder albumHolder;
        View itemView;
        if (viewType == BaseDataBean.TYPE_TITLE) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_album_title, null, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_album, null, false);
        }
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        albumHolder = new AlbumHolder(itemView, viewType);

        return albumHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {
        if (holder.type == BaseDataBean.TYPE_TITLE) {
            holder.tvTitle.setText(((TitleBean) mDataList.get(position)).getTime());
        } else {
            MediaFile mediaFile = ((AlbumBean) mDataList.get(position)).getMediaFile();
            holder.ivThumb.setImageBitmap(mediaFile.getThumbnail());
            if (mediaFile.getMediaType() == MediaFile.MediaType.MOV || mediaFile.getMediaType() == MediaFile.MediaType.MP4) {
                holder.ivPlay.setVisibility(View.VISIBLE);
            } else {
                holder.ivPlay.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getType();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemLongClick(v);
        }
        return false;
    }

    public class AlbumHolder extends RecyclerView.ViewHolder {

        int type;

        TextView tvTitle;
        ImageView ivThumb;
        ImageView ivPlay;
        View vMask;
        CheckBox cbCheck;

        public AlbumHolder(View itemView) {
            super(itemView);
        }

        public AlbumHolder(View itemView, int type) {
            super(itemView);
            this.type = type;
            if (type == BaseDataBean.TYPE_TITLE) {
                tvTitle = (TextView) itemView.findViewById(R.id.title);
            } else if (type == BaseDataBean.TYPE_MEDIA) {
                ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
                ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
                vMask = itemView.findViewById(R.id.v_mask);
                cbCheck = (CheckBox) itemView.findViewById(R.id.cb_check);
            }
        }
    }

}
