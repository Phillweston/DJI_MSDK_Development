package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask;


/** 
 * DAO for table "FLY_TASK".
*/
public class FlyTaskDao extends AbstractDao<FlyTask, String> {

    public static final String TABLENAME = "FLY_TASK";

    /**
     * Properties of entity FlyTask.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property TaskName = new Property(1, String.class, "taskName", false, "TASK_NAME");
        public final static Property Image = new Property(2, String.class, "image", false, "IMAGE");
        public final static Property CreateTime = new Property(3, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property UpdateTime = new Property(4, java.util.Date.class, "updateTime", false, "UPDATE_TIME");
        public final static Property CompletePointSize = new Property(5, int.class, "completePointSize", false, "COMPLETE_POINT_SIZE");
        public final static Property CurrentStart = new Property(6, int.class, "currentStart", false, "CURRENT_START");
        public final static Property CurrentEnd = new Property(7, int.class, "currentEnd", false, "CURRENT_END");
        public final static Property TaskStatus = new Property(8, int.class, "taskStatus", false, "TASK_STATUS");
        public final static Property CameraId = new Property(9, String.class, "cameraId", false, "CAMERA_ID");
        public final static Property CameraDirection = new Property(10, int.class, "cameraDirection", false, "CAMERA_DIRECTION");
        public final static Property TaskType = new Property(11, int.class, "taskType", false, "TASK_TYPE");
        public final static Property PhotoNum = new Property(12, int.class, "photoNum", false, "PHOTO_NUM");
        public final static Property PhotoModel = new Property(13, int.class, "photoModel", false, "PHOTO_MODEL");
        public final static Property Hover = new Property(14, int.class, "hover", false, "HOVER");
        public final static Property Speed = new Property(15, float.class, "speed", false, "SPEED");
        public final static Property AutoFlightSpeed = new Property(16, float.class, "autoFlightSpeed", false, "AUTO_FLIGHT_SPEED");
        public final static Property Radius = new Property(17, float.class, "radius", false, "RADIUS");
        public final static Property DiffHeight = new Property(18, float.class, "diffHeight", false, "DIFF_HEIGHT");
        public final static Property FlyHeight = new Property(19, float.class, "flyHeight", false, "FLY_HEIGHT");
        public final static Property GoHomeHeight = new Property(20, int.class, "GoHomeHeight", false, "GO_HOME_HEIGHT");
        public final static Property AdjacentOverlapping = new Property(21, float.class, "adjacentOverlapping", false, "ADJACENT_OVERLAPPING");
        public final static Property ParallelOverlapping = new Property(22, float.class, "parallelOverlapping", false, "PARALLEL_OVERLAPPING");
        public final static Property AirWayAngle = new Property(23, double.class, "airWayAngle", false, "AIR_WAY_ANGLE");
        public final static Property RadiusAngle = new Property(24, int.class, "radiusAngle", false, "RADIUS_ANGLE");
        public final static Property FinishAction = new Property(25, int.class, "finishAction", false, "FINISH_ACTION");
        public final static Property Interval = new Property(26, int.class, "interval", false, "INTERVAL");
        public final static Property Gsd = new Property(27, float.class, "gsd", false, "GSD");
        public final static Property AreaASL = new Property(28, int.class, "areaASL", false, "AREA_ASL");
        public final static Property HomeASL = new Property(29, int.class, "homeASL", false, "HOME_ASL");
        public final static Property Pitch = new Property(30, int.class, "pitch", false, "PITCH");
        public final static Property PPitchSetting = new Property(31, int.class, "pPitchSetting", false, "P_PITCH_SETTING");
        public final static Property Yaw = new Property(32, int.class, "yaw", false, "YAW");
        public final static Property PYawSetting = new Property(33, int.class, "pYawSetting", false, "P_YAW_SETTING");
        public final static Property IsThridCamera = new Property(34, int.class, "isThridCamera", false, "IS_THRID_CAMERA");
        public final static Property IsSrtm = new Property(35, int.class, "isSrtm", false, "IS_SRTM");
        public final static Property SrtmDataFile = new Property(36, String.class, "srtmDataFile", false, "SRTM_DATA_FILE");
        public final static Property PId = new Property(37, String.class, "pId", false, "P_ID");
        public final static Property Address = new Property(38, String.class, "address", false, "ADDRESS");
        public final static Property Save = new Property(39, boolean.class, "save", false, "SAVE");
        public final static Property FollowSpeed = new Property(40, boolean.class, "followSpeed", false, "FOLLOW_SPEED");
        public final static Property FollowHeight = new Property(41, boolean.class, "followHeight", false, "FOLLOW_HEIGHT");
        public final static Property IsGimbalPitchRotationEnabled = new Property(42, boolean.class, "isGimbalPitchRotationEnabled", false, "IS_GIMBAL_PITCH_ROTATION_ENABLED");
        public final static Property Latitude = new Property(43, double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(44, double.class, "longitude", false, "LONGITUDE");
        public final static Property BasePlaneHeight = new Property(45, float.class, "basePlaneHeight", false, "BASE_PLANE_HEIGHT");
        public final static Property Model = new Property(46, int.class, "model", false, "MODEL");
        public final static Property LineSpace = new Property(47, float.class, "lineSpace", false, "LINE_SPACE");
        public final static Property PointSpace = new Property(48, float.class, "pointSpace", false, "POINT_SPACE");
        public final static Property PointSort = new Property(49, double.class, "pointSort", false, "POINT_SORT");
        public final static Property CirclePointCount = new Property(50, int.class, "circlePointCount", false, "CIRCLE_POINT_COUNT");
    }


    public FlyTaskDao(DaoConfig config) {
        super(config);
    }
    
    public FlyTaskDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FLY_TASK\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"TASK_NAME\" TEXT NOT NULL ," + // 1: taskName
                "\"IMAGE\" TEXT," + // 2: image
                "\"CREATE_TIME\" INTEGER," + // 3: createTime
                "\"UPDATE_TIME\" INTEGER," + // 4: updateTime
                "\"COMPLETE_POINT_SIZE\" INTEGER NOT NULL ," + // 5: completePointSize
                "\"CURRENT_START\" INTEGER NOT NULL ," + // 6: currentStart
                "\"CURRENT_END\" INTEGER NOT NULL ," + // 7: currentEnd
                "\"TASK_STATUS\" INTEGER NOT NULL ," + // 8: taskStatus
                "\"CAMERA_ID\" TEXT," + // 9: cameraId
                "\"CAMERA_DIRECTION\" INTEGER NOT NULL ," + // 10: cameraDirection
                "\"TASK_TYPE\" INTEGER NOT NULL ," + // 11: taskType
                "\"PHOTO_NUM\" INTEGER NOT NULL ," + // 12: photoNum
                "\"PHOTO_MODEL\" INTEGER NOT NULL ," + // 13: photoModel
                "\"HOVER\" INTEGER NOT NULL ," + // 14: hover
                "\"SPEED\" REAL NOT NULL ," + // 15: speed
                "\"AUTO_FLIGHT_SPEED\" REAL NOT NULL ," + // 16: autoFlightSpeed
                "\"RADIUS\" REAL NOT NULL ," + // 17: radius
                "\"DIFF_HEIGHT\" REAL NOT NULL ," + // 18: diffHeight
                "\"FLY_HEIGHT\" REAL NOT NULL ," + // 19: flyHeight
                "\"GO_HOME_HEIGHT\" INTEGER NOT NULL ," + // 20: GoHomeHeight
                "\"ADJACENT_OVERLAPPING\" REAL NOT NULL ," + // 21: adjacentOverlapping
                "\"PARALLEL_OVERLAPPING\" REAL NOT NULL ," + // 22: parallelOverlapping
                "\"AIR_WAY_ANGLE\" REAL NOT NULL ," + // 23: airWayAngle
                "\"RADIUS_ANGLE\" INTEGER NOT NULL ," + // 24: radiusAngle
                "\"FINISH_ACTION\" INTEGER NOT NULL ," + // 25: finishAction
                "\"INTERVAL\" INTEGER NOT NULL ," + // 26: interval
                "\"GSD\" REAL NOT NULL ," + // 27: gsd
                "\"AREA_ASL\" INTEGER NOT NULL ," + // 28: areaASL
                "\"HOME_ASL\" INTEGER NOT NULL ," + // 29: homeASL
                "\"PITCH\" INTEGER NOT NULL ," + // 30: pitch
                "\"P_PITCH_SETTING\" INTEGER NOT NULL ," + // 31: pPitchSetting
                "\"YAW\" INTEGER NOT NULL ," + // 32: yaw
                "\"P_YAW_SETTING\" INTEGER NOT NULL ," + // 33: pYawSetting
                "\"IS_THRID_CAMERA\" INTEGER NOT NULL ," + // 34: isThridCamera
                "\"IS_SRTM\" INTEGER NOT NULL ," + // 35: isSrtm
                "\"SRTM_DATA_FILE\" TEXT," + // 36: srtmDataFile
                "\"P_ID\" TEXT," + // 37: pId
                "\"ADDRESS\" TEXT," + // 38: address
                "\"SAVE\" INTEGER NOT NULL ," + // 39: save
                "\"FOLLOW_SPEED\" INTEGER NOT NULL ," + // 40: followSpeed
                "\"FOLLOW_HEIGHT\" INTEGER NOT NULL ," + // 41: followHeight
                "\"IS_GIMBAL_PITCH_ROTATION_ENABLED\" INTEGER NOT NULL ," + // 42: isGimbalPitchRotationEnabled
                "\"LATITUDE\" REAL NOT NULL ," + // 43: latitude
                "\"LONGITUDE\" REAL NOT NULL ," + // 44: longitude
                "\"BASE_PLANE_HEIGHT\" REAL NOT NULL ," + // 45: basePlaneHeight
                "\"MODEL\" INTEGER NOT NULL ," + // 46: model
                "\"LINE_SPACE\" REAL NOT NULL ," + // 47: lineSpace
                "\"POINT_SPACE\" REAL NOT NULL ," + // 48: pointSpace
                "\"POINT_SORT\" REAL NOT NULL ," + // 49: pointSort
                "\"CIRCLE_POINT_COUNT\" INTEGER NOT NULL );");
      
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_FLY_TASK_ID_CREATE_TIME_DESC ON \"FLY_TASK\"" +
                " (\"ID\" ASC,\"CREATE_TIME\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FLY_TASK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FlyTask entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindString(2, entity.getTaskName());
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(3, image);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(4, createTime.getTime());
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(5, updateTime.getTime());
        }
        stmt.bindLong(6, entity.getCompletePointSize());
        stmt.bindLong(7, entity.getCurrentStart());
        stmt.bindLong(8, entity.getCurrentEnd());
        stmt.bindLong(9, entity.getTaskStatus());
 
        String cameraId = entity.getCameraId();
        if (cameraId != null) {
            stmt.bindString(10, cameraId);
        }
        stmt.bindLong(11, entity.getCameraDirection());
        stmt.bindLong(12, entity.getTaskType());
        stmt.bindLong(13, entity.getPhotoNum());
        stmt.bindLong(14, entity.getPhotoModel());
        stmt.bindLong(15, entity.getHover());
        stmt.bindDouble(16, entity.getSpeed());
        stmt.bindDouble(17, entity.getAutoFlightSpeed());
        stmt.bindDouble(18, entity.getRadius());
        stmt.bindDouble(19, entity.getDiffHeight());
        stmt.bindDouble(20, entity.getFlyHeight());
        stmt.bindLong(21, entity.getGoHomeHeight());
        stmt.bindDouble(22, entity.getAdjacentOverlapping());
        stmt.bindDouble(23, entity.getParallelOverlapping());
        stmt.bindDouble(24, entity.getAirWayAngle());
        stmt.bindLong(25, entity.getRadiusAngle());
        stmt.bindLong(26, entity.getFinishAction());
        stmt.bindLong(27, entity.getInterval());
        stmt.bindDouble(28, entity.getGsd());
        stmt.bindLong(29, entity.getAreaASL());
        stmt.bindLong(30, entity.getHomeASL());
        stmt.bindLong(31, entity.getPitch());
        stmt.bindLong(32, entity.getPPitchSetting());
        stmt.bindLong(33, entity.getYaw());
        stmt.bindLong(34, entity.getPYawSetting());
        stmt.bindLong(35, entity.getIsThridCamera());
        stmt.bindLong(36, entity.getIsSrtm());
 
        String srtmDataFile = entity.getSrtmDataFile();
        if (srtmDataFile != null) {
            stmt.bindString(37, srtmDataFile);
        }
 
        String pId = entity.getPId();
        if (pId != null) {
            stmt.bindString(38, pId);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(39, address);
        }
        stmt.bindLong(40, entity.getSave() ? 1L: 0L);
        stmt.bindLong(41, entity.getFollowSpeed() ? 1L: 0L);
        stmt.bindLong(42, entity.getFollowHeight() ? 1L: 0L);
        stmt.bindLong(43, entity.getIsGimbalPitchRotationEnabled() ? 1L: 0L);
        stmt.bindDouble(44, entity.getLatitude());
        stmt.bindDouble(45, entity.getLongitude());
        stmt.bindDouble(46, entity.getBasePlaneHeight());
        stmt.bindLong(47, entity.getModel());
        stmt.bindDouble(48, entity.getLineSpace());
        stmt.bindDouble(49, entity.getPointSpace());
        stmt.bindDouble(50, entity.getPointSort());
        stmt.bindLong(51, entity.getCirclePointCount());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FlyTask entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindString(2, entity.getTaskName());
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(3, image);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(4, createTime.getTime());
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(5, updateTime.getTime());
        }
        stmt.bindLong(6, entity.getCompletePointSize());
        stmt.bindLong(7, entity.getCurrentStart());
        stmt.bindLong(8, entity.getCurrentEnd());
        stmt.bindLong(9, entity.getTaskStatus());
 
        String cameraId = entity.getCameraId();
        if (cameraId != null) {
            stmt.bindString(10, cameraId);
        }
        stmt.bindLong(11, entity.getCameraDirection());
        stmt.bindLong(12, entity.getTaskType());
        stmt.bindLong(13, entity.getPhotoNum());
        stmt.bindLong(14, entity.getPhotoModel());
        stmt.bindLong(15, entity.getHover());
        stmt.bindDouble(16, entity.getSpeed());
        stmt.bindDouble(17, entity.getAutoFlightSpeed());
        stmt.bindDouble(18, entity.getRadius());
        stmt.bindDouble(19, entity.getDiffHeight());
        stmt.bindDouble(20, entity.getFlyHeight());
        stmt.bindLong(21, entity.getGoHomeHeight());
        stmt.bindDouble(22, entity.getAdjacentOverlapping());
        stmt.bindDouble(23, entity.getParallelOverlapping());
        stmt.bindDouble(24, entity.getAirWayAngle());
        stmt.bindLong(25, entity.getRadiusAngle());
        stmt.bindLong(26, entity.getFinishAction());
        stmt.bindLong(27, entity.getInterval());
        stmt.bindDouble(28, entity.getGsd());
        stmt.bindLong(29, entity.getAreaASL());
        stmt.bindLong(30, entity.getHomeASL());
        stmt.bindLong(31, entity.getPitch());
        stmt.bindLong(32, entity.getPPitchSetting());
        stmt.bindLong(33, entity.getYaw());
        stmt.bindLong(34, entity.getPYawSetting());
        stmt.bindLong(35, entity.getIsThridCamera());
        stmt.bindLong(36, entity.getIsSrtm());
 
        String srtmDataFile = entity.getSrtmDataFile();
        if (srtmDataFile != null) {
            stmt.bindString(37, srtmDataFile);
        }
 
        String pId = entity.getPId();
        if (pId != null) {
            stmt.bindString(38, pId);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(39, address);
        }
        stmt.bindLong(40, entity.getSave() ? 1L: 0L);
        stmt.bindLong(41, entity.getFollowSpeed() ? 1L: 0L);
        stmt.bindLong(42, entity.getFollowHeight() ? 1L: 0L);
        stmt.bindLong(43, entity.getIsGimbalPitchRotationEnabled() ? 1L: 0L);
        stmt.bindDouble(44, entity.getLatitude());
        stmt.bindDouble(45, entity.getLongitude());
        stmt.bindDouble(46, entity.getBasePlaneHeight());
        stmt.bindLong(47, entity.getModel());
        stmt.bindDouble(48, entity.getLineSpace());
        stmt.bindDouble(49, entity.getPointSpace());
        stmt.bindDouble(50, entity.getPointSort());
        stmt.bindLong(51, entity.getCirclePointCount());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public FlyTask readEntity(Cursor cursor, int offset) {
        FlyTask entity = new FlyTask( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.getString(offset + 1), // taskName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // image
            cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)), // createTime
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // updateTime
            cursor.getInt(offset + 5), // completePointSize
            cursor.getInt(offset + 6), // currentStart
            cursor.getInt(offset + 7), // currentEnd
            cursor.getInt(offset + 8), // taskStatus
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // cameraId
            cursor.getInt(offset + 10), // cameraDirection
            cursor.getInt(offset + 11), // taskType
            cursor.getInt(offset + 12), // photoNum
            cursor.getInt(offset + 13), // photoModel
            cursor.getInt(offset + 14), // hover
            cursor.getFloat(offset + 15), // speed
            cursor.getFloat(offset + 16), // autoFlightSpeed
            cursor.getFloat(offset + 17), // radius
            cursor.getFloat(offset + 18), // diffHeight
            cursor.getFloat(offset + 19), // flyHeight
            cursor.getInt(offset + 20), // GoHomeHeight
            cursor.getFloat(offset + 21), // adjacentOverlapping
            cursor.getFloat(offset + 22), // parallelOverlapping
            cursor.getDouble(offset + 23), // airWayAngle
            cursor.getInt(offset + 24), // radiusAngle
            cursor.getInt(offset + 25), // finishAction
            cursor.getInt(offset + 26), // interval
            cursor.getFloat(offset + 27), // gsd
            cursor.getInt(offset + 28), // areaASL
            cursor.getInt(offset + 29), // homeASL
            cursor.getInt(offset + 30), // pitch
            cursor.getInt(offset + 31), // pPitchSetting
            cursor.getInt(offset + 32), // yaw
            cursor.getInt(offset + 33), // pYawSetting
            cursor.getInt(offset + 34), // isThridCamera
            cursor.getInt(offset + 35), // isSrtm
            cursor.isNull(offset + 36) ? null : cursor.getString(offset + 36), // srtmDataFile
            cursor.isNull(offset + 37) ? null : cursor.getString(offset + 37), // pId
            cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38), // address
            cursor.getShort(offset + 39) != 0, // save
            cursor.getShort(offset + 40) != 0, // followSpeed
            cursor.getShort(offset + 41) != 0, // followHeight
            cursor.getShort(offset + 42) != 0, // isGimbalPitchRotationEnabled
            cursor.getDouble(offset + 43), // latitude
            cursor.getDouble(offset + 44), // longitude
            cursor.getFloat(offset + 45), // basePlaneHeight
            cursor.getInt(offset + 46), // model
            cursor.getFloat(offset + 47), // lineSpace
            cursor.getFloat(offset + 48), // pointSpace
            cursor.getDouble(offset + 49), // pointSort
            cursor.getInt(offset + 50) // circlePointCount
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FlyTask entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTaskName(cursor.getString(offset + 1));
        entity.setImage(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreateTime(cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)));
        entity.setUpdateTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setCompletePointSize(cursor.getInt(offset + 5));
        entity.setCurrentStart(cursor.getInt(offset + 6));
        entity.setCurrentEnd(cursor.getInt(offset + 7));
        entity.setTaskStatus(cursor.getInt(offset + 8));
        entity.setCameraId(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCameraDirection(cursor.getInt(offset + 10));
        entity.setTaskType(cursor.getInt(offset + 11));
        entity.setPhotoNum(cursor.getInt(offset + 12));
        entity.setPhotoModel(cursor.getInt(offset + 13));
        entity.setHover(cursor.getInt(offset + 14));
        entity.setSpeed(cursor.getFloat(offset + 15));
        entity.setAutoFlightSpeed(cursor.getFloat(offset + 16));
        entity.setRadius(cursor.getFloat(offset + 17));
        entity.setDiffHeight(cursor.getFloat(offset + 18));
        entity.setFlyHeight(cursor.getFloat(offset + 19));
        entity.setGoHomeHeight(cursor.getInt(offset + 20));
        entity.setAdjacentOverlapping(cursor.getFloat(offset + 21));
        entity.setParallelOverlapping(cursor.getFloat(offset + 22));
        entity.setAirWayAngle(cursor.getDouble(offset + 23));
        entity.setRadiusAngle(cursor.getInt(offset + 24));
        entity.setFinishAction(cursor.getInt(offset + 25));
        entity.setInterval(cursor.getInt(offset + 26));
        entity.setGsd(cursor.getFloat(offset + 27));
        entity.setAreaASL(cursor.getInt(offset + 28));
        entity.setHomeASL(cursor.getInt(offset + 29));
        entity.setPitch(cursor.getInt(offset + 30));
        entity.setPPitchSetting(cursor.getInt(offset + 31));
        entity.setYaw(cursor.getInt(offset + 32));
        entity.setPYawSetting(cursor.getInt(offset + 33));
        entity.setIsThridCamera(cursor.getInt(offset + 34));
        entity.setIsSrtm(cursor.getInt(offset + 35));
        entity.setSrtmDataFile(cursor.isNull(offset + 36) ? null : cursor.getString(offset + 36));
        entity.setPId(cursor.isNull(offset + 37) ? null : cursor.getString(offset + 37));
        entity.setAddress(cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38));
        entity.setSave(cursor.getShort(offset + 39) != 0);
        entity.setFollowSpeed(cursor.getShort(offset + 40) != 0);
        entity.setFollowHeight(cursor.getShort(offset + 41) != 0);
        entity.setIsGimbalPitchRotationEnabled(cursor.getShort(offset + 42) != 0);
        entity.setLatitude(cursor.getDouble(offset + 43));
        entity.setLongitude(cursor.getDouble(offset + 44));
        entity.setBasePlaneHeight(cursor.getFloat(offset + 45));
        entity.setModel(cursor.getInt(offset + 46));
        entity.setLineSpace(cursor.getFloat(offset + 47));
        entity.setPointSpace(cursor.getFloat(offset + 48));
        entity.setPointSort(cursor.getDouble(offset + 49));
        entity.setCirclePointCount(cursor.getInt(offset + 50));
     }
    
    @Override
    protected final String updateKeyAfterInsert(FlyTask entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(FlyTask entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FlyTask entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
