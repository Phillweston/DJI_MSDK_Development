package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.TowerStudyDetailV2Dao;
import com.ew.autofly.db.entity.TowerStudyDetailV2;

import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;



public class TowerStudyDetailV2DbHelper {

    private final static TowerStudyDetailV2DbHelper INSTANCE = new TowerStudyDetailV2DbHelper();

    private TowerStudyDetailV2Dao mDao;

    private TowerStudyDetailV2DbHelper() {
        mDao = EWApplication.getInstance().getDaoSession().getTowerStudyDetailV2Dao();
    }

    public static TowerStudyDetailV2DbHelper getInstance() {
        return INSTANCE;
    }

    public void saveDetailListByTowerId(String towerId, int missionType, int missionMode,
                                        List<TowerStudyDetailV2> towerStudyDetails) {

      
        deleteDetailListByTowerId(towerId, missionType, missionMode);

      
        mDao.insertInTx(towerStudyDetails);
    }

    /**
     * 删除记录
     *
     * @param towerId
     * @param missionType
     * @param missionMode
     */
    public void deleteDetailListByTowerId(String towerId, int missionType, int missionMode) {
        mDao.queryBuilder().where(TowerStudyDetailV2Dao.Properties.TowerId.eq(towerId),
                TowerStudyDetailV2Dao.Properties.MissionType.eq(missionType),
                TowerStudyDetailV2Dao.Properties.MissionMode.eq(missionMode))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }


    public long getDetailListSizeByTowerId(String towerId, int missionMode) {
        QueryBuilder builder = mDao.queryBuilder().where(TowerStudyDetailV2Dao.Properties.TowerId.eq(towerId)
                , TowerStudyDetailV2Dao.Properties.MissionMode.eq(missionMode));
        return builder.count();
    }


    public List<TowerStudyDetailV2> getDetailListByTowerId(String towerId, int missionMode) {
        QueryBuilder<TowerStudyDetailV2> builder = mDao.queryBuilder().where(TowerStudyDetailV2Dao.Properties.TowerId.eq(towerId)
                , TowerStudyDetailV2Dao.Properties.MissionMode.eq(missionMode));
        return builder.list();
    }


    public List<TowerStudyDetailV2> getDetailListByTowerId(String towerId, int missionType, int missionMode) {
        QueryBuilder<TowerStudyDetailV2> builder = mDao.queryBuilder().where(TowerStudyDetailV2Dao.Properties.TowerId.eq(towerId)
                , TowerStudyDetailV2Dao.Properties.MissionType.eq(missionType), TowerStudyDetailV2Dao.Properties.MissionMode.eq(missionMode));
        return builder.list();
    }


    public void getDetailListByTowerId(String towerId, AsyncOperationListener listener) {
        QueryBuilder<TowerStudyDetailV2> builder = mDao.queryBuilder().where(TowerStudyDetailV2Dao.Properties.TowerId.eq(towerId));
        AsyncSession session = mDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }

}
