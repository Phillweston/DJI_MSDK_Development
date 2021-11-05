package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.TowerStudyComponentDao;
import com.ew.autofly.db.entity.TowerStudyComponent;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.LogUtilsOld;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;



public class TowerStudyComponentDbHelper {

    private final static TowerStudyComponentDbHelper INSTANCE = new TowerStudyComponentDbHelper();

    private TowerStudyComponentDao mDao;

    private TowerStudyComponentDbHelper() {
        mDao = EWApplication.getInstance().getDaoSession().getTowerStudyComponentDao();
    }

    public static TowerStudyComponentDbHelper getInstance() {
        return INSTANCE;
    }

    public void save(Tower tower, List<TowerStudyComponent> list) {

        try {

            mDao.queryBuilder().where(TowerStudyComponentDao.Properties.GridLineName.eq(tower.getGridLineName()),
                    TowerStudyComponentDao.Properties.TowerNo.eq(tower.getTowerNo()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();

            mDao.insertInTx(list);
        } catch (Exception e) {
            LogUtilsOld.getInstance(EWApplication.getInstance()).e(e.toString());
            e.printStackTrace();
        }
    }

    public List<TowerStudyComponent> getTowerComponentList(Tower tower) {
        try {
            QueryBuilder<TowerStudyComponent> builder = mDao.queryBuilder()
                    .where(TowerStudyComponentDao.Properties.GridLineName.eq(tower.getGridLineName()),
                            TowerStudyComponentDao.Properties.TowerNo.eq(tower.getTowerNo()));
            return builder.list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
