package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.dao.PatrolSubJobExecuteStatusDbDao;
import com.ew.autofly.db.dao.TowerLocationUpdateDao;
import com.ew.autofly.db.entity.PatrolSubJobExecuteStatusDb;
import com.ew.autofly.entity.mission.PatrolJobStatus;
import com.umeng.commonsdk.debug.E;


public class PatrolJobDbHelper {

    private static PatrolJobDbHelper INSTANCE;

    private PatrolSubJobExecuteStatusDbDao mJobExecuteStatusDb;

    public PatrolJobDbHelper() {
        mJobExecuteStatusDb = EWApplication.getInstance().getDaoSession().getPatrolSubJobExecuteStatusDbDao();
    }

    public static PatrolJobDbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (PatrolJobDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PatrolJobDbHelper();
                }
            }
        }
        return INSTANCE;
    }

    public boolean isPatrolSubJobFinish(long missionId) {
        boolean flag = false;
        try {
            PatrolSubJobExecuteStatusDb jobExecuteStatusDb = mJobExecuteStatusDb.queryBuilder()
                    .where(PatrolSubJobExecuteStatusDbDao.Properties.MissionId.eq(missionId)).unique();
            flag = jobExecuteStatusDb != null && jobExecuteStatusDb.getStatus() == PatrolJobStatus.STATUS_FINISH;
        } catch (Exception e) {

        }
        return flag;
    }

    public void savePatrolSubJobFinish(long missionId) {
       savePatrolSubJobStatus(missionId,PatrolJobStatus.STATUS_FINISH);
    }

    public void removePatrolSubJobStatus(long missionId){
        try {
            mJobExecuteStatusDb.queryBuilder().where(PatrolSubJobExecuteStatusDbDao.Properties.MissionId.eq(missionId))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
        }catch (Exception e){

        }
    }

    public void savePatrolSubJobStatus(long missionId,int status){
        try {
            PatrolSubJobExecuteStatusDb jobExecuteStatusDb = mJobExecuteStatusDb.queryBuilder()
                    .where(PatrolSubJobExecuteStatusDbDao.Properties.MissionId.eq(missionId)).unique();
            if (jobExecuteStatusDb != null) {
                jobExecuteStatusDb.setStatus(status);
                mJobExecuteStatusDb.update(jobExecuteStatusDb);
            } else {
                PatrolSubJobExecuteStatusDb statusDb = new PatrolSubJobExecuteStatusDb();
                statusDb.setMissionId(missionId);
                statusDb.setStatus(status);
                mJobExecuteStatusDb.insert(statusDb);
            }
        } catch (Exception e) {

        }
    }
}
