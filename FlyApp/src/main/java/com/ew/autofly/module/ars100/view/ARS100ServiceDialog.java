package com.ew.autofly.module.ars100.view;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.dialog.EditMissionNameDialogFragment;
import com.ew.autofly.module.ars100.ARS100Service;
import com.ew.autofly.module.ars100.request.request.control.ConnectSensorRequest;
import com.ew.autofly.module.ars100.request.request.control.StartPointCloudRequest;
import com.ew.autofly.module.ars100.request.request.control.StartPosRequest;
import com.ew.autofly.module.ars100.request.request.control.StopPointCloudRequest;
import com.ew.autofly.module.ars100.request.request.control.StopPosRequest;
import com.ew.autofly.module.ars100.request.request.monitor.WorkStatusRequest;
import com.ew.autofly.module.ars100.response.ResponseID;
import com.ew.autofly.module.ars100.response.ResponseImpl;
import com.ew.autofly.module.ars100.response.result.control.BaseControlResult;
import com.ew.autofly.module.ars100.response.result.monitor.PosDataResult;
import com.ew.autofly.module.ars100.response.result.monitor.WorkStatusResult;
import com.ew.autofly.module.ars100.response.result.service.ConnectServiceResult;
import com.flycloud.autofly.base.util.DateHelperUtils;

import static com.ew.autofly.module.ars100.ARS100Service.SOCKET_ERROR;



