package com.ew.autofly.module.flightrecord.presenter;

import android.text.TextUtils;

import com.ew.autofly.R;
import com.ew.autofly.base.BaseMvpPresenter;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.db.entity.FlightRecordDetail;
import com.ew.autofly.db.helper.FlightRecordDbHelper;
import com.ew.autofly.db.helper.FlightRecordDetailDbHelper;
import com.ew.autofly.entity.FlightRecordUpload;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.module.flightrecord.view.IRecordView;
import com.ew.autofly.net.OkHttpUtil;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.google.gson.Gson;
import com.flycloud.autofly.base.net.entity.HttpResponse;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;

import java.util.ArrayList;
import java.util.List;



public class RecordPresenter extends BaseMvpPresenter<IRecordView> implements IRecordPresenter {

    @Override
    public void loadUserInfo() {
    }

    @Override
    public void loadFlightRecordList() {
        if (isViewAttached())
            getView().showLoading(true, null);

        FlightRecordDbHelper.getInstance().getAllRecordList(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation != null && operation.getResult() != null) {

                    List<FlightRecord> mFlightRecordList = (List<FlightRecord>) operation.getResult();
                    if (mFlightRecordList == null || mFlightRecordList.isEmpty()) {
                        if (isViewAttached())
                            getView().showEmpty(true, null);
                        return;
                    }

                    if (isViewAttached()) {
                        getView().onLoadRecordList(mFlightRecordList);
                        getView().showLoading(false, null);
                    }

                } else {
                    if (isViewAttached())
                        getView().showEmpty(true, null);
                }

            }
        });

    }

    @Override
    public void deleteFlightRecord(FlightRecord record) {
        boolean isDelete = FlightRecordDbHelper.getInstance().delete(record);
        if (isDelete) {
            if (isViewAttached())
                getView().onDeleteRecord(record);
        } else {
            if (isViewAttached())
                getView().showToast(getString(R.string.flight_record_delete_fail));

        }
    }

    @Override
    public void uploadFlightRecord(final int position, final FlightRecord record, final String userId, final String userName) {
        final String recordId = record.getId();
        FlightRecordDetailDbHelper.getInstance().getDetailListByRecordId(recordId, new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation != null && operation.isCompletedSucessfully()) {
                    List<FlightRecordDetail> flightRecordDetailList = (List<FlightRecordDetail>) operation.getResult();

                    FlightRecordUpload recordUpload = new FlightRecordUpload();

                    recordUpload.setUserId(userId);
                    recordUpload.setUserName(userName);
                    recordUpload.setLongitude(record.getLongitude());
                    recordUpload.setLongitude(record.getLatitude());
                    recordUpload.setDroneModel(record.getProductName());
                    recordUpload.setDistance(record.getDistance());
                    String startTimeStr = record.getStartTime();
                    String endTimeStr = record.getEndTime();
                    recordUpload.setStartTime(startTimeStr);
                    recordUpload.setEndTime(endTimeStr);
                    if (!TextUtils.isEmpty(startTimeStr) && !TextUtils.isEmpty(endTimeStr)) {
                        double min = (DateHelperUtils.string2DateTime(endTimeStr).getTime()
                                - DateHelperUtils.string2DateTime(startTimeStr).getTime()) / 1000 / 60;
                        recordUpload.setFlightTime(min);
                    }
                    recordUpload.setMaxAltitude(record.getMaxHeight());
                    recordUpload.setFlightSerialNumber(record.getProductSerialNumber());
                    recordUpload.setBatterySerialNumber(record.getBatterySerialNumber());
                    recordUpload.setCreatedTime(record.getCreatedTime());

                    List<LatLngInfo> latLngInfoList = new ArrayList<>();

                    for (FlightRecordDetail recordDetail : flightRecordDetailList) {
                        LatLngInfo latLngInfo = new LatLngInfo(recordDetail.getLatitude(), recordDetail.getLongitude());
                        latLngInfoList.add(latLngInfo);
                    }

                    recordUpload.setFlightRecordDetailList(latLngInfoList);

                    Gson gson = new Gson();
                    String recordStr = gson.toJson(recordUpload);

                    String url = "http://192.168.1.106:8080/spring-shiro-training/flightRecord/upload";

                    if (isViewAttached())
                        getView().showUploadProgress(true);

                    OkHttpUtil.doPostJsonAsyncUI(url, recordStr, new OkHttpUtil.HttpCallBack<HttpResponse<String>>() {

                        @Override
                        public void onSuccess(HttpResponse<String> result) {
                            if (result.getCode() == 200) {
                                if (isViewAttached()) {
                                    getView().showUploadSuccess(position);
                                    getView().showToast("上传成功");
                                }
                                record.setIsUpload(true);
                                FlightRecordDbHelper.getInstance().update(record);
                            } else {
                                if (isViewAttached()) {
                                    getView().showToast("上传失败");
                                }
                            }

                            if (isViewAttached())
                                getView().showUploadProgress(false);
                        }

                        @Override
                        public void onError(Exception e) {
                            if (isViewAttached()) {
                                getView().showUploadProgress(false);
                                getView().showToast("上传失败");
                            }
                        }
                    });
                }
            }
        });
    }


}
