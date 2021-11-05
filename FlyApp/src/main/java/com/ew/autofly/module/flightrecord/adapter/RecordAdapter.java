package com.ew.autofly.module.flightrecord.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.interfaces.OnRecyclerViewItemClickListener;
import com.ew.autofly.utils.coordinate.CoordConvertManager;
import com.ew.autofly.xflyer.utils.DateHelperUtils;

import java.text.DecimalFormat;
import java.util.List;



public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private List<FlightRecord> mRecordList;


    private int mShowFunctionMenuPosition = -1;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private onClickFunctionMenu mOnClickFunctionMenu = null;

    public void setonClickFunctionMenu(onClickFunctionMenu onClickFunctionMenu) {
        this.mOnClickFunctionMenu = onClickFunctionMenu;
    }

    
    public interface onClickFunctionMenu {
        void onClickRead(int position);

        void onClickDelete(int position);

        void onClickUpload(int position);
    }

    public RecordAdapter(Context context, List<FlightRecord> recordList) {
        this.mContext = context;
        this.mRecordList = recordList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_flight_record, parent, false);

        RecordViewHolder holder = new RecordViewHolder(view);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {

            RecordViewHolder recordViewHolder = (RecordViewHolder) holder;

            final FlightRecord record = mRecordList.get(position);

            recordViewHolder.tvDate.setText(record.getCreatedTime());

            DecimalFormat decimalFormat = new DecimalFormat("###,##0");

            recordViewHolder.tvMiles.setText(decimalFormat.format((int)record.getDistance()) + "m");

            recordViewHolder.tvMaxHeight.setText(decimalFormat.format(record.getMaxHeight()) + "m");

            String startTimeStr = record.getStartTime();
            String endTimeStr = record.getEndTime();
            String time="";

            if (!TextUtils.isEmpty(startTimeStr) && !TextUtils.isEmpty(endTimeStr)) {
                int second =(int)(DateHelperUtils.string2DateTime(endTimeStr).getTime()
                        - DateHelperUtils.string2DateTime(startTimeStr).getTime()) / 1000;

                time = DateHelperUtils.formatTimeByHMS(second);
            }

            recordViewHolder.tvTime.setText(time);

            Boolean isUpload = record.getIsUpload();
            recordViewHolder.ivStatus.setImageResource(isUpload?R.drawable.ic_flight_record_mission_is_upload
                    :R.drawable.ic_flight_record_mission_is_not_upload);

            double latitude=record.getLatitude();
            String latitudeStr= CoordConvertManager.convertToSexagesimalFenUnit(latitude);
            double longitude=record.getLongitude();
            String longitudeStr=CoordConvertManager.convertToSexagesimalFenUnit(longitude);

            recordViewHolder.tvLocation.setText(longitudeStr+"/"+latitudeStr);

            if (mShowFunctionMenuPosition == position) {
                recordViewHolder.rlFunction.setVisibility(View.VISIBLE);
                recordViewHolder.rlFunction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mShowFunctionMenuPosition = -1;
                        notifyDataSetChanged();
                    }
                });
            } else {
                recordViewHolder.rlFunction.setVisibility(View.GONE);
            }

            recordViewHolder.btnRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mOnClickFunctionMenu != null) {
                        mOnClickFunctionMenu.onClickRead(position);
                    }
                }
            });

            recordViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mOnClickFunctionMenu != null) {
                        mOnClickFunctionMenu.onClickDelete(position);
                    }
                }
            });

            recordViewHolder.btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClickFunctionMenu != null) {
                        mOnClickFunctionMenu.onClickUpload(position);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
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

    /**
     * 显示功能菜单
     *
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

    private class RecordViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvMiles;
        private TextView tvLocation;
        private TextView tvTime;
        private TextView tvMaxHeight;
        private ImageView ivStatus;

        private LinearLayout rlFunction;
        private Button btnRead;
        private Button btnDelete;
        private Button btnUpload;

        RecordViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvMiles = (TextView) itemView.findViewById(R.id.tv_miles);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvMaxHeight = (TextView) itemView.findViewById(R.id.tv_max_height);
            ivStatus = (ImageView) itemView.findViewById(R.id.iv_status);

            rlFunction = (LinearLayout) itemView.findViewById(R.id.rl_function);
            btnRead = (Button) itemView.findViewById(R.id.btn_see);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            btnUpload = (Button) itemView.findViewById(R.id.btn_upload);

        }
    }

}
