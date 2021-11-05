package com.ew.autofly.module.missionmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.activities.BaseActivity;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.helper.MissionHelper;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.module.missionmanager.adapter.MissionAdapter;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.MissionUtil;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.ew.autofly.module.missionmanager.MissionPhotoPreviewActivity.PARAMS_PREVIEW_MISSION;
import static com.ew.autofly.module.missionmanager.StitchImageActivity.PARAMS_STICHIMAGE_MISSION;


public class MissionOperateActivity extends BaseActivity implements OnClickListener {

    public static final String PARAMS_MISSION_ID = "PARAMS_MISSION_ID";
    public static final String PARAMS_MISSION_TYPE = "PARAMS_MISSION_TYPE";

    private DataBaseUtils mDB = null;
    private SharedConfig config;
    public int currentProgress = 0;
    private TextView mTvPreview, mTvUploadImages, mTvStitchImages, mTvDeleteMission;

    private ListView mLvMission;


    private String mMissionId = null;
    private String mBatchId = null;
    private int mMissionType;
    public boolean blnFinishUpload = false;
    public boolean bFinishSync = false;
    public boolean bSupplyResult = false;

    public int iSyncSuccess = 0;
    public int iSyncFailed = 0;

    private Timer mMonitorTimer;

    private MissionAdapter missionAdapter;
    private List<MissionBase> mMissionList = new ArrayList<>();
    private MissionBase mSelectedMission;

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    /*public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MissionImage Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/

    private void bindField() {
        findViewById(R.id.imgBtnBack).setOnClickListener(this);

        mLvMission = (ListView) findViewById(R.id.lv_mission);
        missionAdapter = new MissionAdapter(getApplication(), mMissionList);
        mLvMission.setAdapter(missionAdapter);
        mLvMission.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedMission = missionAdapter.getSelectedMission(position);

                mTvDeleteMission.setEnabled(!(mSelectedMission == null));
                mTvPreview.setEnabled(!(mSelectedMission == null));

                if (mSelectedMission != null) {
                    mTvStitchImages.setEnabled(isCanStitchImage());
                } else {
                    mTvStitchImages.setEnabled(false);
                }

            }
        });

        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        mTvUploadImages = (TextView) findViewById(R.id.tv_upload_media);
        mTvStitchImages = (TextView) findViewById(R.id.tv_mosaic_images);
        mTvDeleteMission = (TextView) findViewById(R.id.tv_delete_mission);

        mTvPreview.setEnabled(false);
        mTvUploadImages.setEnabled(false);
        mTvStitchImages.setEnabled(false);
        mTvDeleteMission.setEnabled(false);
        mTvPreview.setOnClickListener(this);
        mTvUploadImages.setOnClickListener(this);
        mTvStitchImages.setOnClickListener(this);
        mTvDeleteMission.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new SharedConfig(this);
        setContentView(R.layout.activity_mission_operate);
        bindField();

        Intent intent = getIntent();

        mBatchId = intent.getStringExtra(PARAMS_MISSION_ID);
        mMissionType = intent.getIntExtra(PARAMS_MISSION_TYPE, -1);

        try {
            mDB = DataBaseUtils.getInstance(getApplicationContext());
        } catch (Exception e) {
            ToastUtil.show(this, "数据库初始化错误,系统即将退出");
            finish();
            return;
        }
        initData();


    }

    private void initData() {

        if (MissionUtil.checkIfNewMissionDB(config.getMode())) {
            List<MissionBase> missionBaseList = MissionHelper.loadMissionListByBatchId(mMissionType, mBatchId);
            missionAdapter.updateData(missionBaseList);
        } else {
            mDB.getMissionListByBatchId(mBatchId, new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {

                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {

                    final List<Mission2> missionList = (List<Mission2>) result;
                    final List<MissionBase> missionBaseList = new ArrayList<>();
                    MissionOperateActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            missionBaseList.addAll(missionList);
                            missionAdapter.updateData(missionBaseList);
                        }
                    });
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnBack:
                finish();
                break;
            case R.id.tv_preview:
                if (mSelectedMission != null) {
                    Intent intent = new Intent(this, MissionPhotoPreviewActivity.class);
                    intent.putExtra(PARAMS_PREVIEW_MISSION, mSelectedMission);
                    startActivity(intent);
                }
                break;
            case R.id.tv_upload_media:

                break;
            case R.id.tv_mosaic_images:
                if (mSelectedMission != null) {

                    if (isCanStitchImage()) {
                        if (mSelectedMission.getStartTime() == null
                                || mSelectedMission.getEndTime() == null
                                || mSelectedMission.getStatus() == 0) {
                            ToastUtil.show(MissionOperateActivity.this, "任务未完成");
                        } else {
                            Intent intent = new Intent(this, StitchImageActivity.class);
                            intent.putExtra(PARAMS_STICHIMAGE_MISSION, mSelectedMission);
                            startActivity(intent);
                        }
                    }
                }
                break;
            case R.id.tv_delete_mission:
                deleteMission();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    
    private void deleteMission() {
        if (mSelectedMission != null) {

            final String id = mSelectedMission.getMissionId();

            CustomDialog.Builder deleteDialog = new CustomDialog.Builder(MissionOperateActivity.this);
            deleteDialog.setTitle(getString(R.string.notice))
                    .setMessage(getString(R.string.default_delete_notice))
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (MissionUtil.checkIfNewMissionDB(config.getMode())) {
                                MissionHelper.deleteMission(mMissionType, id);
                                mMissionList.remove(mSelectedMission);
                                missionAdapter.updateData(mMissionList);
                                mSelectedMission = null;
                                if (mMissionList.size() == 0) {
                                    mTvDeleteMission.setEnabled(false);
                                }
                                ToastUtil.show(MissionOperateActivity.this, "删除成功");
                            } else {


                                mDB.deleteMissionById(id, new DataBaseUtils.onExecResult() {

                                    @Override
                                    public void execResult(final boolean succ, String errStr) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if (succ) {
                                                    try {
                                                        mMissionList.remove(mSelectedMission);
                                                        missionAdapter.updateData(mMissionList);
                                                        mSelectedMission = null;
                                                        if (mMissionList.size() == 0) {
                                                            mTvDeleteMission.setEnabled(false);
                                                        }
                                                        ToastUtil.show(MissionOperateActivity.this, "删除成功");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    ToastUtil.show(MissionOperateActivity.this, "删除失败");
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void execResultWithResult(boolean succ, Object result, String errStr) {

                                    }

                                    @Override
                                    public void setExecCount(int i, int count) {

                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();

        } else {
            ToastUtil.show(MissionOperateActivity.this, "请选择要删除的任务");
        }
    }

    /**
     * 是否可以拼接图像
     *
     * @return
     */
    private boolean isCanStitchImage() {
        return true;
    }

}