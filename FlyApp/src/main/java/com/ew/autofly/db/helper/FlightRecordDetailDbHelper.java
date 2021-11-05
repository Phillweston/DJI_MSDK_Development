package com.ew.autofly.db.helper;

import com.ew.autofly.BuildConfig;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.FlightRecordDetailDao;
import com.ew.autofly.db.entity.FlightRecordDetail;
import com.ew.autofly.utils.LogUtilsOld;

import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;



public class FlightRecordDetailDbHelper {

    private static FlightRecordDetailDbHelper INSTANCE;

    private FlightRecordDetailDao mDao;

    public FlightRecordDetailDbHelper() {
        mDao= EWApplication.getInstance().getDaoSession().getFlightRecordDetailDao();
    }

    public static FlightRecordDetailDbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (FlightRecordDetailDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlightRecordDetailDbHelper();
                }
            }
        }
        return INSTANCE;
    }

    public boolean save(FlightRecordDetail flightRecordDetail) {
        try {
            long result = mDao.insert(flightRecordDetail);
            return result>0;
        }catch (Exception e){
            if(BuildConfig.DEBUG){
                LogUtilsOld.getInstance(EWApplication.getInstance()).e("更新飞行记录失败" + e.toString());
            }
        }
        return false;
    }

    public void getDetailListByRecordId(String recordId,AsyncOperationListener listener) {
        QueryBuilder builder = mDao.queryBuilder().where(FlightRecordDetailDao.Properties.RecordId.eq(recordId));
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }

    public void getDetailListByMissionId(String missionId,AsyncOperationListener listener) {
        QueryBuilder builder = mDao.queryBuilder().where(FlightRecordDetailDao.Properties.MissionId.eq(missionId));
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }

    public void getAllDetailList(AsyncOperationListener listener) {
        QueryBuilder builder = mDao.queryBuilder();
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }

    public void deleteDetailListByRecordId(String recordId){
        QueryBuilder<FlightRecordDetail> builder = mDao.queryBuilder()
                .where(FlightRecordDetailDao.Properties.RecordId.eq(recordId));
        List<FlightRecordDetail> detailList= builder.build().list();
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.deleteInTx(FlightRecordDetail.class,detailList);
    }
}
