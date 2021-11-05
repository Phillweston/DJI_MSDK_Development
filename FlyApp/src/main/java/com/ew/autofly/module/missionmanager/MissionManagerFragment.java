package com.ew.autofly.module.missionmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.ew.autofly.R;
import com.ew.autofly.adapter.decoration.GridSpacingItemDecoration;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.db.entity.MissionBatch;
import com.ew.autofly.db.helper.MissionHelper;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.EditMissionNameDialogFragment;
import com.ew.autofly.dialog.tool.BluetoothScanDialog;
import com.ew.autofly.entity.CloudMissionUpload;
import com.ew.autofly.entity.ExportMission;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.fragments.BaseFragment;
import com.ew.autofly.interfaces.OnRecyclerViewItemClickListener;
import com.ew.autofly.interfaces.OnTopLeftMenuClickListener;
import com.ew.autofly.module.missionmanager.adapter.MissionBatchGridAdapter;
import com.ew.autofly.module.missionmanager.service.MissionBluetoothService;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.MissionUtil;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.flycloud.autofly.base.util.DensityUtils;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.google.gson.Gson;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.ew.autofly.module.missionmanager.MissionOperateActivity.PARAMS_MISSION_ID;
import static com.ew.autofly.module.missionmanager.MissionOperateActivity.PARAMS_MISSION_TYPE;


public class MissionManagerFragment extends BaseFragment implements View.OnClickListener {

    private Context mContext;

    private DataBaseUtils mDB = null;

    private List<MissionBatch> mMissionBatchList = new ArrayList<>();
  
    private List<MissionBatch2> mMissionBatch2List = new ArrayList<>();
    private String mSendMissionBatchId;

    private RecyclerView mMissionRV;
    private GridLayoutManager mGridLayoutManager;
    private MissionBatchGridAdapter mMissionBatchGridAdapter;
    private TextView mTvNoData;

    private int mMissionGridColumn = 2;

    private SharedConfig config;

    private BluetoothAdapter mBluetooth;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private BluetoothScanDialog mBluetoothScanDlg;
    private BaseProgressDialog mProgressDlg;

    private final int OPEN_SEND_BLUETOOTH = 1;
    private final int OPEN_RECEIVE_BLUETOOTH = 2;

