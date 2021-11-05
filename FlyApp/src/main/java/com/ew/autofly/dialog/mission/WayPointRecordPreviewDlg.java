package com.ew.autofly.dialog.mission;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.db.entity.FinePatrolWayPointDetail;
import com.ew.autofly.db.entity.PhotoPosition;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.mission.MissionPointType;
import com.ew.autofly.event.AddWayPointEvent;
import com.ew.autofly.model.teaching.WayPointTeachingDataModel;
import com.ew.autofly.widgets.business.AirlineModelWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

import de.mrapp.android.dialog.MaterialDialog;


public class WayPointRecordPreviewDlg extends BaseDialogFragment implements View.OnClickListener {

    private final static String DIALOG_BUILDER = "DIALOG_BUILDER";

    private boolean isAbsoluteAltitude;

    private LocationCoordinate mPointCloudBaseLocation;
    private String mPointCloudPath;
    private List<FinePatrolWayPointDetail> mWayPointDetails;

    private Context mContext;
    private int mCheckPosition = 0;

    private AirlineModelWidget mTowerModelWidget;

    private ExpandableListView mListDetailElv;
    private DetailExpandableAdapter mDetailExpandableAdapter;

    private OnWayPointEditListener mOnWayPointEditListener;

    public void setOnWayPointEditListener(OnWayPointEditListener onWayPointEditListener) {
        mOnWayPointEditListener = onWayPointEditListener;
    }

