package com.ew.autofly.fragments;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ew.autofly.BuildConfig;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.DJIAccountStatusDialogFragment;
import com.ew.autofly.dialog.DemDataInfoFragment;
import com.ew.autofly.dialog.UpdateDlgFragment;
import com.ew.autofly.interfaces.OnDemFragmentClickListener;
import com.ew.autofly.interfaces.OnUpdateDialogClickListener;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.net.OkHttpUtil;
import com.ew.autofly.service.UpdateService;
import com.flycloud.autofly.base.util.NetworkUtil;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.DialogUtils;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.flycloud.autofly.control.service.RouteService;
import com.tencent.bugly.beta.Beta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.products.Aircraft;
import dji.thirdparty.okhttp3.Call;
import dji.thirdparty.okhttp3.Callback;
import dji.thirdparty.okhttp3.Response;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.ew.autofly.constant.AppConstant.DIR_MAP_CACHE;

/**
 * Created by g2526 on 2016/3/2.
 * 系统设置
 */
public class SysSettingFragment extends BaseFragment implements OnUpdateDialogClickListener {
    private SharedConfig config;
    private RadioGroup rgMap;
    private RadioButton rbGoogle;
    private RadioButton rbGaode;
    private RadioButton rbOpenCycle;
    private TextView btnClearCache;
    private CheckBox cbSimulate, cbRTK, cbPSDK;
    private TextView btnUpdateVersion;
    private EditText edtUploadImageUrl;
    private EditText edtDownloadImageUrl;
    private TextView tvSoftwareVersion;
    private View view;
    private String[] result;
    private String urlPath;
    private boolean bForceUpdate = false;

    private TextView tvMachineCode, tvExpireDate;
    private TextView tvGoHomeHeight;
    private TextView tvMaxHeight;
    private int maxHeight;
    private int goHomeHeight;

    private DownloadManager downloadManager;


    private final String CACHE_URL = Environment.getExternalStorageDirectory().getPath()
            + File.separator + AppConstant.APP_STORAGE_PATH + File.separator + DIR_MAP_CACHE;

