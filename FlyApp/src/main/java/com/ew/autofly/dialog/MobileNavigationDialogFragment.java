package com.ew.autofly.dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.io.File;

public class MobileNavigationDialogFragment extends DialogFragment implements View.OnClickListener {

    private int mSelectMap = 0;
    private int AMAP = 1001;
    private int BAIDUMAP = 1002;
    private TextView tvAmap;
    private TextView tvBaidu;
    private LatLngInfo destination;
    private TextView tvOther;
    private boolean isInstallBaidu;
    private boolean isInstallAmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        destination = (LatLngInfo) getArguments().getSerializable("location");
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.4), (int) (dm.heightPixels * 0.5));
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.4), (int) (dm.widthPixels * 0.5));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_mobile_navigation, container, false);
        bindField(view);
        loadPackage();
        return view;
    }

    private void bindField(View view) {
        tvAmap = (TextView) view.findViewById(R.id.tv_map_amap);
        tvBaidu = (TextView) view.findViewById(R.id.tv_map_baidu);
        tvOther = (TextView) view.findViewById(R.id.tv_map_other);

        tvAmap.setOnClickListener(this);
        tvBaidu.setOnClickListener(this);
        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.tv_start).setOnClickListener(this);
    }

    private void loadPackage() {
        isInstallBaidu = isInstallByread("com.baidu.BaiduMap");
        isInstallAmap = isInstallByread("com.autonavi.minimap");






    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_map_amap:
                if (mSelectMap == AMAP) {
                    tvAmap.setBackgroundResource(0);
                    mSelectMap = 0;
                    return;
                }
                tvBaidu.setBackgroundResource(0);
                tvAmap.setBackgroundColor(Color.parseColor("#696969"));
                mSelectMap = AMAP;
                break;
            case R.id.tv_map_baidu:
                if (mSelectMap == BAIDUMAP) {
                    tvBaidu.setBackgroundResource(0);
                    mSelectMap = 0;
                    return;
                }
                tvAmap.setBackgroundResource(0);
                tvBaidu.setBackgroundColor(Color.parseColor("#696969"));
                mSelectMap = BAIDUMAP;
                break;
            case R.id.tv_start:
                if ((tvAmap.getVisibility() != View.GONE || tvBaidu.getVisibility() != View.GONE) && mSelectMap == 0) {
                    ToastUtil.show(getActivity(), "请选择导航地图");
                    return;
                } else if (mSelectMap != 0) {
                    String mapName = mSelectMap == AMAP ? "高德地图" : "百度地图";
                    if ((mSelectMap == AMAP && isInstallAmap) || (mSelectMap == BAIDUMAP && isInstallBaidu)) {
                        readyNavigation(mSelectMap);
                    } else {
                        CustomDialog.Builder comfirmDialog = new CustomDialog.Builder(getActivity());
                        comfirmDialog.setTitle("导航准备")
                                .setMessage("未检测到 " + mapName + "\n点击确认跳转下载")
                                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i;
                                        if (mSelectMap == AMAP) {
                                            i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mobile.amap.com/"));
                                            startActivity(i);
                                        } else if (mSelectMap == BAIDUMAP) {
                                            i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://map.baidu.com/zt/client/index/"));
                                            startActivity(i);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create()
                                .show();
                    }

                }
                break;
        }
    }

    /**
     * 应用是否存在
     *
     * @param packageName 包名
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 调用地图
     *
     * @param mSelectMap 地图类型
     */
    private void readyNavigation(int mSelectMap) {
        try {
            if (mSelectMap == AMAP) {
                StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                        .append("yitu8_driver").append("&lat=").append(destination.latitude)
                        .append("&lon=").append(destination.longitude)
                        .append("&dev=").append(0)
                        .append("&style=").append(0);
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(stringBuffer.toString()));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setPackage("com.autonavi.minimap");
                startActivity(intent);
                dismiss();
            } else if (mSelectMap == BAIDUMAP) {
                LatLngInfo BD09 = CoordinateUtils.gcj02_To_Bd09(destination.latitude, destination.longitude);
                StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=")
                        .append(BD09.latitude).append(",").append(BD09.longitude).append("&type=TIME");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
                intent.setPackage("com.baidu.BaiduMap");
                startActivity(intent);
                dismiss();
            }
        } catch (ActivityNotFoundException e) {
            String mapName = mSelectMap == AMAP ? "高德地图" : "百度地图";
            ToastUtil.show(getActivity(), "打开 " + mapName + " 失败，请升级或重新安装");
        }
    }
}