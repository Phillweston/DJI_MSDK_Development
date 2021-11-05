package com.ew.autofly.dialog.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.PhotoViewDialogFragment;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.fragments.BaseFragment;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.StringUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



public class LocalMissionFragment extends BaseFragment implements View.OnClickListener {

    private final String DATABASECHANGE = "DATABASECHANGE";

    private String workMode;

    private ArrayList<String> selectedIdList = new ArrayList<>();

    private DataBaseUtils mDB = null;

    private List<MissionBatch2> missionBatchList = new ArrayList<>();

    private ProgressBar mProgressBar;

    private ListViewAdapter mAdapter;

    private ListView mListView;

    private boolean isSelectedAll = false;

    private OnItemLoadClickListener onItemLoadClickListener;

    private OnFragmentDismissListener onFragmentDismissListener;

    private OnItemReviewClickListener onItemReviewClickListener;

    public static LocalMissionFragment newInstance(Bundle args) {
        LocalMissionFragment fragment = new LocalMissionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DATABASECHANGE)) {
                if (mAdapter != null && missionBatchList != null) {
                    missionBatchList.add((MissionBatch2) intent.getSerializableExtra("missionbatch"));

                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workMode = getArguments().getString("mode");
        selectedIdList = getArguments().getStringArrayList("selectedIdList");
        try {
            mDB = DataBaseUtils.getInstance(getContext());
        } catch (Exception e) {

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DATABASECHANGE);
        getActivity().getApplicationContext().registerReceiver(receiver, filter);

        mAdapter = new ListViewAdapter();

        mListView.setAdapter(mAdapter);

        loadData();

    }

    public void loadData() {
        getLocalMission();
    }

    private void getLocalMission() {
        if (mDB == null)
            return;
        if (workMode == null)
            return;

        missionBatchList.clear();

        mDB.getAllMissionBatch2(workMode, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {
            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {
                if (succ && result != null) {
                    ArrayList<MissionBatch2> missionBatch2ArrayList = (ArrayList<MissionBatch2>) result;
                    missionBatchList.addAll(missionBatch2ArrayList);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });
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

    
    public void selectAll() {

        if (selectedIdList == null)
            return;
        if (mAdapter == null)
            return;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_mission, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_load_task);
        mListView = (ListView) view.findViewById(R.id.mListView);
        view.findViewById(R.id.tv_read).setOnClickListener(this);
        view.findViewById(R.id.tv_read_all).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_read:
                onItemReviewClickListener.onItemReviewClick();
                break;
            case R.id.tv_read_all:
                selectAll();
                break;
        }

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
            if (missionBatchList.size() == 0)
                return convertView;
            final MissionBatch2 missionBatch = missionBatchList.get(position);

            holder.mTvName.setText(missionBatch.getName());
            holder.mTvFlightHeight.setText(missionBatch.getAltitude() + "m");
            holder.mTvResolutionRatio.setText(new DecimalFormat("##.##").format((missionBatch.getResolutionRate())) + "cm");
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
                    if (onFragmentDismissListener != null) {
                        onFragmentDismissListener.OnFragmentDismiss();
                    }
                }
            });

            String path = missionBatch.isCloud() ? missionBatch.getSnapShot() : IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_MISSION_PHOTO + File.separator + missionBatch.getSnapShot();
            /*    final String path = IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_MISSION_PHOTO + "/" + missionBatch.getSnapShot();*/
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

    @Override
    public void onDestroy() {
        if (receiver != null) {
            getActivity().getApplicationContext().unregisterReceiver(receiver);
        }
        super.onDestroy();
    }



    public interface OnItemLoadClickListener {
        void onItemLoadClick(Mission2 mission);
    }

    public void setOnItemClickListener(OnItemLoadClickListener onItemLoadClickListener) {
        this.onItemLoadClickListener = onItemLoadClickListener;
    }

    public interface OnFragmentDismissListener {
        void OnFragmentDismiss();
    }

    public void setOnFragmentDismissListener(OnFragmentDismissListener onFragmentDismissListener) {
        this.onFragmentDismissListener = onFragmentDismissListener;
    }

    public interface OnItemReviewClickListener {
        void onItemReviewClick();
    }

    public void setOnItemReviewClickListener(OnItemReviewClickListener onItemReviewClickListener) {
        this.onItemReviewClickListener = onItemReviewClickListener;
    }

}
