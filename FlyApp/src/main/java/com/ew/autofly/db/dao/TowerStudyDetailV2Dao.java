package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.entity.TowerStudyDetailV2;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_Tower_StudyDetail_v2".
*/
public class TowerStudyDetailV2Dao extends AbstractDao<TowerStudyDetailV2, Long> {

    public static final String TABLENAME = "t_Tower_StudyDetail_v2";

    /**
     * Properties of entity TowerStudyDetailV2.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TowerId = new Property(1, String.class, "towerId", false, "TOWER_ID");
        public final static Property HomeLocationLatitude = new Property(2, double.class, "homeLocationLatitude", false, "HOME_LOCATION_LATITUDE");
        public final static Property HomeLocationLongitude = new Property(3, double.class, "homeLocationLongitude", false, "HOME_LOCATION_LONGITUDE");
        public final static Property AircraftLocationLatitude = new Property(4, double.class, "aircraftLocationLatitude", false, "AIRCRAFT_LOCATION_LATITUDE");
        public final static Property AircraftLocationLongitude = new Property(5, double.class, "aircraftLocationLongitude", false, "AIRCRAFT_LOCATION_LONGITUDE");
        public final static Property AircraftLocationAltitude = new Property(6, float.class, "aircraftLocationAltitude", false, "AIRCRAFT_LOCATION_ALTITUDE");
        public final static Property BaseLocationLatitude = new Property(7, double.class, "baseLocationLatitude", false, "BASE_LOCATION_LATITUDE");
        public final static Property BaseLocationLongitude = new Property(8, double.class, "baseLocationLongitude", false, "BASE_LOCATION_LONGITUDE");
        public final static Property BaseLocationAltitude = new Property(9, float.class, "baseLocationAltitude", false, "BASE_LOCATION_ALTITUDE");
        public final static Property AircraftYaw = new Property(10, double.class, "aircraftYaw", false, "AIRCRAFT_YAW");
        public final static Property AircraftPitch = new Property(11, float.class, "aircraftPitch", false, "AIRCRAFT_PITCH");
        public final static Property AircraftRoll = new Property(12, float.class, "aircraftRoll", false, "AIRCRAFT_ROLL");
        public final static Property GimbalYaw = new Property(13, double.class, "gimbalYaw", false, "GIMBAL_YAW");
        public final static Property GimbalPitch = new Property(14, float.class, "gimbalPitch", false, "GIMBAL_PITCH");
        public final static Property GimbalRoll = new Property(15, float.class, "gimbalRoll", false, "GIMBAL_ROLL");
        public final static Property MissionType = new Property(16, int.class, "missionType", false, "MISSION_TYPE");
        public final static Property MissionMode = new Property(17, int.class, "missionMode", false, "MISSION_MODE");
        public final static Property WaypointType = new Property(18, int.class, "waypointType", false, "WAYPOINT_TYPE");
        public final static Property CameraName = new Property(19, String.class, "cameraName", false, "CAMERA_NAME");
        public final static Property AircraftName = new Property(20, String.class, "aircraftName", false, "AIRCRAFT_NAME");
        public final static Property CreatedTime = new Property(21, java.util.Date.class, "createdTime", false, "CREATED_TIME");
    }


    public TowerStudyDetailV2Dao(DaoConfig config) {
        super(config);
    }
    
    public TowerStudyDetailV2Dao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_Tower_StudyDetail_v2\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TOWER_ID\" TEXT," + // 1: towerId
                "\"HOME_LOCATION_LATITUDE\" REAL NOT NULL ," + // 2: homeLocationLatitude
                "\"HOME_LOCATION_LONGITUDE\" REAL NOT NULL ," + // 3: homeLocationLongitude
                "\"AIRCRAFT_LOCATION_LATITUDE\" REAL NOT NULL ," + // 4: aircraftLocationLatitude
                "\"AIRCRAFT_LOCATION_LONGITUDE\" REAL NOT NULL ," + // 5: aircraftLocationLongitude
                "\"AIRCRAFT_LOCATION_ALTITUDE\" REAL NOT NULL ," + // 6: aircraftLocationAltitude
                "\"BASE_LOCATION_LATITUDE\" REAL NOT NULL ," + // 7: baseLocationLatitude
                "\"BASE_LOCATION_LONGITUDE\" REAL NOT NULL ," + // 8: baseLocationLongitude
                "\"BASE_LOCATION_ALTITUDE\" REAL NOT NULL ," + // 9: baseLocationAltitude
                "\"AIRCRAFT_YAW\" REAL NOT NULL ," + // 10: aircraftYaw
                "\"AIRCRAFT_PITCH\" REAL NOT NULL ," + // 11: aircraftPitch
                "\"AIRCRAFT_ROLL\" REAL NOT NULL ," + // 12: aircraftRoll
                "\"GIMBAL_YAW\" REAL NOT NULL ," + // 13: gimbalYaw
                "\"GIMBAL_PITCH\" REAL NOT NULL ," + // 14: gimbalPitch
                "\"GIMBAL_ROLL\" REAL NOT NULL ," + // 15: gimbalRoll
                "\"MISSION_TYPE\" INTEGER NOT NULL ," + // 16: missionType
                "\"MISSION_MODE\" INTEGER NOT NULL ," + // 17: missionMode
                "\"WAYPOINT_TYPE\" INTEGER NOT NULL ," + // 18: waypointType
                "\"CAMERA_NAME\" TEXT," + // 19: cameraName
                "\"AIRCRAFT_NAME\" TEXT," + // 20: aircraftName
                "\"CREATED_TIME\" INTEGER);"); // 21: createdTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_Tower_StudyDetail_v2\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TowerStudyDetailV2 entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String towerId = entity.getTowerId();
        if (towerId != null) {
            stmt.bindString(2, towerId);
        }
        stmt.bindDouble(3, entity.getHomeLocationLatitude());
        stmt.bindDouble(4, entity.getHomeLocationLongitude());
        stmt.bindDouble(5, entity.getAircraftLocationLatitude());
        stmt.bindDouble(6, entity.getAircraftLocationLongitude());
        stmt.bindDouble(7, entity.getAircraftLocationAltitude());
        stmt.bindDouble(8, entity.getBaseLocationLatitude());
        stmt.bindDouble(9, entity.getBaseLocationLongitude());
        stmt.bindDouble(10, entity.getBaseLocationAltitude());
        stmt.bindDouble(11, entity.getAircraftYaw());
        stmt.bindDouble(12, entity.getAircraftPitch());
        stmt.bindDouble(13, entity.getAircraftRoll());
        stmt.bindDouble(14, entity.getGimbalYaw());
        stmt.bindDouble(15, entity.getGimbalPitch());
        stmt.bindDouble(16, entity.getGimbalRoll());
        stmt.bindLong(17, entity.getMissionType());
        stmt.bindLong(18, entity.getMissionMode());
        stmt.bindLong(19, entity.getWaypointType());
 
        String cameraName = entity.getCameraName();
        if (cameraName != null) {
            stmt.bindString(20, cameraName);
        }
 
        String aircraftName = entity.getAircraftName();
        if (aircraftName != null) {
            stmt.bindString(21, aircraftName);
        }
 
        java.util.Date createdTime = entity.getCreatedTime();
        if (createdTime != null) {
            stmt.bindLong(22, createdTime.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TowerStudyDetailV2 entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String towerId = entity.getTowerId();
        if (towerId != null) {
            stmt.bindString(2, towerId);
        }
        stmt.bindDouble(3, entity.getHomeLocationLatitude());
        stmt.bindDouble(4, entity.getHomeLocationLongitude());
        stmt.bindDouble(5, entity.getAircraftLocationLatitude());
        stmt.bindDouble(6, entity.getAircraftLocationLongitude());
        stmt.bindDouble(7, entity.getAircraftLocationAltitude());
        stmt.bindDouble(8, entity.getBaseLocationLatitude());
        stmt.bindDouble(9, entity.getBaseLocationLongitude());
        stmt.bindDouble(10, entity.getBaseLocationAltitude());
        stmt.bindDouble(11, entity.getAircraftYaw());
        stmt.bindDouble(12, entity.getAircraftPitch());
        stmt.bindDouble(13, entity.getAircraftRoll());
        stmt.bindDouble(14, entity.getGimbalYaw());
        stmt.bindDouble(15, entity.getGimbalPitch());
        stmt.bindDouble(16, entity.getGimbalRoll());
        stmt.bindLong(17, entity.getMissionType());
        stmt.bindLong(18, entity.getMissionMode());
        stmt.bindLong(19, entity.getWaypointType());
 
        String cameraName = entity.getCameraName();
        if (cameraName != null) {
            stmt.bindString(20, cameraName);
        }
 
        String aircraftName = entity.getAircraftName();
        if (aircraftName != null) {
            stmt.bindString(21, aircraftName);
        }
 
        java.util.Date createdTime = entity.getCreatedTime();
        if (createdTime != null) {
            stmt.bindLong(22, createdTime.getTime());
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TowerStudyDetailV2 readEntity(Cursor cursor, int offset) {
        TowerStudyDetailV2 entity = new TowerStudyDetailV2( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // towerId
            cursor.getDouble(offset + 2), // homeLocationLatitude
            cursor.getDouble(offset + 3), // homeLocationLongitude
            cursor.getDouble(offset + 4), // aircraftLocationLatitude
            cursor.getDouble(offset + 5), // aircraftLocationLongitude
            cursor.getFloat(offset + 6), // aircraftLocationAltitude
            cursor.getDouble(offset + 7), // baseLocationLatitude
            cursor.getDouble(offset + 8), // baseLocationLongitude
            cursor.getFloat(offset + 9), // baseLocationAltitude
            cursor.getDouble(offset + 10), // aircraftYaw
            cursor.getFloat(offset + 11), // aircraftPitch
            cursor.getFloat(offset + 12), // aircraftRoll
            cursor.getDouble(offset + 13), // gimbalYaw
            cursor.getFloat(offset + 14), // gimbalPitch
            cursor.getFloat(offset + 15), // gimbalRoll
            cursor.getInt(offset + 16), // missionType
            cursor.getInt(offset + 17), // missionMode
            cursor.getInt(offset + 18), // waypointType
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // cameraName
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // aircraftName
            cursor.isNull(offset + 21) ? null : new java.util.Date(cursor.getLong(offset + 21)) // createdTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TowerStudyDetailV2 entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTowerId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHomeLocationLatitude(cursor.getDouble(offset + 2));
        entity.setHomeLocationLongitude(cursor.getDouble(offset + 3));
        entity.setAircraftLocationLatitude(cursor.getDouble(offset + 4));
        entity.setAircraftLocationLongitude(cursor.getDouble(offset + 5));
        entity.setAircraftLocationAltitude(cursor.getFloat(offset + 6));
        entity.setBaseLocationLatitude(cursor.getDouble(offset + 7));
        entity.setBaseLocationLongitude(cursor.getDouble(offset + 8));
        entity.setBaseLocationAltitude(cursor.getFloat(offset + 9));
        entity.setAircraftYaw(cursor.getDouble(offset + 10));
        entity.setAircraftPitch(cursor.getFloat(offset + 11));
        entity.setAircraftRoll(cursor.getFloat(offset + 12));
        entity.setGimbalYaw(cursor.getDouble(offset + 13));
        entity.setGimbalPitch(cursor.getFloat(offset + 14));
        entity.setGimbalRoll(cursor.getFloat(offset + 15));
        entity.setMissionType(cursor.getInt(offset + 16));
        entity.setMissionMode(cursor.getInt(offset + 17));
        entity.setWaypointType(cursor.getInt(offset + 18));
        entity.setCameraName(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setAircraftName(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setCreatedTime(cursor.isNull(offset + 21) ? null : new java.util.Date(cursor.getLong(offset + 21)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TowerStudyDetailV2 entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TowerStudyDetailV2 entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TowerStudyDetailV2 entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