    private MissionBluetoothService mBluetoothService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        config = new SharedConfig(mContext);
        try {
            mDB = DataBaseUtils.getInstance(getActivity().getApplicationContext());
        } catch (Exception e) {
            ToastUtil.show(getActivity(), "数据库初始化错误,系统即将退出");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mission_manager, null);
        initView(rootView);
        initData();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View rootView) {
        ImageView leftMenu = (ImageView) rootView.findViewById(R.id.iv_menu);
        /*if (AppConstant.CHANNEL_ENWEI)
            leftMenu.setImageResource(R.drawable.logo_enwei);
        else if (AppConstant.CHANNEL_JIXUN)
            leftMenu.setImageResource(R.drawable.dingxin_top_menu);*/
        leftMenu.setOnClickListener(this);
      
        rootView.findViewById(R.id.iv_bluetooth_receive).setOnClickListener(this);
        TextView title = (TextView) rootView.findViewById(R.id.tv_title);

        mTvNoData = (TextView) rootView.findViewById(R.id.tv_mission_nodata);
        mMissionRV = (RecyclerView) rootView.findViewById(R.id.rv_mission);
        mGridLayoutManager = new GridLayoutManager(getActivity(), mMissionGridColumn, LinearLayoutManager.VERTICAL, false);
        mMissionRV.setLayoutManager(mGridLayoutManager);
        mMissionRV.addItemDecoration(new GridSpacingItemDecoration(mMissionGridColumn, DensityUtils.dip2px(getActivity(), 16), false));
        mMissionBatchGridAdapter = new MissionBatchGridAdapter(getContext(), mMissionBatchList);
        mMissionRV.setAdapter(mMissionBatchGridAdapter);

        mMissionBatchGridAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = mMissionRV.getChildAdapterPosition(view);
                mMissionBatchGridAdapter.openFunctionMenu(position);
            }

            @Override
            public void onItemLongClick(View view) {
            }
        });

        mMissionBatchGridAdapter.setonClickFunctionMenu(new MissionBatchGridAdapter.onClickFunctionMenu() {
            @Override
            public void onClickRead(int position) {
                mMissionBatchGridAdapter.closeFunctionMenu();
                Intent intent = new Intent(getActivity(), MissionOperateActivity.class);
                Bundle args = new Bundle();
                args.putString(PARAMS_MISSION_ID, mMissionBatchList.get(position).getMissionBatchId());
                args.putInt(PARAMS_MISSION_TYPE, mMissionBatchList.get(position).getMissionType());
                intent.putExtras(args);
                startActivity(intent);
            }

            @Override
            public void onClickDelete(final int position) {
                CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                deleteDialog.setTitle(getActivity().getString(R.string.notice))
                        .setMessage(getActivity().getString(R.string.delete_mission_notice))
                        .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMissionBatchGridAdapter.closeFunctionMenu();
                                final String missionBatchId = mMissionBatchList.get(position).getMissionBatchId();
                                if (MissionUtil.checkIfNewMissionDB(config.getMode())) {
                                    MissionHelper.deleteMissionBatch(missionBatchId);
                                    notifyDataSetRemoved(position);
                                    ToastUtil.show(getActivity(), getActivity().getString(R.string.default_delete_success));
                                } else {
                                    mDB.deleteMissionBatchById(missionBatchId, new DataBaseUtils.onExecResult() {
                                        @Override
                                        public void execResult(final boolean succ, String errStr) {
                                            mDB.deleteMissionByBatchId(missionBatchId, null);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (succ) {
                                                        try {
                                                            notifyDataSetRemoved(position);
                                                            ToastUtil.show(getActivity(), getActivity().getString(R.string.default_delete_success));
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        ToastUtil.show(getActivity(), getActivity().getString(R.string.default_delete_fail));
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
                        .setNegativeButton(getActivity().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onClickEdit(int position) {
                mMissionBatchGridAdapter.closeFunctionMenu();
                final MissionBatch missionBatch = mMissionBatchList.get(position);
                EditMissionNameDialogFragment editMissionNameDialogFragment = new EditMissionNameDialogFragment();
                editMissionNameDialogFragment.show(getActivity().getFragmentManager(), "修改任务名称");
                editMissionNameDialogFragment.setOnSureButtonClickListener(new EditMissionNameDialogFragment.onSureButtonClickListener() {
                    @Override
                    public void sure(final String content) {

                        if (MissionUtil.checkIfNewMissionDB(config.getMode())) {
                            MissionHelper.updateMissionBathName(missionBatch.getMissionBatchId(), content);
                            ToastUtil.show(getActivity(), "修改成功");
                            missionBatch.setName(content);
                            mMissionBatchGridAdapter.notifyDataSetChanged();
                        } else {
                            mDB.updateMissionBatchName(missionBatch.getMissionBatchId(), content, new DataBaseUtils.onExecResult() {
                                @Override
                                public void execResult(final boolean succ, final String errStr) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (succ) {
                                                ToastUtil.show(getActivity(), "修改成功");
                                                missionBatch.setName(content);
                                                mMissionBatchGridAdapter.notifyDataSetChanged();

                                            } else {
                                                ToastUtil.show(getActivity(), "修改失败");
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
                });
            }


            @Override
            public void onClickBluetooth(int position) {
                mMissionBatchGridAdapter.closeFunctionMenu();

                if (MissionUtil.checkIfNewMissionDB(config.getMode())) {
                    ToastUtil.show(getContext(), "此任务不支持蓝牙发送");
                    return;
                }

                if (!checkBluetoothAvailable()) {
                    return;
                }
                if (!mBluetooth.isEnabled()) {
                  
                    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enabler, OPEN_SEND_BLUETOOTH);
                  
                  
                } else {
                    mSendMissionBatchId = mMissionBatchList.get(position).getMissionBatchId();
                    sendMission();
                }
            }
        });
    }

    private void initData() {
        loadData();
        registerBluetoothReceiver();
    }

    private void loadData() {

        String modeStr = getModeStr(config.getMode());

        int mode = config.getMode();
        if (MissionUtil.checkIfNewMissionDB(config.getMode())) {
            MissionHelper.loadMissionBathList(mode, new AsyncOperationListener() {
                @Override
                public void onAsyncOperationCompleted(AsyncOperation operation) {
                    if (operation != null && operation.isCompletedSucessfully()) {
                        List<MissionBatch> res = (List<MissionBatch>) operation.getResult();
                        if (res != null) {
                            notifyDataSetChanged(res);
                        }
                    }
                }
            });
        } else {
          
            mDB.getAllMissionBatch2(modeStr, new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {
                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    if (succ && result != null) {
                        mMissionBatch2List = (List<MissionBatch2>) result;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged(MissionUtil.convertToMissionBatchList(mMissionBatch2List));
                            }
                        });
                    }
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        }

    }

    private void notifyDataSetRemoved(int position) {
        mMissionBatchList.remove(position);
        mMissionBatchGridAdapter.notifyDataSetChanged();
        mTvNoData.setVisibility(mMissionBatchList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void notifyDataSetChanged(List<MissionBatch> missionBatchList) {
        this.mMissionBatchList.clear();
        this.mMissionBatchList.addAll(missionBatchList);
        mMissionBatchGridAdapter.notifyDataSetChanged();
        mTvNoData.setVisibility(mMissionBatchList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private String getModeStr(int mode) {

        String modeStr = "";
        switch (mode) {
            case FlyCollectMode.LinePatrol://线状巡视
                modeStr = "LinePatrol";
                break;
            case FlyCollectMode.TiltImage://倾斜影像
                modeStr = "TiltImage";
                break;
            case FlyCollectMode.Manual://手动
                modeStr = "Manual";
                break;
        }
        return modeStr;
    }

    private void setTitle(TextView mTvTitle) {
        switch (config.getMode()) {
            case FlyCollectMode.LinePatrol:
                mTvTitle.setText(getResources().getString(R.string.txt_line_patrol));
                break;
            case FlyCollectMode.TiltImage:
                mTvTitle.setText(getResources().getString(R.string.txt_fast_mapping_tilt));
                break;
            case FlyCollectMode.Manual:
                mTvTitle.setText(getResources().getString(R.string.txt_manual_collect));
                break;
        }
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
              
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) return;

                if (mBluetoothScanDlg != null) {
                  
                    if (scanDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        mDeviceList.add(scanDevice);
                        mBluetoothScanDlg.refreshDeviceList();
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
        }
    };

    private void registerBluetoothReceiver() {

        mBluetooth = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
      
        filter.addAction(BluetoothDevice.ACTION_FOUND);
      
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
      
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
      
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        mContext.registerReceiver(mBluetoothReceiver, filter);
    }

    @Override
    public void onDestroy() {
        if (mBluetoothReceiver != null)
            mContext.unregisterReceiver(mBluetoothReceiver);
        if (mBluetoothService != null)
            mBluetoothService.stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                ((OnTopLeftMenuClickListener) getActivity()).onMenuClick(v);
                break;
            case R.id.iv_bluetooth_receive:
                if (!checkBluetoothAvailable()) {
                    return;
                }
                if (!mBluetooth.isEnabled()) {
                  
                    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enabler, OPEN_RECEIVE_BLUETOOTH);
                  
                  
                } else {
                    receiveMission();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_SEND_BLUETOOTH:
                if (resultCode == -1) {
                    sendMission();
                }
            case OPEN_RECEIVE_BLUETOOTH:
                if (resultCode == -1) {
                    receiveMission();
                }
                break;
        }
    }

    
    private void receiveMission() {

        if (mBluetoothService == null) {
            mBluetoothService = new MissionBluetoothService(mHandler);
        }

        if (mBluetoothService.getState() == MissionBluetoothService.STATE_NONE) {
            showScanBluetoothDialog();
        } else if (mBluetoothService.getState() == MissionBluetoothService.STATE_LISTENING
                || mBluetoothService.getState() == MissionBluetoothService.STATE_CONNECTING) {
            ToastUtil.show(mContext, "发送端设备连接中，请稍后……");
        } else if (mBluetoothService.getState() == MissionBluetoothService.STATE_CONNECTED) {
            ToastUtil.show(mContext, "发送端设备已连接，等待发送任务……");
        }
    }

    private void sendMission() {
        if (mBluetoothService == null) {
            mBluetoothService = new MissionBluetoothService(mHandler);
        }

        if (mBluetoothService.getState() == MissionBluetoothService.STATE_NONE) {
            showProgressDialog("等待接收端设备连接……");
            mBluetoothService.startServer();
        } else if (mBluetoothService.getState() == MissionBluetoothService.STATE_LISTENING
                || mBluetoothService.getState() == MissionBluetoothService.STATE_CONNECTING) {
            ToastUtil.show(mContext, "接收端设备连接中，请稍后……");
        } else if (mBluetoothService.getState() == MissionBluetoothService.STATE_CONNECTED) {
            if (mSendMissionBatchId == null) {
                ToastUtil.show(mContext, "发送任务错误，请重试");
                return;
            }
            mDB.getMissionListByBatchId(mSendMissionBatchId, new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {

                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    ArrayList<Mission2> missionList = (ArrayList<Mission2>) result;
                    mBluetoothService.sendMission(missionList.get(0));
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });

        }
    }

    private String FakeUploadMission(ArrayList<Mission2> missionList, MissionBatch2 missionBatch2) {
        StringBuffer area = new StringBuffer();

        switch (missionList.get(0).getGeomType()) {
            case 0:
                MultiPoint multiPoint = missionList.get(0).getMultiPoint();
                for (int i = 0; i < multiPoint.getPointCount(); i++) {
                    BigDecimal bdX = new BigDecimal(multiPoint.getPoint(i).getX());
                    BigDecimal bdY = new BigDecimal(multiPoint.getPoint(i).getY());
                    area.append(bdX.toBigInteger());
                    area.append(" ");
                    area.append(bdY.toBigInteger());
                    if (multiPoint.getPointCount() != i - 2)
                        area.append(",");
                }
                break;
            case 1:
                Polyline polyline = missionList.get(0).getPolyLine();
                for (int i = 0; i < polyline.getPointCount(); i++) {
                    BigDecimal bdX = new BigDecimal(polyline.getPoint(i).getX());
                    BigDecimal bdY = new BigDecimal(polyline.getPoint(i).getY());
                    area.append(bdX.toBigInteger());
                    area.append(" ");
                    area.append(bdY.toBigInteger());
                    if (polyline.getPathCount() != i - 2)
                        area.append(",");
                }
                break;
            case 2:
                Polygon polygon = missionList.get(0).getPolygon();
                for (int i = 0; i < polygon.getPointCount(); i++) {
                    BigDecimal bdX = new BigDecimal(polygon.getPoint(i).getX());
                    BigDecimal bdY = new BigDecimal(polygon.getPoint(i).getY());
                    area.append(bdX.toBigInteger());
                    area.append(" ");
                    area.append(bdY.toBigInteger());
                    if (polygon.getPathCount() != i - 2)
                        area.append(",");
                }
                break;
        }

        int workMode = 0;
        if (missionBatch2.getWorkMode().toLowerCase().equals("tiltimage")) {
            workMode = 1;
        }

        CloudMissionUpload missionUpload = new CloudMissionUpload();
        missionUpload.setId(missionBatch2.getId());
        missionUpload.setName(missionBatch2.getName());
        missionUpload.setMissionType(workMode);
        missionUpload.setFlyAngle(0);
        missionUpload.setArea(area.toString());
        missionUpload.setAltitude(missionBatch2.getAltitude());
        missionUpload.setSpeed(missionList.get(0).getFlySpeed());
        missionUpload.setGimbalAngle(missionList.get(0).getGimbalAngle());
        missionUpload.setResolutionRatio(missionBatch2.getResolutionRate());
        missionUpload.setSideOverlap(missionBatch2.getSideOverlap());
        missionUpload.setRouteOverlap(missionBatch2.getRouteOverlap());
        missionUpload.setFixedAltitude(1);
        missionUpload.setStatus(1);
        missionUpload.setClient("WEB");


        ArrayList<CloudMissionUpload.MissionListBean> listBeans = new ArrayList<>();
        for (Mission2 mission2 : missionList) {
            if (mission2.getFlightNum() == 0)
                continue;
            CloudMissionUpload.MissionListBean listBean = new CloudMissionUpload.MissionListBean();
            listBean.setId(mission2.getId());
            listBean.setParentId("");
            listBean.setName(mission2.getName());
            listBean.setMissionType(workMode);
            listBean.setWorkStep(mission2.getFlightNum());
            listBean.setStartTime(DateHelperUtils.format(mission2.getStartTime()));
            listBean.setEndTime(DateHelperUtils.format(mission2.getEndTime()));
            listBean.setAltitude(mission2.getAltitude());
            listBean.setSpeed(mission2.getFlySpeed());
            listBean.setGimbalAngle(mission2.getGimbalAngle());
            listBean.setResolutionRatio(mission2.getResolutionRate());
            listBean.setSideOverlap(mission2.getSideOverlap());
            listBean.setRouteOverlap(mission2.getRouteOverlap());
            listBean.setFixedAltitude(1);
            listBean.setStartPoint("");
            listBean.setHomePoint("");
            listBean.setMissionPhotoCount(mission2.getPhotoNum());
            listBean.setCreatedTime(DateHelperUtils.format(mission2.getCreateDate()));
            listBean.setUpdatedTime(DateHelperUtils.format(new Date()));
            listBeans.add(listBean);
        }
        missionUpload.setMissionList(listBeans);

        return new Gson().toJson(missionUpload);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MissionBluetoothService.MESSAGE_TOAST:
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;
                case MissionBluetoothService.MESSAGE_STATE_CHANGE:
                    switch ((int) msg.obj) {
                        case MissionBluetoothService.STATE_NONE:
                        case MissionBluetoothService.STATE_CONNECTED:
                            dismissProgressDialog(null);
                            break;
                    }
                    break;
                case MissionBluetoothService.MESSAGE_SEND:
                    switch (msg.arg1) {
                        case MissionBluetoothService.SEND_SUCCESS:
                            dismissProgressDialog("发送任务" + msg.obj + "成功");
                            break;
                        case MissionBluetoothService.SEND_FAILED:
                            dismissProgressDialog("发送任务" + msg.obj + "失败");
                            break;
                    }
                    break;
                case MissionBluetoothService.MESSAGE_RECEIVE:
                    switch (msg.arg1) {
                        case MissionBluetoothService.RECEIVE_SUCCESS:

                            try {
                                ExportMission exportMission = (ExportMission) msg.obj;
                                dismissProgressDialog("接收任务" + exportMission.getMission().getName() + "成功");
                                insertMissionToDB(exportMission);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                       /* case MissionBluetoothService.RECEIVE_FAILED:
                            dismissProgressDialog("任务接收失败");
                            break;*/
                    }
                    break;
                case MissionBluetoothService.MESSAGE_PROGRESS:
                    switch (msg.arg1) {
                        case MissionBluetoothService.SEND_PROGRESS:
                            showProgressDialog("正在发送任务" + msg.arg2 + "%……");
                            break;
                        case MissionBluetoothService.RECEIVE_PROGRESS:
                            showProgressDialog("正在接收任务" + msg.arg2 + "%……");
                            break;
                    }
                    break;
            }
        }
    };

    private void insertMissionToDB(final ExportMission exportMission) {

        mDB.saveTransmissionMission(exportMission, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(final boolean succ, String errStr) {

                if (succ) {
                    mHandler.sendMessage(mHandler.obtainMessage(MissionBluetoothService.MESSAGE_TOAST, "任务保存到数据库成功"));
                    loadData();
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MissionBluetoothService.MESSAGE_TOAST, "任务保存到数据库失败"));
                }
            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {

            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });
    }

    private void showScanBluetoothDialog() {
        if (!checkBluetoothAvailable()) {
            return;
        }
        mBluetooth.startDiscovery();
        Set<BluetoothDevice> bondedDevices = mBluetooth.getBondedDevices();
        mDeviceList.clear();
        mDeviceList.addAll(bondedDevices);

        if (mBluetoothScanDlg == null) {
            mBluetoothScanDlg = new BluetoothScanDialog();
            Bundle args = new Bundle();
            args.putParcelableArrayList(BluetoothScanDialog.DEVICE_LIST, mDeviceList);
            mBluetoothScanDlg.setArguments(args);
            mBluetoothScanDlg.setOnScanListener(new BluetoothScanDialog.onScanListener() {
                @Override
                public void onSelectDevice(BluetoothDevice device) {

                  
                    mBluetooth.cancelDiscovery();
                    mBluetoothService.connectServer(mBluetooth.getRemoteDevice(device.getAddress()));

                }

                @Override
                public void onCancel() {
                    mBluetooth.cancelDiscovery();
                }
            });
        }
        mBluetoothScanDlg.show(getFragmentManager(), "bluetooth");
        mBluetoothScanDlg.refreshDeviceList();
    }

    private void showProgressDialog(String message) {
        if (mProgressDlg == null) {
            mProgressDlg = new BaseProgressDialog(mContext);
            mProgressDlg.setCancelable(true);
        }

        mProgressDlg.setMessage(message);

        if (!mProgressDlg.isShowing()) {
            mProgressDlg.show();
        }
    }

    private void dismissProgressDialog(String message) {
        if (mProgressDlg != null && mProgressDlg.isShowing())
            mProgressDlg.dismiss();
        if (!TextUtils.isEmpty(message))
            ToastUtil.show(mContext, message);
    }

    private boolean checkBluetoothAvailable() {
        if (mBluetooth == null) {
            ToastUtil.show(mContext, "蓝牙不可用");
        }
        return mBluetooth != null;
    }
}