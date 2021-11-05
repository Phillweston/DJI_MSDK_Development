package com.ew.autofly.db;




import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ew.autofly.db.dao.DaoMaster;
import com.ew.autofly.db.dao.EPointDao;
import com.ew.autofly.db.dao.FlightRecordDao;
import com.ew.autofly.db.dao.FlightRecordDetailDao;
import com.ew.autofly.db.dao.MissionBatchDao;
import com.ew.autofly.db.dao.MissionBeltOrthoPatrolDao;
import com.ew.autofly.db.dao.MissionChannelPatrolDao;
import com.ew.autofly.db.dao.MissionFinePatrolDao;
import com.ew.autofly.db.dao.MissionPhotoDao;
import com.ew.autofly.db.dao.MissionPointCloudV2Dao;
import com.ew.autofly.db.dao.MissionTowerDao;
import com.ew.autofly.db.dao.MissionTreeObstacleDao;
import com.ew.autofly.db.dao.MultiSpectralMissionConfigDao;
import com.ew.autofly.db.dao.PatrolSubJobExecuteStatusDbDao;
import com.ew.autofly.db.dao.TowerGridLineDao;
import com.ew.autofly.db.dao.TowerLocationUpdateDao;
import com.ew.autofly.db.dao.TowerStudyComponentDao;
import com.ew.autofly.db.dao.TowerStudyDetailDao;
import com.ew.autofly.db.dao.TowerStudyDetailV2Dao;
import com.ew.autofly.db.dao.TowerStudyRecordDao;

import org.greenrobot.greendao.database.Database;


public class DBOpenHelper extends DaoMaster.OpenHelper {
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        migrateDB(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        migrateDB(db);
    }

    /**
     * 数据库数据备份
     *
     * @param db
     */
    private void migrateDB(SQLiteDatabase db) {

        DBMigrationHelper migratorHelper = new DBMigrationHelper();

      
      
        migratorHelper.onUpgrade(db,
                EPointDao.class,
                TowerGridLineDao.class,
                TowerStudyDetailDao.class,
                TowerStudyComponentDao.class,
                TowerStudyDetailV2Dao.class,
                TowerStudyRecordDao.class,
                MissionPhotoDao.class,
                FlightRecordDao.class,
                FlightRecordDetailDao.class,
                MissionBatchDao.class,
                MissionTreeObstacleDao.class,
                MissionChannelPatrolDao.class,
                MissionPointCloudV2Dao.class,
                MissionBeltOrthoPatrolDao.class,
                MissionFinePatrolDao.class,
                MultiSpectralMissionConfigDao.class,
                MissionTowerDao.class,
                TowerLocationUpdateDao.class,
                PatrolSubJobExecuteStatusDbDao.class);
    }
}