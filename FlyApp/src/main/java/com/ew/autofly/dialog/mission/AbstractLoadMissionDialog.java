package com.ew.autofly.dialog.mission;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.ew.autofly.R;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.dialog.PhotoViewDialogFragment;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;



public abstract class AbstractLoadMissionDialog extends BaseDialogFragment {

    public static final String TAG_LOAD_MISSION = "TAG_LOAD_MISSION";
    
    public static final String PARAMS_LOAD_MISSION_TYPE = "PARAMS_LOAD_MISSION_TYPE";
    public static final String PARAMS_LOAD_MISSION_SELECTED_LIST = "PARAMS_LOAD_MISSION_SELECTED_LIST";

    private ProgressBar progress;
    private ListView mLvMissionBatch;
    private TextView mTvReadAll;
    private TextView mTvRead;

    private boolean isSelectedAll = false;

    private Option mOption;

    
    private int mMissionType;
    private LinearLayout mLeftMenusBottom;
    private LinearLayout mRightMenusBottom;

    public int getMissionType() {
        return mMissionType;
    }

    private List<String> selectedIdList = new ArrayList<>();

    protected List<String> getSelectedIdList() {
        return selectedIdList;
    }

    private List<MissionData> mMissionData = new ArrayList<>();

    private ListViewAdapter mAdapter;

    private OnSingleItemLoadClickListener mOnSingleItemLoadClickListener;

    public interface OnSingleItemLoadClickListener {
        void onItemLoadClick(String missionId);
    }

    private OnMultipleItemLoadClickListener mOnMultipleItemLoadClickListener;

    public interface OnMultipleItemLoadClickListener {
        void onItemLoadClick(List<String> selectIdList);
    }

    public void setOnSingleItemLoadClickListener(OnSingleItemLoadClickListener onSingleItemLoadClickListener) {
        this.mOnSingleItemLoadClickListener = onSingleItemLoadClickListener;
    }

    public void setOnMultipleItemLoadClickListener(OnMultipleItemLoadClickListener onMultipleItemLoadClickListener) {
        this.mOnMultipleItemLoadClickListener = onMultipleItemLoadClickListener;
    }

    /**
     * 设置参数
     *
     * @return {@link Option}
     */
    public Option setOptions() {
        return null;
    }

