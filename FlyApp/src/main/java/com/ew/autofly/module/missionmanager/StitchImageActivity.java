package com.ew.autofly.module.missionmanager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.activities.BaseActivity;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.module.missionmanager.dialog.ButtonProgressDialog;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.service.StitchMonitorService;
import com.ew.autofly.net.api.BoxApi;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.ew.autofly.xflyer.utils.ThreadPoolUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;



public class StitchImageActivity extends BaseActivity implements View.OnClickListener {

    public static final String PARAMS_STICHIMAGE_MISSION = "PARAMS_STICHIMAGE_MISSION";

    private final int HANDLER_SHOW_TOAST = 1;
    private final int HANDLER_START_DOWNLOAD = 2;

    private DataBaseUtils mDB = null;

    private MissionBase mMission;

    private int missionStatus = 0;

    private TextView mTvCopyImages, mTvStitchImages, mTvDownload;
    private ProgressDialog mCheckDialog;
    private ProgressDialog mDownloadDialog;
    private ButtonProgressDialog mButtonProgressDialog;

    private String mDownloadFileUrl = "";
    private String mDownloadFileSavePath = "";
    private DownloadTask mDownloadImageTask = null;

    private Timer mMonitorTimer;

    private StitchMonitorService monitorService;

    private boolean isReceiveStepBroadcast=true;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mBroadcastReceiver;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SHOW_TOAST:
                    ToastUtil.show(StitchImageActivity.this, (String) msg.obj);
                    break;
                case HANDLER_START_DOWNLOAD:
                    if (mDownloadImageTask != null && mDownloadImageTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mDownloadImageTask.cancel(true);
                    }
                    mDownloadImageTask = new DownloadTask();
                    mDownloadImageTask.execute(mDownloadFileUrl, mDownloadFileSavePath + ".tmp");
                    break;
            }
            return false;
        }
    });


    private MyServiceConnection mServiceConnection;

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            monitorService = ((StitchMonitorService.MyBinder) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stitch_image);
        initData();
        initView();
    }

    private void initData() {
        mMission = (MissionBase) getIntent().getExtras().getSerializable(PARAMS_STICHIMAGE_MISSION);
        if (mMission != null) {
            missionStatus = mMission.getStatus();
            mDownloadFileUrl = "http://" + AppConstant.BOX_IP + ":81/mbtiles/"
                    + mMission.getMissionId() + ".mbtiles";
            mDownloadFileSavePath = IOUtils.getRootStoragePath(this) + AppConstant.DIR_MAP + File.separator
                    + mMission.getName() + ".mbtiles";
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SHOW_TOAST,
                    getString(R.string.mission_operate_error)));
            finish();
            return;
        }

        try {
            mDB = DataBaseUtils.getInstance(getApplicationContext());
        } catch (Exception e) {
            ToastUtil.show(this, "数据库初始化错误,系统即将退出");
            finish();
        }
    }

    private void initView() {
        mTvCopyImages = (TextView) findViewById(R.id.tv_copy_image);
        mTvStitchImages = (TextView) findViewById(R.id.tv_start_stitch);
        mTvDownload = (TextView) findViewById(R.id.tv_download_result);

        mTvCopyImages.setOnClickListener(this);
        mTvStitchImages.setOnClickListener(this);
        mTvDownload.setOnClickListener(this);

        findViewById(R.id.imgBtnBack).setOnClickListener(this);
        findViewById(R.id.btn_shutdown).setOnClickListener(this);
        findViewById(R.id.btn_restart).setOnClickListener(this);

        initBroadcast();

        initService();
    }

    private void initService() {
        mServiceConnection = new MyServiceConnection();
        Intent intent = new Intent(StitchImageActivity.this, StitchMonitorService.class);
        intent.putExtra(PARAMS_STICHIMAGE_MISSION, mMission);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void initBroadcast() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (monitorService != null
                        && monitorService.getMission() != null
                        && monitorService.getMission().getId().equals(mMission.getMissionId())
                        &&isReceiveStepBroadcast) {

                    String mosaicStep = intent.getStringExtra("stepMsg");


                    if(mosaicStep.contains("DEBUG")){
                        return;
                    }

                    String message="";
                    try {
                        message=mosaicStep.substring(7);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    showStartNotice(message);
                    showServiceBtn();

                    if (mosaicStep.contains("拼接完成")) {

                        showStopNotice(getString(R.string.mission_operate_notice_stitch_success), getString(R.string.sure),null);
                        updateState(Mission2.STATE_STITCH_PHOTO_FINISHED);

                    }  else if (mosaicStep.contains("找不到照片文件夹")) {

                        showStopNotice("找不到照片文件夹,请重新拷贝图像" ,getString(R.string.sure),null);
                        updateState(Mission2.STATE_EXECUTE_FINISHED);

                    }else if (mosaicStep.contains("失败")||mosaicStep.contains("ERROR")) {

                        showStopNotice("发生错误："+message,"停止拼接", new ButtonProgressDialog.OnClickButton() {
                            @Override
                            public void onClickButton() {
                                stopStitchPhoto();
                            }
                        });
                        updateState(Mission2.STATE_COPY_PHOTO_FINISHED);

                    }else{

                        if (!mosaicStep.contains("上传")) {
                            showProgressNotice(message);
                        }
                    }
                }
            }
        };
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(AppConstant.BROADCAST_STITCH_MONITOR));
    }


    private class CopyMonitorTask extends TimerTask {
        @Override
        public void run() {

            final String mosaicStep = BoxApi.getMosaicStep(mMission.getMissionId());

            if (!mosaicStep.isEmpty() && !mosaicStep.equals("0")) {

                if (missionStatus == Mission2.STATE_COPYING_PHOTO) {

                    if (mosaicStep.contains("成功")) {

                        showStopNotice(getString(R.string.mission_operate_notice_copy_success), getString(R.string.sure),null);
                        updateState(Mission2.STATE_COPY_PHOTO_FINISHED);

                    } else if (mosaicStep.contains("失败")||mosaicStep.contains("ERROR")) {

                        String message="";
                        try {
                            message=mosaicStep.substring(7);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        showStopNotice(message, "停止拼接", new ButtonProgressDialog.OnClickButton() {
                            @Override
                            public void onClickButton() {
                                stopStitchPhoto();
                            }
                        });

                        updateState(Mission2.STATE_EXECUTE_FINISHED);
                    }
                }

            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_copy_image:
                copyPhotoFromUSB();
                break;
            case R.id.tv_start_stitch:
                startStitchPhoto();
                break;
            case R.id.tv_download_result:
                downloadResultFile();
                break;
            case R.id.btn_shutdown:
                shutdownBox();
                break;
            case R.id.btn_restart:
                restartBox();
                break;
            case R.id.imgBtnBack:
                finish();
                break;
        }
    }

    private void shutdownBox() {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (checkBoxConnect()) {
                    showStartNotice("正在发送关机命令");
                    boolean result = BoxApi.shutdownBox();
                    if (result) {
                        showStopNotice("发送关机命令成功",getString(R.string.sure),null);
                    } else {
                        showStopNotice("发送关机命令失败",getString(R.string.sure),null);
                    }
                }
            }
        });
    }

    private void restartBox() {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (checkBoxConnect()) {
                    showStartNotice("正在发送重启命令");
                    boolean result = BoxApi.restartBox();
                    if (result) {
                        showStopNotice("发送关机重启成功",getString(R.string.sure),null);
                    } else {
                        showStopNotice("发送关机重启失败",getString(R.string.sure),null);
                    }
                }
            }
        });
    }


    private void copyPhotoFromUSB() {

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (checkBoxConnect()) {

                    showStartNotice(getString(R.string.mission_operate_notice_copying));

                    if (!BoxApi.checkUSBConnection()) {
                        showStopNotice(getString(R.string.mission_operate_notice_insert_sdcard),getString(R.string.sure),null);
                        return;
                    }

                    String beginTime = DateHelperUtils.format(
                            mMission.getStartTime(), "yyyyMMddHHmmss");
                    String endTime = "";
                    if (mMission.getEndTime() != null) {
                        endTime = DateHelperUtils.format(
                                mMission.getEndTime(), "yyyyMMddHHmmss");
                    }

                    boolean blnResult = BoxApi.copyPhotoFromUSB(
                            mMission.getMissionId(), beginTime, endTime);

                    if (blnResult) {
                        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SHOW_TOAST,
                                getString(R.string.mission_operate_notice_copy_start_success)));

                        missionStatus = Mission2.STATE_COPYING_PHOTO;
                        destroyMonitorTimer();
                        mMonitorTimer = new Timer();
                        CopyMonitorTask task = new CopyMonitorTask();
                        mMonitorTimer.schedule(task, 5000, 2000);

                    } else {
                        missionStatus = Mission2.STATE_EXECUTE_FINISHED;

                        showStopNotice(getString(R.string.mission_operate_notice_copy_start_fail),getString(R.string.sure),null);
                    }
                }
            }
        });

    }


    private void startStitchPhoto() {

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (checkBoxConnect()) {

                    showStartNotice(getString(R.string.mission_operate_notice_stitch_starting));


                    if (BoxApi.checkFileIfExist(mDownloadFileUrl)) {
                        showStopNotice(getString(R.string.mission_operate_notice_stitch_already_success),getString(R.string.sure),null);
                        return;
                    }



                    boolean stopResult = true;

                    if (stopResult) {
                        boolean startResult = BoxApi.startMosaic(mMission.getMissionId());
                        if (startResult) {
                            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SHOW_TOAST,
                                    getString(R.string.mission_operate_notice_stitch_start_success)));

                            missionStatus = Mission2.STATE_STITCHING_PHOTO;

                            showProgressNotice(getString(R.string.mission_operate_notice_stitching));
                            showServiceBtn();
                            isReceiveStepBroadcast=true;

                            destroyMonitorTimer();
                            startService(new Intent(StitchImageActivity.this, StitchMonitorService.class));

                        } else {
                            showStopNotice(getString(R.string.mission_operate_notice_stitch_start_fail),getString(R.string.sure),null);
                        }
                    } else {
                        showStopNotice(getString(R.string.mission_operate_notice_stitch_start_fail),getString(R.string.sure),null);
                    }

                }
            }
        });

    }


    private void stopStitchPhoto() {

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (checkBoxConnect()) {

                    hideNoticeDialog();
                    showStartNotice(getString(R.string.mission_operate_notice_stitch_stopping));
                    isReceiveStepBroadcast=false;

                    boolean stopResult = getStopResult();

                    if (stopResult) {
                        showStopNotice(getString(R.string.mission_operate_notice_stitch_stop_success),getString(R.string.sure),null);
                    } else {
                        showStopNotice(getString(R.string.mission_operate_notice_stitch_stop_fail),getString(R.string.sure),null);
                    }

                }
            }
        });
    }

    private boolean getStopResult() {

        boolean stopResult = BoxApi.stopMosaic(mMission.getMissionId());
        if (!stopResult) {
            return false;
        }

        destroyMonitorTimer();

        if (monitorService == null) {
            return false;
        }

        boolean flag=true;

        long t1 = System.currentTimeMillis();


        while (!monitorService.isStopStitch()) {
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 60 * 1000) {
              flag=false;
            }
        }

        monitorService.stopStitchMonitor();

        return flag;
    }

    /**
     * 检查盒子是否连接
     *
     * @return
     */
    private boolean checkBoxConnect() {

        if (monitorService != null
                && monitorService.getMission() != null
                && !monitorService.getMission().getId().isEmpty()
                && !monitorService.getMission().getId().equals(mMission.getMissionId())) {
            mHandler.sendMessage(mHandler.obtainMessage(
                    HANDLER_SHOW_TOAST, getString(R.string.mission_operate_notice_has_other_mission)
                            + monitorService.getMission().getName()));
            return false;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCheckDialog = ProgressDialog.show(StitchImageActivity.this, "", getString(R.string.mission_operate_notice_checking_box_connect));
            }
        });

        boolean bConnectWifi = BoxApi.checkBoxConnect();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCheckDialog != null && mCheckDialog.isShowing()) {
                    mCheckDialog.dismiss();
                }
            }
        });
        if (!bConnectWifi) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHandler.sendMessage(mHandler.obtainMessage(
                            HANDLER_SHOW_TOAST, getString(R.string.mission_operate_notice_reconnect_box)));
                    Intent intent = new Intent();
                    intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                    intent.putExtra("extra_prefs_show_button_bar", true);
                    intent.putExtra("extra_prefs_set_next_text", getString(R.string.done));
                    intent.putExtra("extra_prefs_set_back_text", getString(R.string.back));
                    intent.putExtra("wifi_enable_next_on_connect", true);
                    startActivity(intent);
                }
            });
        }
        return bConnectWifi;
    }


    private void downloadResultFile() {

        File file = new File(mDownloadFileSavePath);
        if (file.exists()) {
            CustomDialog.Builder deleteDialog = new CustomDialog.Builder(this);
            deleteDialog.setTitle(getString(R.string.notice))
                    .setMessage("文件已下载，是否重新下载")
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadImageFromBox();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancle), null)
                    .create()
                    .show();
        } else {
            downloadImageFromBox();
        }
    }


    private void downloadImageFromBox() {

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (checkBoxConnect()) {
                    mHandler.sendMessage(mHandler.obtainMessage(HANDLER_START_DOWNLOAD));

                }
            }
        });

    }

    private class DownloadTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            String urlPath = params[0];
            if (!BoxApi.checkFileIfExist(urlPath)) {
                return 0;
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlPath);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return -1;
                }

                int fileLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                String tempPath = params[1];
                outputStream = new FileOutputStream(params[1]);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {

                    total += count;

                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    outputStream.write(data, 0, count);
                }

                IOUtils.renameFile(tempPath, tempPath.replace(".tmp", ""));

            } catch (Exception e) {
                return -1;
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createDownloadProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            setDownloadProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);

            hideDownloadProgressDialog();

            mButtonProgressDialog = new ButtonProgressDialog(StitchImageActivity.this);
            mButtonProgressDialog.setTitle("提示");
            if (code == 1) {
                mButtonProgressDialog.setMessage("下载完成");
                updateState(Mission2.STATE_DOWNLOAD_FINISHED);
            } else if (code == 0) {
                mButtonProgressDialog.setMessage("下载文件不存在,请重新拼接");
            } else {
                mButtonProgressDialog.setMessage("下载失败");
            }
            mButtonProgressDialog.show();
            mButtonProgressDialog.setCanceledOnTouchOutside(false);
            mButtonProgressDialog.showBtn(null,null);
        }
    }

    /**
     * 更新当前状态
     *
     * @param state
     */
    private void updateState(int state) {

        destroyMonitorTimer();

        missionStatus = state;
        mMission.setStatus(state);

    }

    private void destroyMonitorTimer() {
        if (mMonitorTimer != null) {
            mMonitorTimer.cancel();
            mMonitorTimer.purge();
            mMonitorTimer = null;
        }
    }

    @Override
    protected void onPause() {
        destroyMonitorTimer();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        destroyMonitorTimer();

        if (mDownloadImageTask != null && mDownloadImageTask.getStatus() == AsyncTask.Status.RUNNING) {
            mDownloadImageTask.cancel(true);
        }
        unbindService(mServiceConnection);
        mHandler.removeCallbacksAndMessages(null);
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void showStartNotice(final String message) {

        StitchImageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mButtonProgressDialog == null || !mButtonProgressDialog.isShowing()) {
                    mButtonProgressDialog = new ButtonProgressDialog(StitchImageActivity.this);
                    mButtonProgressDialog.setMessage(message);
                    mButtonProgressDialog.setTitle("提示");
                    mButtonProgressDialog.setCancelable(false);
                    mButtonProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            destroyMonitorTimer();
                        }
                    });
                    mButtonProgressDialog.show();
                }
            }
        });
    }

    private void showServiceBtn(){
        StitchImageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mButtonProgressDialog != null && mButtonProgressDialog.isShowing()) {
                    mButtonProgressDialog.showServiceBtn(new ButtonProgressDialog.OnClickService() {
                        @Override
                        public void enterService() {
                            finish();
                        }

                        @Override
                        public void stopStitch() {
                            stopStitchPhoto();
                        }
                    });
                }
            }
        });
    }

    private void showProgressNotice(final String message) {
        StitchImageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mButtonProgressDialog != null && mButtonProgressDialog.isShowing()) {
                    mButtonProgressDialog.setMessage(message);
                }
            }
        });
    }

    private void showStopNotice(final String message, final String btnStr,
                                final ButtonProgressDialog.OnClickButton onClickButton) {

        StitchImageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mButtonProgressDialog != null && mButtonProgressDialog.isShowing()) {
                    mButtonProgressDialog.setTitle("提示");
                    mButtonProgressDialog.setMessage(message);
                    mButtonProgressDialog.showBtn(btnStr,onClickButton);
                }
            }
        });
    }

    private void hideNoticeDialog() {
        StitchImageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mButtonProgressDialog != null && mButtonProgressDialog.isShowing()) {
                    mButtonProgressDialog.dismiss();
                }
            }
        });
    }

    private void createDownloadProgressDialog() {
        mDownloadDialog = new ProgressDialog(StitchImageActivity.this);
        mDownloadDialog.setTitle("下载成果文件进度");
        mDownloadDialog.setIcon(android.R.drawable.ic_dialog_info);
        mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.setIndeterminate(false);
        mDownloadDialog.setMax(100);
        mDownloadDialog.show();
    }

    private void setDownloadProgress(int progress) {
        if (mDownloadDialog != null) {
            mDownloadDialog.setProgress(progress);
        }
    }

    private void hideDownloadProgressDialog() {
        if (null != mDownloadDialog && mDownloadDialog.isShowing()) {
            mDownloadDialog.dismiss();
        }
    }

}
