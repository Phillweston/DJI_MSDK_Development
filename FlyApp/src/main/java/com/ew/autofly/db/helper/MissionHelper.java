package com.ew.autofly.db.helper;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.db.dao.DaoSession;
import com.ew.autofly.db.dao.MissionBatchDao;
import com.ew.autofly.db.dao.MissionBeltOrthoPatrolDao;
import com.ew.autofly.db.dao.MissionChannelPatrolDao;
import com.ew.autofly.db.dao.MissionFinePatrolDao;
import com.ew.autofly.db.dao.MissionPointCloudV2Dao;
import com.ew.autofly.db.dao.MissionTowerDao;
import com.ew.autofly.db.dao.MissionTreeObstacleDao;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.entity.MissionBatch;
import com.ew.autofly.db.entity.MissionTower;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class MissionHelper {

    public static DaoSession sDaoSession = EWApplication.getInstance().getDaoSession();

   /* private final static MissionHelper INSTANCE = new MissionHelper();


    private MissionHelper() {

    }

    public static MissionHelper getInstance() {
        return INSTANCE;
    }
    */

    public static boolean saveMissionBath(MissionBatch missionBatch) {
        MissionBatchDao missionBatchDao = sDaoSession.getMissionBatchDao();
        long id = missionBatchDao.insert(missionBatch);
        return id != -1;
    }

    public static void deleteMissionBatch(String missionBatchId) {
        MissionBatchDao missionBatchDao = sDaoSession.getMissionBatchDao();
        MissionBatch missionBatch = loadMissionBatch(missionBatchId);
        if (missionBatch != null) {
            missionBatchDao.delete(missionBatch);
            deleteMissionByBatchId(missionBatch.getMissionType(), missionBatchId);
        }
    }

    public static void deleteMissionByBatchId(int missionType, String missionBatchId) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            dao.queryBuilder().where(getMissionDaoProperty(missionType, "MissionBatchId").eq(missionBatchId))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMission(int missionType, String missionId) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            dao.queryBuilder().where(getMissionDaoProperty(missionType, "MissionId").eq(missionId))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMissions(int missionType, List<String> selectedIdList) {
        try {
            AbstractDao dao = getMissionDao(missionType);
            dao.queryBuilder().where(getMissionDaoProperty(missionType, "MissionId")
                    .in(selectedIdList)).buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMissionBathName(String missionBatchId, String name) {
        MissionBatchDao missionBatchDao = sDaoSession.getMissionBatchDao();
        MissionBatch missionBatch = missionBatchDao.queryBuilder()
                .where(MissionBatchDao.Properties.MissionBatchId.eq(missionBatchId)).unique();
        if (missionBatch != null) {
            missionBatch.setName(name);
            missionBatchDao.update(missionBatch);
            updateMissionListName(missionBatch.getMissionType(), missionBatchId, name);
        }
    }

    public static void updateMissionListName(int missionType, String missionBatchId, String name) {
        List<MissionBase> missionBaseList = loadMissionListByBatchId(missionType, missionBatchId);
        if (missionBaseList != null) {
            for (MissionBase missionBase : missionBaseList) {
                missionBase.setName(name);
            }
            updateMissionList(missionType, missionBaseList);
        }
    }

    public static void updateMissionName(int missionType, String missionId, String name) {
        MissionBase missionBase = loadMission(missionType, missionId);
        if (missionBase != null) {
            missionBase.setName(name);
            updateMission(missionType, missionBase);
        }
    }

    public static void updateMissionStart(int missionType, String missionId, Date startTime, int startPhotoIndex) {
        MissionBase missionBase = loadMission(missionType, missionId);
        if (missionBase != null) {
            missionBase.setStartTime(startTime);
            missionBase.setStartPhotoIndex(startPhotoIndex);
            updateMission(missionType, missionBase);
        }
    }

    public static void updateMissionEnd(int missionType, String missionId, Date endTime, int endPhotoIndex) {
        MissionBase missionBase = loadMission(missionType, missionId);
        if (missionBase != null) {
            missionBase.setStatus(1);
            missionBase.setEndTime(endTime);
            missionBase.setEndPhotoIndex(endPhotoIndex);
            updateMission(missionType, missionBase);
        }
    }

    public static void updateMissionPhotoIndex(int missionType, String missionId, int photoIndex) {
        MissionBase missionBase = loadMission(missionType, missionId);
        if (missionBase != null) {
            missionBase.setEndPhotoIndex(photoIndex);
            updateMission(missionType, missionBase);
        }
    }

    public static void updateMissionBathFinish(String missionBatchId) {
        MissionBatchDao missionBatchDao = sDaoSession.getMissionBatchDao();
        MissionBatch missionBatch = missionBatchDao.queryBuilder()
                .where(MissionBatchDao.Properties.MissionBatchId.eq(missionBatchId)).unique();
        if (missionBatch != null) {
            missionBatch.setStatus(1);
            missionBatchDao.update(missionBatch);
        }
    }

    public static <T extends MissionBase> boolean saveMissionBase(T mission) {

        long id = -1;

        try {
            AbstractDao dao = getMissionDao(mission.getMissionType());
            id = dao.insert(mission);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id != -1;
    }

    public static void saveMissionTowers(List<MissionTower> missionTowers) {
        if (missionTowers != null && !missionTowers.isEmpty()) {
            MissionTowerDao missionTowerDao = sDaoSession.getMissionTowerDao();
            missionTowerDao.insertInTx(missionTowers);
        }
    }

    public static MissionBatch loadMissionBatch(String missionBatchId) {
        MissionBatchDao missionBatchDao = sDaoSession.getMissionBatchDao();
        QueryBuilder builder = missionBatchDao.queryBuilder()
                .where(MissionBatchDao.Properties.MissionBatchId.eq(missionBatchId));
        return (MissionBatch) builder.unique();
    }

    public static void loadMissionBathList(int missionType, AsyncOperationListener listener) {
        MissionBatchDao missionBatchDao = sDaoSession.getMissionBatchDao();
        QueryBuilder builder = missionBatchDao.queryBuilder()
                .where(MissionBatchDao.Properties.MissionType.eq(missionType)).orderDesc(MissionBatchDao.Properties.CreateDate);
        AsyncSession session = missionBatchDao.getSession().startAsyncSession();
        session.setListenerMainThread(listener);
        session.queryList(builder.build());
    }

    public static List<MissionBase> loadMissionList(int missionType) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            QueryBuilder builder = dao.queryBuilder().orderDesc(getMissionDaoProperty(missionType, "CreateDate"));
            return convertToMissionBase(builder.list());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<MissionBase> loadMissionListByBatchId(int missionType, String missionBatchId) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            QueryBuilder builder = dao.queryBuilder()
                    .where(getMissionDaoProperty(missionType, "MissionBatchId").eq(missionBatchId));
            return convertToMissionBase(builder.list());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MissionBase loadMission(int missionType, String missionId) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            QueryBuilder builder = dao.queryBuilder()
                    .where(getMissionDaoProperty(missionType, "MissionId").eq(missionId));
            return (MissionBase) builder.unique();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<MissionTower> loadMissionTowerList(String missionId) {
        MissionTowerDao missionTowerDao = sDaoSession.getMissionTowerDao();
        QueryBuilder<MissionTower> builder = missionTowerDao.queryBuilder()
                .where(MissionTowerDao.Properties.MissionId.eq(missionId));
        return builder.list();
    }

    public static void updateMission(int missionType, MissionBase missionBase) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            dao.update(missionBase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMissionList(int missionType, List<MissionBase> missionBaseList) {

        try {
            AbstractDao dao = getMissionDao(missionType);
            dao.updateInTx(missionBaseList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    private static <T extends MissionBase> List<MissionBase> convertToMissionBase(List<T> tList) {
        List<MissionBase> missionBaseList = new ArrayList<>();
        if (tList != null) {
            missionBaseList.addAll(tList);
        }
        return missionBaseList;
    }

  
    private static <T extends MissionBase> List<T> convertToMissionChild(List<MissionBase> tList) {
        List<T> missionChildList = new ArrayList<>();
        if (tList != null) {
            for (MissionBase missionBase : tList) {
                missionChildList.add((T) missionBase);
            }
        }
        return missionChildList;
    }

    private static AbstractDao getMissionDao(int missionType) throws Exception {
        Class cls = getMissionClass(missionType);
        return (AbstractDao) DaoSession.class.getDeclaredMethod("get" + cls.getSimpleName()).invoke(sDaoSession);
    }

    private static Property getMissionDaoProperty(int missionType, String propertyName) throws Exception {
        Property property = null;

        Class cls = getMissionClass(missionType);
        Class innerClass[] = cls.getDeclaredClasses();
        for (Class aClass : innerClass) {
            int mode = aClass.getModifiers();
            String modifier = Modifier.toString(mode);
            String name = aClass.getSimpleName();
            if (modifier.contains("static") && name.equals("Properties")) {
                Field field = aClass.getDeclaredField(propertyName);
                property = (Property) field.get(aClass);
            }
        }

        return property;
    }

    private static Class<? extends AbstractDao<?, ?>> getMissionClass(int missionType) {

        switch (missionType) {










        }

        return null;
    }
}
