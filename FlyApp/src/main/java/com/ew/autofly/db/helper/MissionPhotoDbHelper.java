package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.MissionPhotoDao;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.utils.LogUtilsOld;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;



public class MissionPhotoDbHelper{

    private static MissionPhotoDbHelper INSTANCE;

    private MissionPhotoDao mMissionPhotoDao;

    public MissionPhotoDbHelper() {
        mMissionPhotoDao = EWApplication.getInstance().getDaoSession().getMissionPhotoDao();
    }

    public static MissionPhotoDbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (MissionPhotoDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MissionPhotoDbHelper();
                }
            }
        }
        return INSTANCE;
    }


    public void save(MissionPhoto missionPhoto){
        try {
            long id=mMissionPhotoDao.insert(missionPhoto);
            LogUtilsOld.getInstance(EWApplication.getInstance()).i("插入照片成功：id "+id+" missionId: "+missionPhoto.getMissionId());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).i("插入照片失败 ：missionId:" + missionPhoto.getMissionId()+" errorMsg:"+ e.toString());
        }
    }

    public MissionPhoto getMissionPhotoById(String id) {
        QueryBuilder<MissionPhoto> queryBuilder = mMissionPhotoDao.queryBuilder().where(MissionPhotoDao.Properties.Id.eq(id));
        MissionPhoto missionPhoto = queryBuilder.build().unique();
        return missionPhoto;
    }


    public void updateMissionFile(String id, String filePath) {
        try {

            MissionPhoto missionPhoto = getMissionPhotoById(id);
            missionPhoto.setPhotoPath(filePath);
            mMissionPhotoDao.update(missionPhoto);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).i("更新照片缩略图路径失败" + e.toString()  + id +"      " + filePath);
        }
    }

    public boolean updateMissionBigFile(String id, String filePath) {
        boolean flag = false;
        try {
            MissionPhoto missionPhoto = getMissionPhotoById(id);
            missionPhoto.setBigPhotoPath(filePath);
            mMissionPhotoDao.update(missionPhoto);
            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            LogUtilsOld.getInstance(EWApplication.getInstance()).i("更新照片大图路径失败" + e.toString());
        }
        return flag;
    }

    public List<MissionPhoto> getPhotoByMissionId(String missionId) {
        QueryBuilder<MissionPhoto> qb = mMissionPhotoDao.queryBuilder().where(MissionPhotoDao.Properties.MissionId.eq(missionId))
                .orderAsc(MissionPhotoDao.Properties.PhotoIndex);
        return qb.list();
    }
}
