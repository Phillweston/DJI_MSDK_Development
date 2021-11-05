package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.FlightRecordDetailDao;
import com.ew.autofly.db.dao.TowerStudyDetailDao;
import com.ew.autofly.db.entity.TowerStudyDetail;

import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;



public class TowerStudyDetailDbHelper {

    private static TowerStudyDetailDbHelper INSTANCE;

    private TowerStudyDetailDao mDao;

    public TowerStudyDetailDbHelper() {
        mDao= EWApplication.getInstance().getDaoSession().getTowerStudyDetailDao();
    }

    public static TowerStudyDetailDbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (TowerStudyDetailDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TowerStudyDetailDbHelper();
                }
            }
        }
        return INSTANCE;
    }

    public void updateDetailListByTowerId(String towerId,List<TowerStudyDetail> towerStudyDetails){

        mDao.queryBuilder().where(TowerStudyDetailDao.Properties.TowerId.eq(towerId))
                .buildDelete().executeDeleteWithoutDetachingEntities();


        for (TowerStudyDetail towerStudyDetail : towerStudyDetails) {
            mDao.insert(towerStudyDetail);
        }
    }

    public int getDetailListSizeByTowerId(String towerId,boolean isRTK){
        QueryBuilder builder=mDao.queryBuilder().where(TowerStudyDetailDao.Properties.TowerId.eq(towerId)
                ,TowerStudyDetailDao.Properties.IsRTK.eq(isRTK));
        return builder.list().size();
    }

    public List<TowerStudyDetail> getDetailListByTowerId(String towerId,boolean isRTK){
        QueryBuilder builder=mDao.queryBuilder().where(TowerStudyDetailDao.Properties.TowerId.eq(towerId)
                ,TowerStudyDetailDao.Properties.IsRTK.eq(isRTK));
        return builder.list();
    }

    public void getDetailListByTowerId(String towerId,AsyncOperationListener listener){
        QueryBuilder builder=mDao.queryBuilder().where(TowerStudyDetailDao.Properties.TowerId.eq(towerId));
        AsyncSession session=mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }
}
