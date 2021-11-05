package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.TowerStudyRecordDao;
import com.ew.autofly.db.entity.TowerStudyDetailV2;
import com.ew.autofly.db.entity.TowerStudyRecord;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;



public class TowerStudyRecordDbHelper {

    private final static TowerStudyRecordDbHelper INSTANCE = new TowerStudyRecordDbHelper();

    private TowerStudyRecordDao mDao;

    private TowerStudyRecordDbHelper() {
        mDao = EWApplication.getInstance().getDaoSession().getTowerStudyRecordDao();
    }

    public static TowerStudyRecordDbHelper getInstance() {
        return INSTANCE;
    }

    public TowerStudyRecord getStudyRecord(String towerId, int missionType, int missionMode) {

        QueryBuilder<TowerStudyRecord> builder = mDao.queryBuilder().where(TowerStudyRecordDao.Properties.TowerId.eq(towerId),
                TowerStudyRecordDao.Properties.MissionType.eq(missionType), TowerStudyRecordDao.Properties.MissionMode.eq(missionMode));
        List<TowerStudyRecord> studyRecords = builder.list();

        if (studyRecords != null && !studyRecords.isEmpty()) {
            return studyRecords.get(0);
        }

        return null;
    }

    public int getRecordStatus(String towerId, int missionType, int missionMode) {

        TowerStudyRecord studyRecord = getStudyRecord(towerId, missionType, missionMode);
        if (studyRecord != null) {
            return studyRecord.getRecordStatus();
        }
        return -1;
    }


    public List<TowerStudyDetailV2> getDetailListByTowerId(String towerId, int missionType, int missionMode) {
        return TowerStudyDetailV2DbHelper.getInstance().getDetailListByTowerId(towerId, missionType, missionMode);
    }

    
    public void saveRecord(TowerStudyRecord studyRecord, List<TowerStudyDetailV2> towerStudyDetails) {


        deleteRecord(studyRecord.getTowerId(), studyRecord.getMissionType(),
                studyRecord.getMissionMode());

        mDao.insert(studyRecord);

        TowerStudyDetailV2DbHelper.getInstance()
                .saveDetailListByTowerId(studyRecord.getTowerId(), studyRecord.getMissionType(),
                        studyRecord.getMissionMode(), towerStudyDetails);
    }

    /**
     * 删除记录
     *
     * @param towerId
     * @param missionType
     * @param missionMode
     */
    public void deleteRecord(String towerId, int missionType, int missionMode) {
        mDao.queryBuilder().where(TowerStudyRecordDao.Properties.TowerId.eq(towerId),
                TowerStudyRecordDao.Properties.MissionType.eq(missionType),
                TowerStudyRecordDao.Properties.MissionMode.eq(missionMode))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }
}