    /**
     * 设置参数
     *
     * @param option {@link Option}
     */
    public void setOptions(Option option) {
        this.mOption = option;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.mOption == null) {
            this.mOption = setOptions();
        }
    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f, 0.9f);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_load_task_abstract, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initData() {
        mMissionType = getArguments().getInt(PARAMS_LOAD_MISSION_TYPE);
        selectedIdList = getArguments().getStringArrayList(PARAMS_LOAD_MISSION_SELECTED_LIST);
    }

    private void initView(View view) {
        mLeftMenusBottom = (LinearLayout) view.findViewById(R.id.bottom_left_menus);
        mRightMenusBottom = (LinearLayout) view.findViewById(R.id.bottom_right_menus);
        progress = (ProgressBar) view.findViewById(R.id.pb_load_task);
        progress.setVisibility(View.GONE);
        mLvMissionBatch = (ListView) view.findViewById(R.id.lv_mission_batch);

        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mAdapter = new ListViewAdapter();
        mLvMissionBatch.setAdapter(mAdapter);

        showLoadProgressDialog(null);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataChange(loadMission());
            }
        });

        initBottomMenu(mLeftMenusBottom, mRightMenusBottom);

        initOption();
    }

    protected void initBottomMenu(ViewGroup left, ViewGroup right) {
        mTvReadAll = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.btn_dialog_bottom_menu_buttton_read_all, left, false);
        mTvReadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll();
            }
        });
        left.addView(mTvReadAll);

        mTvRead = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.btn_dialog_bottom_menu_buttton, right, false);
        mTvRead.setText(getString(R.string.read));
        mTvRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnMultipleItemLoadClickListener != null) {
                    sortSelectId();
                    if (selectedIdList == null || selectedIdList.isEmpty()) {
                        showToast(getString(R.string.tips_no_delete_items));
                        return;
                    }
                    mOnMultipleItemLoadClickListener.onItemLoadClick(selectedIdList);
                }
                dismiss();
            }
        });

        right.addView(mTvRead);
    }

    private void initOption() {
        if (this.mOption != null) {
            if (!TextUtils.isEmpty(mOption.buttonAllText) && mTvReadAll != null) {
                mTvReadAll.setText(mOption.buttonAllText);
            }

            if (!TextUtils.isEmpty(mOption.buttonLoadText) && mTvRead != null) {
                mTvRead.setText(mOption.buttonLoadText);
            }
        }
    }

    protected abstract List<MissionData> loadMission();

    protected void notifyDataChange(List<MissionData> missionData) {
        dismissLoadProgressDialog();
        if (missionData != null) {
            this.mMissionData = missionData;
        } else {
            this.mMissionData = new ArrayList<>();
        }
        mAdapter.notifyDataSetChanged();
    }

    class ListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mMissionData.size();
        }

        @Override
        public Object getItem(int position) {
            return mMissionData == null || mMissionData.size() == 0 ? null : mMissionData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_load_task_s, parent, false);
                holder = new ViewHolder();
                holder.mIvMissionSnapshot = (ImageView) convertView.findViewById(R.id.iv_mission_snapshot);
                holder.mTvMissionInfo = (TextView) convertView.findViewById(R.id.tv_mission_info);
                holder.mCbCheck = (CheckBox) convertView.findViewById(R.id.cb_check);
                holder.mTvLoad = (TextView) convertView.findViewById(R.id.tv_load);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final MissionData missionData = mMissionData.get(position);

            holder.mTvMissionInfo.setText(missionData.getInfo());

            if (mOption != null && !mOption.enableLoadSingle) {
                holder.mTvLoad.setVisibility(View.GONE);
            } else {
                holder.mTvLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnSingleItemLoadClickListener != null) {
                            mOnSingleItemLoadClickListener.onItemLoadClick(missionData.getId());
                        }
                        dismiss();
                    }
                });
            }

            final String path = missionData.getSnapshot();
            holder.mIvMissionSnapshot.setOnClickListener(new View.OnClickListener() {
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
                    .error(R.drawable.default_mission_photo)
                    .into(holder.mIvMissionSnapshot);

            final String id = missionData.getId();

            boolean isSelected = isSelect(id);
            holder.mCbCheck.setChecked(isSelected);
            convertView.setBackgroundColor(getResources().getColor(isSelected ?
                    R.color.menu_item_selected : R.color.menu_item_normal));

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

        clearSelected();

        if (!isSelectedAll) {
            for (MissionData missionData : mMissionData) {
                String id = missionData.getId();
                selectedIdList.add(id);
            }
        }

        isSelectedAll = !isSelectedAll;

        mAdapter.notifyDataSetChanged();
    }

    protected void clearSelected() {
        selectedIdList.clear();
    }

    
    private void sortSelectId() {
        List<String> idList = new ArrayList<>();
        for (MissionData missionDatum : mMissionData) {
            for (String s : selectedIdList) {
                if (s.equals(missionDatum.getId())) {
                    idList.add(s);
                }
            }
        }
        selectedIdList = idList;
    }

    class ViewHolder {
        private ImageView mIvMissionSnapshot;
        private TextView mTvMissionInfo;
        private TextView mTvLoad;
        private CheckBox mCbCheck;
    }

    public class MissionData {

        private String id;
        private String snapshot;
        private String info;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSnapshot() {
            return snapshot;
        }

        public void setSnapshot(String snapshot) {
            this.snapshot = snapshot;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public static class Option {

        
        private boolean enableLoadSingle;

        private String buttonAllText;
        private String buttonLoadText;

        protected Option(Builder builder) {
            enableLoadSingle = builder.enableLoadSingle;
            buttonAllText = builder.buttonAllText;
            buttonLoadText = builder.buttonLoadText;
        }

        public static class Builder {
            private boolean enableLoadSingle = true;
            private String buttonAllText;
            private String buttonLoadText;

            public Builder() {
            }

            public Builder enableLoadSingle(boolean val) {
                enableLoadSingle = val;
                return this;
            }

            public Builder buttonAllText(String val) {
                buttonAllText = val;
                return this;
            }

            public Builder buttonLoadText(String val) {
                buttonLoadText = val;
                return this;
            }

            public Option build() {
                return new Option(this);
            }
        }
    }

}
