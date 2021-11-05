package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.MissionPhotoDao;
import com.ew.autofly.db.dao.MultiSpectralMissionConfigDao;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.db.entity.MultiSpectralMissionConfig;
import com.ew.autofly.utils.LogUtilsOld;

import org.greenrobot.greendao.query.QueryBuilder;



public class MultiSpectralMissionConfigDbHelper {

    private MultiSpectralMissionConfigDao mDao;

    private static final MultiSpectralMissionConfigDbHelper INSTANCE=new MultiSpectralMissionConfigDbHelper();

    private MultiSpectralMissionConfigDbHelper(){
        mDao= EWApplication.getInstance().getDaoSession().getMultiSpectralMissionConfigDao();
    }

    public static MultiSpectralMissionConfigDbHelper getInstance(){
        return INSTANCE;
    }

    public boolean save(MultiSpectralMissionConfig config) {
        try {
            long result = mDao.insert(config);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).e("保存多光谱任务配置失败" + e);

        }

        return false;
    }

    public MultiSpectralMissionConfig getConfigByMissionId(String missionId){
        QueryBuilder<MultiSpectralMissionConfig> queryBuilder = mDao.queryBuilder().where(MultiSpectralMissionConfigDao.Properties.MissionId.eq(missionId));
        return queryBuilder.build().unique();
    }
}