    public static WayPointRecordPreviewDlg newInstance(Builder builder) {

        Bundle args = new Bundle();
        args.putSerializable(DIALOG_BUILDER, builder);
        WayPointRecordPreviewDlg fragment = new WayPointRecordPreviewDlg();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TransparentWithoutDimDialogTheme);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_waypoint_record_preview, null, false);
        initView(view);
        return view;
    }

    @Override
    protected void initTitleBar(View rootView) {
        super.initTitleBar(rootView);
        setTitle("航线编辑");
    }

    @Override
    protected void onCreateSize() {
        setSize(1.0f, 1.0f);
    }

    private void initData() {
        mContext = getContext();

        Builder builder = (Builder) getArguments().getSerializable(DIALOG_BUILDER);
        mPointCloudBaseLocation = builder.mPointCloudBaseLocation;
        mPointCloudPath = builder.mPointCloudPath;
        mWayPointDetails = builder.mWayPointDetails;
        isAbsoluteAltitude = builder.isAbsoluteAltitude;
    }

    private void initView(View view) {

        mTowerModelWidget = view.findViewById(R.id.cp_tower_cloud_point);
        mListDetailElv = (ExpandableListView) view.findViewById(R.id.elv_list_detail);

        view.findViewById(R.id.tv_insert).setOnClickListener(this);
        view.findViewById(R.id.tv_delete).setOnClickListener(this);

        mDetailExpandableAdapter = new DetailExpandableAdapter();
        mListDetailElv.setAdapter(mDetailExpandableAdapter);
        mListDetailElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mListDetailElv.isGroupExpanded(groupPosition)) {
                    mListDetailElv.collapseGroup(groupPosition);
                } else {
                    mListDetailElv.expandGroup(groupPosition, true);
                }
                return true;
            }
        });


        mDetailExpandableAdapter.setOnCheckListener(new OnCheckListener() {
            @Override
            public void onCheck(int position) {
                checkWaypoint(position);

            }
        });

        AirlineModelWidget.Param param = new AirlineModelWidget.Param()
                .modelPointCloudPath(mPointCloudPath)
                .wayPointDetails(mWayPointDetails)
                .modelBaseLocationCoordinate(mPointCloudBaseLocation)
                .isShowAircraft(true)
                .build();
        mTowerModelWidget.show(param);
    }

    int checkedItem;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_insert:

                checkedItem = 1;

                CharSequence[] source = {"前", "后"};

                MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext, R.style.TransparentDialogTheme);
                builder.setMessage("当前航点为" + (mCheckPosition + 1) + ",请选择新航点插入位置");
                builder.setMessageColor(getResources().getColor(R.color.black));
                builder.setItemColor(getResources().getColor(R.color.black));
                builder.setSingleChoiceItems(source, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancle), null);
                builder.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnWayPointEditListener != null) {
                            mOnWayPointEditListener.insert(mCheckPosition + checkedItem);
                        }
                    }
                });
                builder.create().show();

                break;
            case R.id.tv_delete:
                CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                deleteDialog.setTitle(getResources().getString(R.string.notice))
                        .setMessage("删除航点后不可恢复，是否删除？")
                        .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteWaypoint();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancle), null)
                        .create()
                        .show();

                break;
        }
    }

    private void checkWaypoint(int position) {

        if (!mTowerModelWidget.isLoaded()) {
            showToast("正在加载三维图形");
            return;
        }
        mCheckPosition = position;
        mTowerModelWidget.checkWaypoint(mCheckPosition);
        mDetailExpandableAdapter.notifyDataSetChanged();
    }

    private void addWaypoint(int position) {

        if (!mTowerModelWidget.isLoaded()) {
            showToast("正在加载三维图形");
            return;
        }
        mCheckPosition = position;
        mTowerModelWidget.addWaypoint(mCheckPosition);
        mDetailExpandableAdapter.notifyDataSetChanged();
    }

    
    private void deleteWaypoint() {

        if (!mTowerModelWidget.isLoaded()) {
            showToast("正在加载三维图形");
            return;
        }

        if (mWayPointDetails.size() < 2) {
            showToast("航点不能少于一个");
            return;
        }

        mWayPointDetails.remove(mCheckPosition);

        mTowerModelWidget.deleteWaypoint(mCheckPosition);

        mCheckPosition = 0;
        mDetailExpandableAdapter.notifyDataSetChanged();

    }


    private class DetailExpandableAdapter extends BaseExpandableListAdapter {

        private OnCheckListener mOnCheckListener;

        public void setOnCheckListener(OnCheckListener onCheckListener) {
            mOnCheckListener = onCheckListener;

        }

        @Override
        public int getGroupCount() {
            return mWayPointDetails.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mWayPointDetails.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mWayPointDetails.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
          
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
          
          
            View v;
            if (convertView == null) {
                v = newGroupView(parent);
            } else {
                v = convertView;
            }
            bindGroupView(v, groupPosition, mWayPointDetails.get(groupPosition), isExpanded);
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
          
            View v;
            if (convertView == null) {
                v = newChildView(parent);
            } else {
                v = convertView;
            }
            bindChildView(v, mWayPointDetails.get(groupPosition));
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
          
            return true;
        }


        /**
         * 创建新的组视图
         *
         * @param parent
         * @return
         */
        public View newGroupView(ViewGroup parent) {
            return LayoutInflater.from(mContext).inflate(R.layout.item_dialog_finpatrol_preview_list_parent, parent, false);
        }

        /**
         * 创建新的子视图
         *
         * @param parent
         * @return
         */
        public View newChildView(ViewGroup parent) {
            return LayoutInflater.from(mContext).inflate(R.layout.item_dialog_finpatrol_preview_list_child, parent, false);
        }

        /**
         * 绑定组数据
         *
         * @param view
         * @param data
         * @param isExpanded
         */
        private void bindGroupView(View view, final int position, FinePatrolWayPointDetail data, boolean isExpanded) {
            ImageView ivCheck = view.findViewById(R.id.iv_check);
            TextView tv_point_desc = view.findViewById(R.id.tv_point_desc);

            boolean isCheck = mCheckPosition == position;
            ivCheck.setActivated(isCheck);
            view.setBackgroundColor(mContext.getResources().getColor(isCheck ?
                    R.color.menu_item_selected : R.color.menu_item_normal));

            int wayPointType = data.getWaypointType();
            tv_point_desc.setText(position + 1 + ":" + (wayPointType == MissionPointType.SHOT_PHOTO.value() ? "拍照点" : "辅助点"));
            ImageView iv_tip = (ImageView) view.findViewById(R.id.iv_tip);
            if (isExpanded) {
                iv_tip.setImageResource(R.drawable.ic_finepatrol_expandable_list_down);
            } else {
                iv_tip.setImageResource(R.drawable.ic_finepatrol_expandable_list_right);
            }

            ivCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCheckPosition = position;
                    notifyDataSetChanged();

                    if (mOnCheckListener != null) {
                        mOnCheckListener.onCheck(position);
                    }
                }
            });
        }


    
        private void bindChildView(View view, FinePatrolWayPointDetail data) {
            TextView tv = (TextView) view;
            tv.setText(WayPointTeachingDataModel.getWaypointDetailInfo(data,isAbsoluteAltitude));
        }

    }

    public interface OnCheckListener {
        void onCheck(int position);
    }

    public static final class Builder implements Serializable {

        private boolean isAbsoluteAltitude = true;
        private LocationCoordinate mPointCloudBaseLocation;
        private String mPointCloudPath;
        private List<FinePatrolWayPointDetail> mWayPointDetails;

        public Builder() {
        }

        public Builder isAbsoluteAltitude(boolean val) {
            isAbsoluteAltitude = val;
            return this;
        }

        public Builder mPointCloudBaseLocation(LocationCoordinate val) {
            mPointCloudBaseLocation = val;
            return this;
        }

        public Builder mPointCloudPath(String val) {
            mPointCloudPath = val;
            return this;
        }

        public Builder mWayPointDetails(List<FinePatrolWayPointDetail> val) {
            mWayPointDetails = val;
            return this;
        }


        public WayPointRecordPreviewDlg build() {
            return WayPointRecordPreviewDlg.newInstance(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddWayPointEvent(AddWayPointEvent event) {

        if (mWayPointDetails != null && !mWayPointDetails.isEmpty()) {
            if (event.getAction() == AddWayPointEvent.ACTION_ADD) {
                addWaypoint(mWayPointDetails.size() - 1);
            } else if (event.getAction() == AddWayPointEvent.ACTION_INSERT) {
                int position = (int) event.getParam();
                addWaypoint(position);
            }
        }
    }

    public interface OnWayPointEditListener {

        void insert(int position);
    }
}
