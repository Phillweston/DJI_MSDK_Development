package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.dao.TowerLocationUpdateDao;
import com.ew.autofly.db.entity.TowerLocationUpdate;
import com.ew.autofly.entity.Tower;
import com.flycloud.autofly.base.util.DateHelperUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.Date;
import java.util.List;


public class TowerLocationUpdateDbHelper {

    private static TowerLocationUpdateDbHelper instance;

    public static synchronized TowerLocationUpdateDbHelper getInstance() {
        if (null == instance) {
            instance = new TowerLocationUpdateDbHelper();
        }
        return instance;
    }

    private TowerLocationUpdateDao mDao;

    public TowerLocationUpdateDbHelper() {
        mDao = EWApplication.getInstance().getDaoSession().getTowerLocationUpdateDao();
    }

    /**
     * 更新记录
     *
     * @param tower
     */
    public void update(Tower tower) {
        try {
            TowerLocationUpdate towerLocationUpdate = getRecord(tower);
            if (towerLocationUpdate == null) {
                towerLocationUpdate = new TowerLocationUpdate();
                towerLocationUpdate.setTowerId(tower.getTowerId());
                towerLocationUpdate.setGridLineName(tower.getGridLineName());
                towerLocationUpdate.setTowerNo(tower.getTowerNo());
                mDao.insert(towerLocationUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void updateFileCreatedTime(String gridLineName) {

        String path = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_TOWER_KML +
                File.separator + gridLineName + ".kml";
        File file = new File(path);

        if (file.exists()) {
            String fileModifyTime = DateHelperUtils.format(new Date(file.lastModified()));
            try {
                List<TowerLocationUpdate> list = getRecordListByGridLineName(gridLineName);
                if (list != null && !list.isEmpty()) {
                    for (TowerLocationUpdate towerLocationUpdate : list) {
                        towerLocationUpdate.setFileUpdateTime(fileModifyTime);
                    }
                    mDao.updateInTx(list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void deleteIfExistRecord(String gridLineName, String fileUpdateTime) {
        try {
            List<TowerLocationUpdate> list = getRecordListByGridLineName(gridLineName);
            if (list != null && !list.isEmpty() && !fileUpdateTime.equals(list.get(0).getFileUpdateTime())) {//fileUpdateTime不相同则删除
                mDao.queryBuilder().where(TowerLocationUpdateDao.Properties.GridLineName.eq(gridLineName))
                        .buildDelete().executeDeleteWithoutDetachingEntities();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取记录列表
     *
     * @param gridLineName
     * @return
     */
    public List<TowerLocationUpdate> getRecordListByGridLineName(String gridLineName) {
        try {
            QueryBuilder<TowerLocationUpdate> builder = mDao.queryBuilder()
                    .where(TowerLocationUpdateDao.Properties.GridLineName.eq(gridLineName));
            return builder.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private TowerLocationUpdate getRecord(Tower tower) {
        try {
            QueryBuilder<TowerLocationUpdate> builder = mDao.queryBuilder()
                    .where(TowerLocationUpdateDao.Properties.GridLineName.eq(tower.getGridLineName())
                            , TowerLocationUpdateDao.Properties.TowerNo.eq(tower.getTowerNo()));
            return builder.unique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