public class ARS100ServiceDialog extends BaseDialogFragment
        implements View.OnClickListener, ARS100Service.ResponseListener, ARS100Service.SocketStatusListener {

    private ImageButton mTvCancel;

    private Button mBtnArsConnect;

    private Button mBtnArsStartPosCollect;

    private Button mBtnArsStartPointcloudCollect;

    private Button mBtnArsStopPointcloudCollect;

    private Button mBtnArsStopPosCollect;

    private TextView mTvArsStatus;

    private TextView mTvArsDiskRemain;

    private TextView mTvArsImuStatus;

    private TextView mTvArsGnssStatus;

    private TextView mTvArsPosTime;


    private TextView mTvArsPointcloudTime;

    private ARS100Service mARS100Service;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ars100, container, false);

        initView(view);
        return view;
    }

    @Override
    protected void onCreateSize() {
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout((int) getResources().getDimension(R.dimen.ars_100_dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initView(View view) {
        mTvCancel = (ImageButton) view.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mBtnArsConnect = (Button) view.findViewById(R.id.btn_ars_connect);
        mBtnArsConnect.setOnClickListener(this);
        mBtnArsStartPosCollect = (Button) view.findViewById(R.id.btn_ars_start_pos_collect);
        mBtnArsStartPosCollect.setOnClickListener(this);
        mBtnArsStartPointcloudCollect = (Button) view.findViewById(R.id.btn_ars_start_pointcloud_collect);
        mBtnArsStartPointcloudCollect.setOnClickListener(this);
        mBtnArsStopPointcloudCollect = (Button) view.findViewById(R.id.btn_ars_stop_pointcloud_collect);
        mBtnArsStopPointcloudCollect.setOnClickListener(this);
        mBtnArsStopPosCollect = (Button) view.findViewById(R.id.btn_ars_stop_pos_collect);
        mBtnArsStopPosCollect.setOnClickListener(this);
        mTvArsStatus = (TextView) view.findViewById(R.id.tv_ars_status);
        mTvArsDiskRemain = (TextView) view.findViewById(R.id.tv_ars_disk_remain);
        mTvArsPosTime = (TextView) view.findViewById(R.id.tv_ars_pos_time);
        mTvArsPointcloudTime = (TextView) view.findViewById(R.id.tv_ars_pointcloud_time);
        mTvArsStatus.setOnClickListener(this);
        mTvArsDiskRemain.setOnClickListener(this);
        mTvArsPosTime.setOnClickListener(this);
        mTvArsPointcloudTime.setOnClickListener(this);

        if (mARS100Service.isConnect()) {
            mBtnArsConnect.setText("断开连接");
            mBtnArsConnect.setEnabled(true);
        }else {
            mBtnArsConnect.setText("连接服务");
            mBtnArsConnect.setEnabled(true);
        }

        mBtnArsStartPosCollect.setEnabled(false);
        mBtnArsStartPointcloudCollect.setEnabled(false);
        mBtnArsStopPointcloudCollect.setEnabled(false);
        mBtnArsStopPosCollect.setEnabled(false);

        mTvArsImuStatus = (TextView) view.findViewById(R.id.tv_ars_imu_status);
        mTvArsGnssStatus = (TextView) view.findViewById(R.id.tv_ars_gnss_status);

        this.mARS100Service.setSocketStatusListener(this);
        this.mARS100Service.setResponseListener(this);
    }

    /**
     * 绑定服务
     *
     * @param service
     */
    public void bindService(ARS100Service service) {
        this.mARS100Service = service;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_cancel:
              
                dismiss();
                break;
            case R.id.btn_ars_connect:

                if (mARS100Service != null) {
                    if ("连接服务".equals(mBtnArsConnect.getText())) {
                        mBtnArsConnect.setEnabled(false);
                        mARS100Service.connect();
                        showLoadProgressDialog("正在连接，请稍候……");
                    } else {
                        mARS100Service.disConnect();
                        resetConnectStatus();
                    }
                }
                break;
            case R.id.btn_ars_start_pos_collect:
                if (mARS100Service != null) {
                    mARS100Service.sendOrder(new StartPosRequest().getData());
                }
                break;
            case R.id.btn_ars_start_pointcloud_collect:
                EditMissionNameDialogFragment editMissionNameDialogFragment = new EditMissionNameDialogFragment();
                editMissionNameDialogFragment.show(getActivity().getFragmentManager(), "输入工程名称");
                editMissionNameDialogFragment.setOnSureButtonClickListener(new EditMissionNameDialogFragment.onSureButtonClickListener() {
                    @Override
                    public void sure(final String name) {
                        if (mARS100Service != null) {
                            mARS100Service.sendOrder(new StartPointCloudRequest(name).getData());
                        }
                    }
                });
                break;
            case R.id.btn_ars_stop_pointcloud_collect:
                if (mARS100Service != null) {
                    mARS100Service.sendOrder(new StopPointCloudRequest().getData());
                }
                break;
            case R.id.btn_ars_stop_pos_collect:
                if (mARS100Service != null) {
                    mARS100Service.sendOrder(new StopPosRequest().getData());
                }
                break;
        }
    }

    @Override
    public void onResponse(ResponseImpl response) {
        switch (response.getResponseId()) {
            case ResponseID.SERVICE_CONNECT:
                ConnectServiceResult connectServiceResult = (ConnectServiceResult) response.getResult();
                if (connectServiceResult.isSuccess()) {
                    mBtnArsConnect.setText("断开连接");
                    mBtnArsConnect.setEnabled(true);
                    showToast("连接服务成功");
                    if (mARS100Service != null) {
                      
                        mARS100Service.sendOrder(new WorkStatusRequest().getData());
                      
                        mARS100Service.sendBeatData();
                    }
                    if (mServiceListener != null) {
                        mServiceListener.onStatus(SERVICE_CONNECT);
                    }
                } else {
                    showToast("连接服务失败，请检查是否有其他客户端正在连接");
                    if (mARS100Service != null) {
                        mARS100Service.disConnect();
                    }
                    resetConnectStatus();
                }
                dismissLoadProgressDialog();
                break;
            case ResponseID.CONTROL_CONNECT_SENSOR:
                BaseControlResult baseControlResult = (BaseControlResult) response.getResult();
                if (baseControlResult.isSuccess()) {
                    showToast("连接传感器成功");
                } else {
                    showToast("连接传感器失败");
                }
                break;
           /* case ResponseID.CONTROL_START_POS:
                BaseControlResult startPosResult = (BaseControlResult) response.getResult();
                if (!startPosResult.isSuccess()) {
                    showToast("开始点云采集失败：code:" + startPosResult.getCmdResult()
                            + " message:" + startPosResult.getMessage());
                }
                break;*/
            case ResponseID.PUSH_AUTHORITY:
              
              
                break;
            case ResponseID.MONITOR_WORK_STATUS:
                WorkStatusResult getWorkStatusResult = (WorkStatusResult) response.getResult();
                if (getWorkStatusResult.getWorkStatus() == WorkStatusResult.WORK_STATUS_NO_CONNECT) {
                  
                    if (mARS100Service != null) {
                        mARS100Service.sendOrder(new ConnectSensorRequest().getData());
                    }
                }
                setWorkStatusInfo(getWorkStatusResult);
                break;
            case ResponseID.PUSH_WORK_STATUS:
                WorkStatusResult workStatusResult = (WorkStatusResult) response.getResult();
                setWorkStatusInfo(workStatusResult);
                break;
            case ResponseID.PUSH_POS_DATA:
            case ResponseID.MONITOR_POS_DATA:
                PosDataResult posDataResult = (PosDataResult) response.getResult();
                setPosDataInfo(posDataResult);
                break;
        }
    }

    /*@Override
    public void onDismiss(DialogInterface dialog) {
      
    }

    public void show() {
        if (getDialog() != null) {
            getDialog().show();
        }
    }

    public void hide() {
        if (getDialog() != null) {
            getDialog().hide();
        }
    }*/

    private void setWorkStatusInfo(WorkStatusResult workStatusResult) {
        changeControlStatus(workStatusResult.getWorkStatus());
        mTvArsStatus.setText(workStatusResult.getWorkStatusMessage());
        mTvArsDiskRemain.setText(String.format("%.2f", workStatusResult.getDiskRemainSize() / 1024 / 1024 / 1024.0f) + "G");
        mTvArsPosTime.setText(DateHelperUtils.formatTimeByHMS00(workStatusResult.getPosTime()));
        mTvArsPointcloudTime.setText(DateHelperUtils.formatTimeByHMS00(workStatusResult.getPointCloudTime()));

    }

    private void setPosDataInfo(PosDataResult posDataResult) {
        mTvArsImuStatus.setText(posDataResult.getIMUStatusMessage());
        mTvArsGnssStatus.setText(posDataResult.getGNSSStatusMessage());
    }

    private void changeControlStatus(int workStatus) {
        switch (workStatus) {
            case WorkStatusResult.WORK_STATUS_NO_CONNECT:
                mBtnArsStartPosCollect.setEnabled(false);
                mBtnArsStartPointcloudCollect.setEnabled(false);
                mBtnArsStopPointcloudCollect.setEnabled(false);
                mBtnArsStopPosCollect.setEnabled(false);
                break;
            case WorkStatusResult.WORK_STATUS_FREE:
                mBtnArsStartPosCollect.setEnabled(true);
                mBtnArsStartPointcloudCollect.setEnabled(false);
                mBtnArsStopPointcloudCollect.setEnabled(false);
                mBtnArsStopPosCollect.setEnabled(false);
                break;
            case WorkStatusResult.WORK_STATUS_POS:
                mBtnArsStartPosCollect.setEnabled(false);
                mBtnArsStartPointcloudCollect.setEnabled(true);
                mBtnArsStopPointcloudCollect.setEnabled(false);
                mBtnArsStopPosCollect.setEnabled(true);
                break;
            case WorkStatusResult.WORK_STATUS_POINTCLOUD:
                mBtnArsStartPosCollect.setEnabled(false);
                mBtnArsStartPointcloudCollect.setEnabled(false);
                mBtnArsStopPointcloudCollect.setEnabled(true);
                mBtnArsStopPosCollect.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void resetConnectStatus() {

        mBtnArsConnect.setText("连接服务");
        mBtnArsConnect.setEnabled(true);
        mBtnArsStartPosCollect.setEnabled(false);
        mBtnArsStartPointcloudCollect.setEnabled(false);
        mBtnArsStopPointcloudCollect.setEnabled(false);
        mBtnArsStopPosCollect.setEnabled(false);
        mTvArsStatus.setText("未连接");
        mTvArsDiskRemain.setText("N/A");
        mTvArsImuStatus.setText("N/A");
        mTvArsGnssStatus.setText("N/A");
        mTvArsPosTime.setText("00:00:00");
        mTvArsPointcloudTime.setText("00:00:00");

        if (mServiceListener != null) {
            mServiceListener.onStatus(SERVICE_DISCONNECT);
        }

        dismissLoadProgressDialog();
    }

    @Override
    public void onStatus(int status) {
        switch (status) {
            case SOCKET_ERROR:
                resetConnectStatus();
                break;
        }
    }

    public final static int SERVICE_CONNECT = 0;
    public final static int SERVICE_DISCONNECT = 1;

    private ServiceListener mServiceListener;

    public void setServiceListener(ServiceListener serviceListener) {
        mServiceListener = serviceListener;
    }

    public interface ServiceListener {

        void onStatus(int status);
    }
}
