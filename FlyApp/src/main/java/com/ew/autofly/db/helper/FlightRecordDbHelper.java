package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.FlightRecordDao;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.utils.LogUtilsOld;

import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;



public class FlightRecordDbHelper {

    private static FlightRecordDbHelper INSTANCE;

    private FlightRecordDao mDao;

    private FlightRecord mCurrentRecord;

    public FlightRecordDbHelper() {
        mDao = EWApplication.getInstance().getDaoSession().getFlightRecordDao();
    }

    public static FlightRecordDbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (FlightRecordDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlightRecordDbHelper();
                }
            }
        }
        return INSTANCE;
    }

    public FlightRecord getCurrentRecord() {
        return mCurrentRecord;
    }

    public void setCurrentRecord(FlightRecord mCurrentRecord) {
        this.mCurrentRecord = mCurrentRecord;
    }

    public boolean save(FlightRecord flightRecord) {
        try {
            long result = mDao.insert(flightRecord);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).e("创建飞行记录失败" + e);

        }

        return false;
    }

    public boolean update(FlightRecord flightRecord) {
        try {
            mDao.update(flightRecord);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).e("更新飞行记录失败" + e);
        }

        return false;
    }

    public boolean delete(FlightRecord flightRecord) {
        try {
            mDao.delete(flightRecord);
            FlightRecordDetailDbHelper.getInstance().deleteDetailListByRecordId(flightRecord.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).e("删除飞行记录失败" + e);
        }

        return false;
    }

    public void getAllRecordList(final AsyncOperationListener listener) {

        QueryBuilder builder = mDao.queryBuilder().orderDesc(FlightRecordDao.Properties.CreatedTime);
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }

    public void getRecordById(String id, final AsyncOperationListener listener) {
        QueryBuilder builder = mDao.queryBuilder().where(FlightRecordDao.Properties.Id.eq(id));
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryUnique(builder.build());
    }
}
