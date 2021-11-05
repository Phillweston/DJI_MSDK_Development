package com.ew.autofly.dialog.mission;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.entity.airroute.AirRoute;
import com.ew.autofly.model.teaching.WayPointTeachingDataModel;
import com.ew.autofly.model.teaching.WayPointTeachingFactory;
import com.ew.autofly.utils.IOUtils;
import com.flycloud.autofly.base.util.DensityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class WayPointTeachingMissionDialog extends AbstractLoadMissionDialog {

    public static final String TAG_LOAD_MISSION = "TAG_LOAD_MISSION";

    private String screenShotPath = "";

    private WayPointTeachingDataModel wayPointTeachingDataModel;

    @Override
    public Option setOptions() {

        Option.Builder builder = new Option.Builder();
        return builder.enableLoadSingle(false)
                .buttonAllText("选择全部")
                .buttonLoadText("载入")
                .build();
    }

    @Override
    protected void initBottomMenu(ViewGroup left, ViewGroup right) {
        super.initBottomMenu(left, right);

        TextView mTvNew = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.btn_dialog_bottom_menu_buttton, right, false);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) mTvNew.getLayoutParams();
        layoutParams1.setMarginEnd(DensityUtils.dip2px(getContext(), 10));
        mTvNew.setLayoutParams(layoutParams1);
        mTvNew.setText("新建");
        mTvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mOnBottomMenuClickListener != null) {
                    mOnBottomMenuClickListener.onNew();
                }
            }
        });
        right.addView(mTvNew, 0);


        TextView mTvDelete = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.btn_dialog_bottom_menu_buttton, right, false);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) mTvDelete.getLayoutParams();
        layoutParams2.setMarginEnd(DensityUtils.dip2px(getContext(), 10));
        mTvDelete.setLayoutParams(layoutParams2);
        mTvDelete.setText(getString(R.string.delete));
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> selectedIdList = getSelectedIdList();
                if (selectedIdList == null || selectedIdList.isEmpty()) {
                    showToast(getString(R.string.tips_no_delete_items));
                    return;
                }
                CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                deleteDialog.setTitle(getActivity().getString(R.string.notice))
                        .setMessage(getActivity().getString(R.string.delete_mission_notice))
                        .setPositiveButton(getActivity().getString(R.string.sure),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        for (String missionName : selectedIdList) {

                                            wayPointTeachingDataModel.deleteAirRoute(missionName);

                                            IOUtils.delete(getScreenShotPath(missionName));
                                        }
                                        notifyDataChange(loadMission());

                                    }
                                }).setNegativeButton(getActivity().getString(R.string.cancle),
                        null)
                        .create()
                        .show();
            }
        });
        right.addView(mTvDelete, 0);
    }

    private String getScreenShotPath(String missionName) {
        return AppConstant.ROOT_PATH + File.separator
                + screenShotPath + File.separator + missionName.replace(WayPointTeachingDataModel
                .DATA_SUFFIX, "") + ".webp";
    }

    @Override
    protected List<MissionData> loadMission() {

        WayPointTeachingFactory.Type type = WayPointTeachingFactory.Type.NONE;











        wayPointTeachingDataModel = WayPointTeachingFactory.createDataModel(type);

        LinkedHashMap<String, AirRoute> allAirRoute = wayPointTeachingDataModel.getAllAirRoute();
        List<MissionData> missionDataList = new ArrayList<>();

        for (Map.Entry<String, AirRoute> entry : allAirRoute.entrySet()) {
            MissionData missionData = new MissionData();

            String name = entry.getKey();
            missionData.setId(name);
            missionData.setSnapshot(getScreenShotPath(name));
            String stringBuilder = "任务名称: " + name.replace(WayPointTeachingDataModel.DATA_SUFFIX, "") + "\n" +
                    "创建时间: " + entry.getValue().getCreatedTime() + "\n";
            missionData.setInfo(stringBuilder);
            missionDataList.add(missionData);
        }

        return missionDataList;
    }

    public interface OnBottomMenuClickListener {

        void onNew();
    }

    private OnBottomMenuClickListener mOnBottomMenuClickListener;

    public void setOnBottomMenuClickListener(OnBottomMenuClickListener onBottomMenuClickListener) {
        mOnBottomMenuClickListener = onBottomMenuClickListener;
    }
}