    private void startUpdateService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent service = new Intent(getActivity(), UpdateService.class);
                service.putExtra("url", urlPath);
                getContext().startService(service);
            }
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        config = new SharedConfig(getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sys_setting, null);
        initView(view);
        initFlyParameter(view);

        initPsdkSetting();
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        return view;
    }

    private void initPsdkSetting() {
        if (config != null) {
            cbPSDK.setChecked(config.getPsdkEnable());
        }
    }

  /*  private void initRTKSetting() {
        if (EWApplication.getProductInstance() != null
                && null != ((Aircraft) EWApplication.getProductInstance()).getFlightController()) {
            if (((Aircraft) EWApplication.getProductInstance()).getFlightController().isRTKSupported()){
                RTK rtk = ((Aircraft) EWApplication.getProductInstance()).getFlightController().getRTK();
                if (!rtk.isConnected()){
                    rtk.setStateCallback(new RTKState.Callback() {
                        @Override
                        public void onUpdate(RTKState rtkState) {
                            if (rtkState.isRTKEnabled()){
                                cbRTK.setEnabled(true);
                                cbRTK.setChecked(true);
                            } else {
                                cbRTK.setEnabled(true);
                                cbRTK.setChecked(false);
                            }
                        }
                    });
                }
            } else {
                cbRTK.setEnabled(false);
            }
        }
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initFlyParameter(View view) {
        tvMaxHeight = (TextView) view.findViewById(R.id.edit_max_height);
        tvGoHomeHeight = (TextView) view.findViewById(R.id.edit_return_home_height);
        getBaseFlyingHeightParameter(tvMaxHeight);
        getBaseGohomeHeightParameter(tvGoHomeHeight);
        tvMaxHeight.setOnClickListener(clickListener);
        tvGoHomeHeight.setOnClickListener(clickListener);
    }

    private void getBaseGohomeHeightParameter(final TextView v) {
        v.setClickable(true);
        if (EWApplication.getProductInstance() != null
                && null != ((Aircraft) EWApplication.getProductInstance()).getFlightController()) {
            ((Aircraft) EWApplication.getProductInstance()).getFlightController().getGoHomeHeightInMeters(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    goHomeHeight = integer;
                    if (isAdded())
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setText(String.valueOf(goHomeHeight));
                            }
                        });
                }

                @Override
                public void onFailure(DJIError djiError) {
                    goHomeHeight = config.getGoHomeHeight();
                    if (isAdded())
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setText(String.valueOf(goHomeHeight));
                            }
                        });
                }
            });
        } else {
            goHomeHeight = config.getGoHomeHeight();
            v.setText(String.valueOf(goHomeHeight));
        }
    }

    private void getBaseFlyingHeightParameter(final TextView v) {
        v.setClickable(true);
        if (EWApplication.getProductInstance() != null &&
                null != ((Aircraft) EWApplication.getProductInstance()).getFlightController()) {
            ((Aircraft) EWApplication.getProductInstance()).getFlightController().getMaxFlightHeight(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    maxHeight = integer;
                    if (isAdded())
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setText(String.valueOf(maxHeight));
                            }
                        });
                }

                @Override
                public void onFailure(DJIError djiError) {
                    maxHeight = config.getMaxFlyHeight();
                    if (isAdded())
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setText(String.valueOf(maxHeight));
                            }
                        });
                }
            });
        } else {
            maxHeight = config.getMaxFlyHeight();
            v.setText(String.valueOf(maxHeight));
        }
    }

    private void initData() {
        if (config.getSimulateState())
            cbSimulate.setChecked(true);
        else
            cbSimulate.setChecked(false);
        int mapType = config.getMapType();
        if (mapType == 0) {
            rbGoogle.setChecked(true);
        } else if (mapType == 1) {
            rbGaode.setChecked(true);
        } else {
            rbOpenCycle.setChecked(true);
        }

        String uploadImageUrl = config.getUploadImgUrl();
        if (!TextUtils.isEmpty(uploadImageUrl)) {
            edtUploadImageUrl.setText(uploadImageUrl);
        }
        String downloadImageUrl = config.getDownloadImgUrl();
        if (!TextUtils.isEmpty(downloadImageUrl)) {
            edtDownloadImageUrl.setText(downloadImageUrl);
        }
        setMachineCode();
    }

    private void setMachineCode() {
        String machineCode;
        String uid = SysUtils.getDeviceId(getActivity());
        machineCode = SysUtils.stringToMD5(uid);

        machineCode = machineCode.substring(0, 4) + "-"
                + machineCode.substring(4, 8) + "-"
                + machineCode.substring(8, 12) + "-"
                + machineCode.substring(12, 16);
        machineCode = machineCode.toUpperCase();
        tvMachineCode.setText(machineCode);
    }

    private void initView(View v) {
        rgMap = (RadioGroup) v.findViewById(R.id.rg_map);
        rbGoogle = (RadioButton) v.findViewById(R.id.rb_google);
        rbGaode = (RadioButton) v.findViewById(R.id.rb_gaode);
        rbOpenCycle = (RadioButton) v.findViewById(R.id.rb_opencycle);
        btnClearCache = (TextView) v.findViewById(R.id.btn_clear_cache);
        cbSimulate = (CheckBox) v.findViewById(R.id.cb_simulator);
        cbSimulate.setOnCheckedChangeListener(checkedChangeListener);
        cbRTK = (CheckBox) v.findViewById(R.id.cb_rtk);
        cbRTK.setOnCheckedChangeListener(checkedChangeListener);
        cbPSDK = (CheckBox) v.findViewById(R.id.cb_psdk);
        cbPSDK.setOnCheckedChangeListener(checkedChangeListener);
        edtUploadImageUrl = (EditText) v.findViewById(R.id.et_upload_img_url);
        edtDownloadImageUrl = (EditText) v.findViewById(R.id.et_download_img_url);
        tvSoftwareVersion = (TextView) v.findViewById(R.id.tv_software_version);
        btnUpdateVersion = (TextView) v.findViewById(R.id.btn_update_version);
        tvMachineCode = (TextView) v.findViewById(R.id.tv_device_code);
        tvExpireDate = (TextView) v.findViewById(R.id.tv_expire_date);
        String versionStr = tvSoftwareVersion.getText().toString();
        if (!TextUtils.isEmpty(versionStr) && versionStr.contains("免费")) {
            tvExpireDate.setVisibility(View.GONE);
        }
        btnClearCache.setOnClickListener(clickListener);
        btnUpdateVersion.setOnClickListener(clickListener);
        v.findViewById(R.id.tv_set_params).setOnClickListener(clickListener);
        v.findViewById(R.id.tv_check_dji_account_status).setOnClickListener(clickListener);
        v.findViewById(R.id.btn_map_dem).setOnClickListener(clickListener);

        addTextWatchListener();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_clear_cache:
                    clearCache();
                    break;
                case R.id.btn_update_version:
                    if (!NetworkUtil.checkNetWork(getContext())) {
                        ToastUtil.show(getContext(), "请检查您的网络是否已连接！");
                        return;
                    }

                    Beta.checkUpgrade(true, false);
                    break;
                case R.id.tv_check_dji_account_status:
                    DJIAccountStatusDialogFragment dlg = new DJIAccountStatusDialogFragment();
                    dlg.show(getFragmentManager(), "");
                    break;
                case R.id.btn_map_dem:
                    DemDataInfoFragment demFragment = new DemDataInfoFragment();
                    demFragment.show(getFragmentManager(), "demdata");
                    OnDemFragmentClickListener dft = demFragment;
                    dft.loadDownloadManager(downloadManager);
                    break;
                case R.id.tv_set_params:
                    break;
                case R.id.edit_max_height:
                    final EditText edtMaxHeight = new EditText(getActivity());
                    edtMaxHeight.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    edtMaxHeight.setText(SysSettingFragment.this.tvMaxHeight.getText().toString());
                    Dialog dialogMaxHeight = DialogUtils.createCustomDialog(getActivity(), -1, "设置最大航高", edtMaxHeight, "确定", "取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setMaxFlightHeight(edtMaxHeight.getText().toString());
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dialogMaxHeight.show();
                    break;
                case R.id.edit_return_home_height:
                    final EditText edtReturnHeight = new EditText(getActivity());
                    edtReturnHeight.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    edtReturnHeight.setText(SysSettingFragment.this.tvGoHomeHeight.getText().toString());
                    Dialog dialogReturnHeight = DialogUtils.createCustomDialog(getActivity(), -1, "设置最大航高", edtReturnHeight, "确定", "取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setReturnHomeHeight(edtReturnHeight.getText().toString());
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dialogReturnHeight.show();
                    break;
            }
        }
    };

    private void addTextWatchListener() {
        edtUploadImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                config.setUploadImgUrl(s.toString().trim());
            }
        });
        edtDownloadImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                config.setDownloadImgUrl(s.toString().trim());
            }
        });
    }


    private void clearCache() {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
        deleteDialog.setTitle(getResources().getString(R.string.clear_cache_tip_title))
                .setMessage(getResources().getString(R.string.clear_cache_tip))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        showLoadProgressDialog("正在清除缓存……");
                        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                                try {
                                    File file = new File(CACHE_URL);
                                    if (file.exists()) {
                                        RecursionDeleteFile(file);
                                    }
                                    emitter.onNext(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    emitter.onNext(false);
                                }

                            }
                        }).subscribeOn(Schedulers.io())//指定异步任务在IO线程运行
                                .observeOn(AndroidSchedulers.mainThread())//制定执行结果在主线程运行
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean success) throws Exception {
                                        dismissLoadProgressDialog();
                                        Toast.makeText(getActivity(), getResources().getString(success ? R.string.clear_cache_success : R.string.clear_cache_fail), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void RecursionDeleteFile(File file) {
        try {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    RecursionDeleteFile(f);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_simulator:
                    if (isChecked) {
                        config.setSimulateState(true);
                    } else {
                        config.setSimulateState(false);
                    }
                    break;
                case R.id.cb_rtk:
                    if (EWApplication.getProductInstance() != null) {
                        FlightController controller = ((Aircraft) EWApplication.getProductInstance()).getFlightController();
                        RTK rtk = controller.getRTK();
                        if (rtk != null) {
                            if (isChecked) {
                                rtk.setRtkEnabled(true, null);
                            } else {
                                rtk.setRtkEnabled(false, null);
                            }
                        }
                    }
                    break;
                case R.id.cb_psdk:
                    if (isChecked) {
                        config.setPsdkEnable(true);
                    } else {
                        config.setPsdkEnable(false);
                    }
                    break;
            }
        }
    };

    private void checkNewVersion() {
        String appId = BuildConfig.APPLICATION_ID;
        String ver = "";
        String cid = "";
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            ver = pInfo.versionName.replace("v", "");
                cid = AppConstant.CHANNEL_NAME_EASY_FLY;
        } catch (PackageManager.NameNotFoundException e) {

        }
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "提示", "操作请求正在发送，请稍等", true, false);
        Map<String, String> map = new HashMap<>();
        map.put("appname", appId);
        map.put("cid", cid);
        map.put("ver", ver);
        OkHttpUtil.doPost(AppConstant.CHECK_UPDATE_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                dialog.dismiss();
                if (null != res && !res.trim().equals("")) {
                    result = res.split(",");
                    if (result[0].equals("0")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(getContext(), "当前已是最新版本");
                            }
                        });
                    } else {
                        bForceUpdate = result[0].equals("1") ? true : false;
                        urlPath = result[1];
                        UpdateDlgFragment update = new UpdateDlgFragment();
                        Bundle args = new Bundle();
                        args.putBoolean("force", bForceUpdate);
                        update.setArguments(args);
                        update.show(getFragmentManager(), "update");
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getContext(), "未检测到新版本");
                        }
                    });
                }
            }
        });
    }

    /**
     * 设置最大行高
     *
     * @param t height
     */
    private void setMaxFlightHeight(String t) {
        if (t.equals(""))
            t = "20";
        int num = Integer.parseInt(t);
        if (num < 20)
            num = 20;
        if (num > 500)
            num = 500;
        if (EWApplication.getProductInstance() != null &&
                ((Aircraft) EWApplication.getProductInstance()).getFlightController() != null) {
            FlightController controller = ((Aircraft) EWApplication.getProductInstance()).getFlightController();
            final int finalNum = num;
            controller.setMaxFlightHeight(num, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null && isAdded()) {
                        ToastUtil.show(getContext(), "设置成功");
                        config.setMaxFlyHeight(finalNum);
                        maxHeight = finalNum;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getBaseGohomeHeightParameter(tvGoHomeHeight);
                                tvMaxHeight.setText(String.valueOf(maxHeight));
                            }
                        });
                    } else {
                        ToastUtil.show(getContext(), "设置失败" + djiError.toString());
                        getBaseFlyingHeightParameter(tvMaxHeight);
                        Logger.d(djiError.toString());
                    }
                }
            });
        } else {
            ToastUtil.show(getContext(), "请连接飞机");
            tvMaxHeight.setText(String.valueOf(maxHeight));
        }
    }

    /**
     * 设置返航高度
     *
     * @param t 返航高度
     */
    private void setReturnHomeHeight(String t) {
        if (t.equals(""))
            t = "20";
        int num = Integer.parseInt(t);
        if (num < 20)
            num = 20;
        if (num > 500)
            num = 500;
        if (EWApplication.getProductInstance() != null &&
                ((Aircraft) EWApplication.getProductInstance()).getFlightController() != null) {
            FlightController controller = ((Aircraft) EWApplication.getProductInstance()).getFlightController();
            final int finalNum = num;
            controller.setGoHomeHeightInMeters(num, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null && isAdded()) {
                        ToastUtil.show(getContext(), "设置成功");
                        config.setGoHomeHeight(finalNum);
                        goHomeHeight = finalNum;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getBaseFlyingHeightParameter(tvMaxHeight);
                                tvGoHomeHeight.setText(String.valueOf(goHomeHeight));
                            }
                        });
                    } else {
                        ToastUtil.show(getContext(), "设置失败" + djiError.toString());
                        getBaseGohomeHeightParameter(tvGoHomeHeight);
                        Logger.d(djiError.toString());
                    }
                }
            });
        } else {
            ToastUtil.show(getContext(), "请连接飞机");
            tvGoHomeHeight.setText(String.valueOf(goHomeHeight));
        }
    }

    @Override
    public void onUpdateConfirm() {
        startUpdateService();
        if (bForceUpdate)
            getActivity().finish();
    }

    @Override
    public void onUpdateCancel() {
        if (bForceUpdate) {
            System.exit(0);
        }
    }

    private BaseProgressDialog mLoadProgressDlg;

    public void showLoadProgressDialog(@Nullable String message) {
        if (mLoadProgressDlg == null) {
            mLoadProgressDlg = new BaseProgressDialog(getContext());
            mLoadProgressDlg.setCancelable(false);
        }
        mLoadProgressDlg.setMessage(message);
        mLoadProgressDlg.show();
    }

    public void dismissLoadProgressDialog() {
        if (mLoadProgressDlg != null && mLoadProgressDlg.isShowing()) {
            mLoadProgressDlg.dismiss();
        }
    }

    @Override
    public void onDestroy() {

        EWApplication.getInstance().initDem();
        super.onDestroy();
    }
}