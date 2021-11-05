package com.ew.autofly.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.StringUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoadTaskDialogFragment extends DialogFragment {
    private DataBaseUtils mDB = null;
    private List<MissionBatch2> missionBatchList = new ArrayList<>();
    private ProgressBar progress;
    private ListView mLvMissionBatch;
    private TextView mTvReadAll;
    private TextView mTvRead;
    private OnItemLoadClickListener onItemLoadClickListener;
    private OnItemReviewClickListener onItemReviewClickListener;
    private String workMode;

    private ListViewAdapter mAdapter;

    private ArrayList<String> selectedIdList = new ArrayList<>();
    private boolean isSelectedAll = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.7), (int) (dm.heightPixels * 0.9));
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.7), (int) (dm.widthPixels * 0.9));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_load_task, container, false);
        try {
            mDB = DataBaseUtils.getInstance(getContext());
        } catch (Exception e) {

        }
        bindField(view);
        initData();
        return view;
    }

    private void bindField(View view) {
        progress = (ProgressBar) view.findViewById(R.id.pb_load_task);
        mLvMissionBatch = (ListView) view.findViewById(R.id.lv_mission_batch);
        mTvReadAll = (TextView) view.findViewById(R.id.tv_read_all);
        mTvRead = (TextView) view.findViewById(R.id.tv_read);

        mTvReadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll();
            }
        });

        mTvRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemReviewClickListener != null) {
                    onItemReviewClickListener.onItemReviewClick(selectedIdList);
                }
                dismiss();
            }
        });

        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        workMode = getArguments().getString("mode");
        selectedIdList = getArguments().getStringArrayList("selectedIdList");

        mAdapter = new ListViewAdapter();

        mDB.getAllMissionBatch2(workMode, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {
            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {
                if (succ && result != null) {
                    missionBatchList = (ArrayList<MissionBatch2>) result;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLvMissionBatch.setAdapter(mAdapter);
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });
    }

    public interface OnItemLoadClickListener {
        void onItemLoadClick(Mission2 mission);
    }

    public void setOnItemClickListener(OnItemLoadClickListener onItemLoadClickListener) {
        this.onItemLoadClickListener = onItemLoadClickListener;
    }

    public interface OnItemReviewClickListener {
        void onItemReviewClick(ArrayList<String> selectIdList);
    }

    public void setOnItemReviewClickListener(OnItemReviewClickListener onItemReviewClickListener) {
        this.onItemReviewClickListener = onItemReviewClickListener;
    }

    class ListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return missionBatchList.size();
        }

        @Override
        public Object getItem(int position) {
            return missionBatchList == null || missionBatchList.size() == 0 ? null : missionBatchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_load_task, parent, false);
                holder = new ViewHolder();
                holder.mIvMission = (ImageView) convertView.findViewById(R.id.iv_mission);
                holder.mTvName = (TextView) convertView.findViewById(R.id.tv_mission_name);
                holder.mTvFlightHeight = (TextView) convertView.findViewById(R.id.tv_flight_height);
                holder.mTvResolutionRatio = (TextView) convertView.findViewById(R.id.tv_resolution_ratio);
                holder.mTvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
                holder.mTvLoad = (TextView) convertView.findViewById(R.id.tv_load);

                holder.mCbCheck = (CheckBox) convertView.findViewById(R.id.cb_check);
                holder.mTvBuffer = (TextView) convertView.findViewById(R.id.tv_buffer);
                holder.llbuffer = (LinearLayout) convertView.findViewById(R.id.ll_buffer);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final MissionBatch2 missionBatch = missionBatchList.get(position);

            holder.mTvName.setText(missionBatch.getName());
            holder.mTvFlightHeight.setText(missionBatch.getAltitude() + "m");
            holder.mTvResolutionRatio.setText(new DecimalFormat("##.##").format((missionBatch.getResolutionRate() * 100)) + "cm");
            holder.mTvCreateTime.setText(DateHelperUtils.format(missionBatch.getCreateDate(), "yyyy-MM-dd HH:mm"));
            int buffer = missionBatch.getBuffer();
            if (buffer != 0) {
                holder.llbuffer.setVisibility(View.VISIBLE);
                holder.mTvBuffer.setText(String.valueOf(buffer) + "m");
            } else {
                holder.llbuffer.setVisibility(View.GONE);
            }
            holder.mTvLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLoadClickListener != null) {
                        getMissionByBatchId(v.getId(), missionBatch.getId());
                    }
                    dismiss();
                }
            });

            String path = missionBatch.isCloud() ? missionBatch.getSnapShot() : IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_MISSION_PHOTO + File.separator + missionBatch.getSnapShot();

            /*final String path = IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_MISSION_PHOTO + "/" + missionBatch.getSnapShot();*/
            holder.mIvMission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoViewDialogFragment dlg = new PhotoViewDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("path", path);
                    dlg.setArguments(args);
                    dlg.show(getFragmentManager(), "photo_view");
                }
            });
            /*holder.mTvReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemReviewClickListener != null) {
                        getMissionByBatchId(v.getId(), missionBatch.getId());
                    }
                    dismiss();
                }
            });*/
            Glide.with(getContext()).load(path)
                    .placeholder(R.drawable.default_mission_photo)
                    .error(R.drawable.default_mission_photo).into(holder.mIvMission);

            final String id = missionBatch.getId();

            if (isSelect(id)) {
                holder.mCbCheck.setChecked(true);
                convertView.setBackgroundColor(getResources().getColor(
                        R.color.menu_item_selected));
            } else {
                holder.mCbCheck.setChecked(false);
                convertView.setBackgroundColor(getResources().getColor(
                        R.color.menu_item_normal));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelect(id)) {
                        selectedIdList.remove(id);
                    } else {
                        selectedIdList.add(id);
                    }

                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        private void getMissionByBatchId(final int viewId, String batchId) {
            mDB.getMissionListByBatchId(batchId, new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {

                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    List<Mission2> missionList = (List<Mission2>) result;
                    Mission2 mission = (missionList == null || missionList.size() == 0) ? null : missionList.get(0);
                    if (viewId == R.id.tv_load)
                        onItemLoadClickListener.onItemLoadClick(mission);
                   /* else if (viewId == R.id.tv_review)
                        onItemReviewClickListener.onItemLoadClick(mission);*/
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        }
    }

    class ViewHolder {
        private ImageView mIvMission;
        private TextView mTvName;
        private TextView mTvFlightHeight;
        private TextView mTvResolutionRatio;
        private TextView mTvCreateTime;
        private TextView mTvLoad;

        private CheckBox mCbCheck;
        private TextView mTvBuffer;
        private LinearLayout llbuffer;
    }

    /**
     * 判断是否已经选中任务
     *
     * @return
     */
    private boolean isSelect(String id) {

        if (StringUtils.isEmptyOrNull(id)) {
            return false;
        }

        for (String selectedId : selectedIdList) {
            if (id.equals(selectedId)) {
                return true;
            }
        }

        return false;
    }


    private void selectAll() {

        selectedIdList.clear();

        if (!isSelectedAll) {
            for (MissionBatch2 missionBatch : missionBatchList) {
                String id = missionBatch.getId();
                selectedIdList.add(id);
            }
        }

        isSelectedAll = !isSelectedAll;

        mAdapter.notifyDataSetChanged();
    }
}