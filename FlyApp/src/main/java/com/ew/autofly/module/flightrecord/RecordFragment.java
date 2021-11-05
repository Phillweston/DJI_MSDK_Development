package com.ew.autofly.module.flightrecord;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ew.autofly.R;
import com.ew.autofly.base.BaseMvpFragment;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.entity.ExportFlightRecord;
import com.ew.autofly.interfaces.OnRecyclerViewItemClickListener;
import com.ew.autofly.module.flightrecord.adapter.RecordAdapter;
import com.ew.autofly.module.flightrecord.presenter.RecordPresenter;
import com.ew.autofly.module.flightrecord.view.IRecordView;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.io.excel.JxlExcelHelper;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.flycloud.autofly.base.util.ToastUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class RecordFragment extends BaseMvpFragment<IRecordView, RecordPresenter> implements IRecordView {

    private RecyclerView mRecordRv;
    private RecordAdapter mRecordAdapter;

    private TextView mTvTime;

    private TextView mTvMile;

    private TextView mTvNum;

    private TextView mTvMaxDistance;
    private ImageView mIvLoading;

    private List<FlightRecord> mRecordList;

    
    private TextView mTvNoRecord;

    private ProgressDialog mUploadProgressDialog;
    private TextView mBtnExportFlightRecord;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected RecordPresenter createPresenter() {
        return new RecordPresenter();
    }

    @Override
    protected int setRootViewId() {
        return R.layout.fragment_flight_record;
    }

    @Override
    protected void initRootView(View view) {
        initData();
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.loadFlightRecordList();
    }

    private void initData() {
        mRecordList = new ArrayList<>();
    }

    private void initView(View rootView) {

        mTvTime = (TextView) rootView.findViewById(R.id.tv_time);
        mTvMile = (TextView) rootView.findViewById(R.id.tv_mile);
        mTvNum = (TextView) rootView.findViewById(R.id.tv_num);
        mTvMaxDistance = (TextView) rootView.findViewById(R.id.tv_max_distance);
        mIvLoading = (ImageView) rootView.findViewById(R.id.iv_loading);
        mRecordRv = (RecyclerView) rootView.findViewById(R.id.rv_record);
        mTvNoRecord = (TextView) rootView.findViewById(R.id.tv_no_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecordRv.setLayoutManager(linearLayoutManager);
        mRecordAdapter = new RecordAdapter(getContext(), mRecordList);
        mRecordRv.setAdapter(mRecordAdapter);

        mRecordAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = mRecordRv.getChildAdapterPosition(view);
                mRecordAdapter.openFunctionMenu(position);
            }

            @Override
            public void onItemLongClick(View view) {

            }
        });

        mRecordAdapter.setonClickFunctionMenu(new RecordAdapter.onClickFunctionMenu() {
            @Override
            public void onClickRead(int position) {
                mRecordAdapter.closeFunctionMenu();
                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                final FlightRecord record = mRecordList.get(position);
                intent.putExtra(PlaybackActivity.ARG_FLIGHT_RECORD, record);
                startActivity(intent);
            }

            @Override
            public void onClickDelete(final int position) {
                CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                deleteDialog.setTitle(getActivity().getString(R.string.notice))
                        .setMessage(getActivity().getString(R.string.default_delete_notice))
                        .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mRecordAdapter.closeFunctionMenu();
                                final FlightRecord record = mRecordList.get(position);
                                mPresenter.deleteFlightRecord(record);

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
            public void onClickUpload(int position) {
                mRecordAdapter.closeFunctionMenu();
                final FlightRecord record = mRecordList.get(position);
                mPresenter.uploadFlightRecord(position, record, "1", "test");
            }
        });

        mBtnExportFlightRecord = rootView.findViewById(R.id.btn_export_flight_record);
        mBtnExportFlightRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportFlightRecord();
            }
        });
    }

    @Override
    public void onLoadRecordList(List<FlightRecord> flightRecordList) {
        mRecordList.clear();
        mRecordList.addAll(flightRecordList);
        mRecordAdapter.notifyDataSetChanged();
        refreshFlightStatistics(flightRecordList);
    }

    @Override
    public void onDeleteRecord(FlightRecord record) {

        try {
            mRecordList.remove(record);
            mRecordAdapter.notifyDataSetChanged();
            if (mRecordList.size() == 0)
                showEmpty(true, null);
            refreshFlightStatistics(mRecordList);
        } catch (Exception e) {

        }
    }

    @Override
    public void showUploadProgress(boolean isShow) {
        if (isShow) {
            mUploadProgressDialog = ProgressDialog.show(mContext, null, "正在上传……");
        } else {
            if (mUploadProgressDialog != null && mUploadProgressDialog.isShowing()) {
                mUploadProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void showUploadSuccess(int position) {
        final FlightRecord record = mRecordList.get(position);
        record.setIsUpload(true);
        mRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String toast) {
        ToastUtil.show(mContext, toast);
    }

    @Override
    public void showLoading(boolean isShow, String loadingMsg) {
        if (isShow) {
            mIvLoading.setVisibility(View.VISIBLE);
            mTvNoRecord.setVisibility(View.GONE);
            Glide.with(requireActivity().getBaseContext()).load(R.drawable.loading).asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mIvLoading);
        } else {
            mIvLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(boolean isShow, String errorMsg) {

    }

    @Override
    public void showEmpty(boolean isShow, String emptyMsg) {
        mTvNoRecord.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mIvLoading.setVisibility(View.GONE);
    }

    private void refreshFlightStatistics(List<FlightRecord> flightRecordList) {

        if (flightRecordList == null) {
            return;
        }

        int totalSecond = 0;
        double totalDistance = 0;
        double maxDistance = 0;

        for (FlightRecord record : flightRecordList) {

            String startTimeStr = record.getStartTime();
            String endTimeStr = record.getEndTime();

            if (!TextUtils.isEmpty(startTimeStr) && !TextUtils.isEmpty(endTimeStr)) {
                int second = (int) (DateHelperUtils.string2DateTime(endTimeStr).getTime()
                        - DateHelperUtils.string2DateTime(startTimeStr).getTime()) / 1000;
                totalSecond += second;
            }

            double distance = record.getDistance();
            totalDistance += distance;
            maxDistance = maxDistance < distance ? distance : maxDistance;
        }

        String time = DateHelperUtils.formatTimeByHM(totalSecond);
        mTvTime.setText(time);

        DecimalFormat decimalFormat = new DecimalFormat("###,##0");

        mTvMile.setText(decimalFormat.format((int) totalDistance) + "m");

        mTvNum.setText(flightRecordList.size() + "次");

        mTvMaxDistance.setText(decimalFormat.format((int) maxDistance) + "m");
    }

    
    private void exportFlightRecord() {

        if (mRecordList != null && !mRecordList.isEmpty()) {

            try {

                List<ExportFlightRecord> exportFlightRecordList = new ArrayList<>();
                for (FlightRecord flightRecord : mRecordList) {
                    ExportFlightRecord exportFlightRecord = new ExportFlightRecord();
                    exportFlightRecord.setCreatedTime(flightRecord.getCreatedTime());
                    double distance = flightRecord.getDistance();
                    exportFlightRecord.setDistance(Math.round(distance * 100) / 100.0);
                    exportFlightRecord.setMaxHeight(flightRecord.getMaxHeight());
                    exportFlightRecord.setLatitude(flightRecord.getLatitude());
                    exportFlightRecord.setLongitude(flightRecord.getLongitude());

                    String startTimeStr = flightRecord.getStartTime();
                    String endTimeStr = flightRecord.getEndTime();

                    if (!TextUtils.isEmpty(startTimeStr) && !TextUtils.isEmpty(endTimeStr)) {
                        float min = (DateHelperUtils.string2DateTime(endTimeStr).getTime()
                                - DateHelperUtils.string2DateTime(startTimeStr).getTime()) / 1000 / 60.00f;
                        exportFlightRecord.setTime(Math.round(min * 100) / 100.0f);
                    }
                    exportFlightRecordList.add(exportFlightRecord);
                }

                String[] fieldName = new String[]{"createdTime", "distance", "longitude", "latitude", "time", "maxHeight"};
                String[] title = new String[]{"日期", "里程(m)", "经度", "纬度", "用时(min)", "最大高度(m)"};

                String path = IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_EXPORT_FLIGHTRECORD;
                JxlExcelHelper excelHelper = JxlExcelHelper.getInstance(path + File.separator
                        + DateHelperUtils.format(new Date(), "yyyy-MM-dd-HHmmss") + ".xls");
                excelHelper.writeExcel(ExportFlightRecord.class, exportFlightRecordList, fieldName, title);

                ToastUtil.showLongTime(mContext, "飞行记录已导出到目录：内部存储/" + AppConstant.APP_STORAGE_PATH
                        + File.separator + AppConstant.DIR_EXPORT_FLIGHTRECORD);

            } catch (Exception e) {
                e.printStackTrace();
                showToast("导出飞行记录失败");
            }

        } else {
            showToast("没有飞行记录");
        }
    }

}
