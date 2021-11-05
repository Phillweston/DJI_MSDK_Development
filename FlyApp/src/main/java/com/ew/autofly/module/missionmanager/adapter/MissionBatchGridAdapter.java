package com.ew.autofly.module.missionmanager.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionBatch;
import com.ew.autofly.interfaces.OnRecyclerViewItemClickListener;
import com.ew.autofly.utils.IOUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;



public class MissionBatchGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener,View.OnLongClickListener{

    private Context mContext;
    private List<MissionBatch> mMissionBatchList;


    private int mShowFunctionMenuPosition =-1;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private onClickFunctionMenu mOnClickFunctionMenu=null;
    public void setonClickFunctionMenu(onClickFunctionMenu onClickFunctionMenu) {
        this.mOnClickFunctionMenu = onClickFunctionMenu;
    }

    
    public interface onClickFunctionMenu{
        void onClickRead(int position);
        void onClickDelete(int position);
        void onClickEdit(int position);
        void onClickBluetooth(int position);
    }

    public MissionBatchGridAdapter(Context context, List<MissionBatch> missionBatchList) {
        this.mContext = context;
        this.mMissionBatchList = missionBatchList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.item_mission_manager_card,parent,false);

        MissionViewHolder holder=new MissionViewHolder(view);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MissionBatch missionBatch2=mMissionBatchList.get(position);
        MissionViewHolder missionViewHolder= (MissionViewHolder) holder;

        missionViewHolder.tvName.setText(missionBatch2.getName());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        missionViewHolder.tvDate.setText(format.format(missionBatch2.getCreateDate()));

        int status = missionBatch2.getStatus();
        missionViewHolder.tvStatus.setText(status == 0 ? "未完成" : "已完成");
        missionViewHolder.tvStatus.setTextColor(status == 0 ?mContext.getResources().getColor(R.color.default_red)
                :mContext.getResources().getColor(R.color.default_white));

        if(mShowFunctionMenuPosition ==position){
            missionViewHolder.rlFunction.setVisibility(View.VISIBLE);
            missionViewHolder.rlFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mShowFunctionMenuPosition =-1;
                    notifyDataSetChanged();
                }
            });
        }else {
            missionViewHolder.rlFunction.setVisibility(View.GONE);
        }

        missionViewHolder.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mOnClickFunctionMenu!=null){
                    mOnClickFunctionMenu.onClickRead(position);
                }
            }
        });

        missionViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mOnClickFunctionMenu!=null){
                    mOnClickFunctionMenu.onClickDelete(position);
                }
            }
        });

        missionViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnClickFunctionMenu!=null){
                    mOnClickFunctionMenu.onClickEdit(position);
                }
            }
        });

        missionViewHolder.btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnClickFunctionMenu!=null){
                    mOnClickFunctionMenu.onClickBluetooth(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMissionBatchList.size();
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener!= null) {
            mOnItemClickListener.onItemLongClick(v);
        }
        return false;
    }

    /**
     *  显示功能菜单
     * @param mShowFunctionPosition 当前显示功能菜单的item的位置
     */
    public void openFunctionMenu(int mShowFunctionPosition) {
        this.mShowFunctionMenuPosition = mShowFunctionPosition;
        notifyDataSetChanged();
    }

    
    public void closeFunctionMenu() {
        this.mShowFunctionMenuPosition = -1;
        notifyDataSetChanged();
    }

    private class MissionViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvDate;
        private ImageView ivMap;
        private TextView tvStatus;

        private RelativeLayout rlFunction;
        private Button btnRead;
        private Button btnDelete;
        private Button btnEdit;
        private Button btnBluetooth;

        MissionViewHolder(View itemView) {
            super(itemView);
            tvName= (TextView) itemView.findViewById(R.id.tv_mission_name);
            tvDate= (TextView) itemView.findViewById(R.id.tv_mission_date);
            ivMap= (ImageView) itemView.findViewById(R.id.iv_mission_map);
            tvStatus= (TextView) itemView.findViewById(R.id.tv_mission_finish_state);

            rlFunction= (RelativeLayout) itemView.findViewById(R.id.rl_mission_function);
            btnRead = (Button) itemView.findViewById(R.id.btn_mission_see);
            btnDelete= (Button) itemView.findViewById(R.id.btn_mission_delete);
            btnEdit= (Button) itemView.findViewById(R.id.btn_mission_edit);
            btnBluetooth= (Button) itemView.findViewById(R.id.btn_mission_bluetooth);

        }
    }

}
