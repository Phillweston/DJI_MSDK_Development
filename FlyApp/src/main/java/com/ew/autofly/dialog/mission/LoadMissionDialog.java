package com.ew.autofly.dialog.mission;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.helper.MissionHelper;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;

import java.util.ArrayList;
import java.util.List;



public class LoadMissionDialog extends AbstractLoadMissionDialog {

    private TextView mTvDelete;
    private TextView mTvExport;

    private LoadMissionOption mLoadMissionOption;


    @Override
    public void setOptions(Option option) {
        super.setOptions(option);
        mLoadMissionOption = (LoadMissionOption) option;
    }

    @Override
    protected List<MissionData> loadMission() {

        List<MissionData> missionDataList = new ArrayList<>();

        List<MissionBase> missionBaseList = MissionHelper.loadMissionList(getMissionType());
        if (missionBaseList != null) {

            for (MissionBase missionBase : missionBaseList) {
                MissionData missionData = new MissionData();

                missionData.setId(missionBase.getMissionId());
                missionData.setSnapshot(IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_MISSION_PHOTO + "/" + missionBase.getSnapshot());

                String stringBuilder = "任务名称: " + missionBase.getName() + "\n" +
                        "创建时间: " + DateHelperUtils.format(missionBase.getCreateDate(), "yyyy-MM-dd HH:mm") + "\n";
                missionData.setInfo(stringBuilder);
                missionDataList.add(missionData);
            }
        }

        return missionDataList;
        /*if(mMissionType== FlyCollectMode.TreeObstacle){
            if(missionBaseList!=null){

                for(MissionBase missionBase:missionBaseList){
                    MissionData missionData=new MissionData();

                    MissionTreeObstacle missionTreeObstacle= (MissionTreeObstacle) missionBase;
                    missionData.setId(missionTreeObstacle.getMissionId());
                    missionData.setSnapshot(missionTreeObstacle.getSnapshot());

                    String stringBuilder = "任务名称: " + missionTreeObstacle.getName() + "\n" +
                            "创建时间: " + DateHelperUtils.format(missionTreeObstacle.getCreateDate(), "yyyy-MM-dd HH:mm") + "\n";
                    missionData.setInfo(stringBuilder);
                    missionDataList.add(missionData);
                }

                notifyDataChange(missionDataList);
            }
        }*/


    }

    @Override
    protected void initBottomMenu(ViewGroup left, ViewGroup right) {
        super.initBottomMenu(left, right);

        mTvExport = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.btn_dialog_bottom_menu_buttton, right, false);
        LinearLayout.LayoutParams exportLayoutParams = (LinearLayout.LayoutParams) mTvExport.getLayoutParams();
        exportLayoutParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.dialog_bottom_menu_button_margin));
        mTvExport.setText(getString(R.string.export));
        mTvExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (mLoadMissionOption != null && mLoadMissionOption.enableDelete) {
            right.addView(mTvExport, 0);
        }

        mTvDelete = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.btn_dialog_bottom_menu_buttton, right, false);
        LinearLayout.LayoutParams deleteLayoutParams = (LinearLayout.LayoutParams) mTvDelete.getLayoutParams();
        deleteLayoutParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.dialog_bottom_menu_button_margin));
        mTvDelete.setText(getString(R.string.delete));
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                        deleteMissions(selectedIdList);
                                        clearSelected();
                                        notifyDataChange(loadMission());

                                    }
                                }).setNegativeButton(getActivity().getString(R.string.cancle),
                        null)
                        .create()
                        .show();
            }
        });

        if (mLoadMissionOption != null && mLoadMissionOption.enableExport) {
            right.addView(mTvDelete, 0);
        }
    }

    private void deleteMissions(List<String> selectedIdList) {
        MissionHelper.deleteMissions(getMissionType(), selectedIdList);
    }

    public static class LoadMissionOption extends Option {

        
        private boolean enableDelete;

        
        private boolean enableExport;

        protected LoadMissionOption(LoadMissionOption.Builder builder) {
            super(builder);
            enableDelete = builder.enableDelete;
            enableExport = builder.enableExport;
        }

        public static final class Builder extends Option.Builder {
            private boolean enableDelete = false;
            private boolean enableExport = false;

            public Builder() {
            }

            public Builder enableDelete(boolean val) {
                enableDelete = val;
                return this;
            }

            public Builder enableExport(boolean val) {
                enableExport = val;
                return this;
            }

            public LoadMissionOption build() {
                return new LoadMissionOption(this);
            }
        }
    }
}
