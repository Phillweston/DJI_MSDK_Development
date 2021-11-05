package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.converter.StringConverter;
import java.util.List;

import com.ew.autofly.db.entity.MissionChannelPatrol;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_Mission_ChannelPatrol".
*/
public class MissionChannelPatrolDao extends AbstractDao<MissionChannelPatrol, Long> {

    public static final String TABLENAME = "t_Mission_ChannelPatrol";

    /**
     * Properties of entity MissionChannelPatrol.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MissionId = new Property(1, String.class, "missionId", false, "MISSION_ID");
        public final static Property MissionBatchId = new Property(2, String.class, "missionBatchId", false, "MISSION_BATCH_ID");
        public final static Property MissionType = new Property(3, int.class, "missionType", false, "MISSION_TYPE");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Snapshot = new Property(5, String.class, "snapshot", false, "SNAPSHOT");
        public final static Property Status = new Property(6, int.class, "status", false, "STATUS");
        public final static Property CreateDate = new Property(7, java.util.Date.class, "createDate", false, "CREATE_DATE");
        public final static Property StartTime = new Property(8, java.util.Date.class, "startTime", false, "START_TIME");
        public final static Property EndTime = new Property(9, java.util.Date.class, "endTime", false, "END_TIME");
        public final static Property StartPhotoIndex = new Property(10, int.class, "startPhotoIndex", false, "START_PHOTO_INDEX");
        public final static Property EndPhotoIndex = new Property(11, int.class, "endPhotoIndex", false, "END_PHOTO_INDEX");
        public final static Property GeometryType = new Property(12, int.class, "geometryType", false, "GEOMETRY_TYPE");
        public final static Property GeometryLatLngList = new Property(13, String.class, "geometryLatLngList", false, "GEOMETRY_LAT_LNG_LIST");
        public final static Property RouteOverlap = new Property(14, int.class, "routeOverlap", false, "ROUTE_OVERLAP");
        public final static Property ResolutionRate = new Property(15, double.class, "resolutionRate", false, "RESOLUTION_RATE");
        public final static Property IsAltitudeFixed = new Property(16, boolean.class, "isAltitudeFixed", false, "IS_ALTITUDE_FIXED");
        public final static Property Altitude = new Property(17, int.class, "altitude", false, "ALTITUDE");
        public final static Property FlySpeed = new Property(18, int.class, "flySpeed", false, "FLY_SPEED");
        public final static Property EntryHeight = new Property(19, int.class, "entryHeight", false, "ENTRY_HEIGHT");
        public final static Property IsReverse = new Property(20, boolean.class, "isReverse", false, "IS_REVERSE");
        public final static Property ChannelEndExtend = new Property(21, int.class, "channelEndExtend", false, "CHANNEL_END_EXTEND");
        public final static Property ActionMode = new Property(22, int.class, "actionMode", false, "ACTION_MODE");
        public final static Property ReturnMode = new Property(23, int.class, "returnMode", false, "RETURN_MODE");
        public final static Property RecodeMode = new Property(24, int.class, "recodeMode", false, "RECODE_MODE");
        public final static Property BaseLineHeight = new Property(25, int.class, "baseLineHeight", false, "BASE_LINE_HEIGHT");
    }

    private final StringConverter geometryLatLngListConverter = new StringConverter();

    public MissionChannelPatrolDao(DaoConfig config) {
        super(config);
    }
    
    public MissionChannelPatrolDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_Mission_ChannelPatrol\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MISSION_ID\" TEXT UNIQUE ," + // 1: missionId
                "\"MISSION_BATCH_ID\" TEXT," + // 2: missionBatchId
                "\"MISSION_TYPE\" INTEGER NOT NULL ," + // 3: missionType
                "\"NAME\" TEXT," + // 4: name
                "\"SNAPSHOT\" TEXT," + // 5: snapshot
                "\"STATUS\" INTEGER NOT NULL ," + // 6: status
                "\"CREATE_DATE\" INTEGER," + // 7: createDate
                "\"START_TIME\" INTEGER," + // 8: startTime
                "\"END_TIME\" INTEGER," + // 9: endTime
                "\"START_PHOTO_INDEX\" INTEGER NOT NULL ," + // 10: startPhotoIndex
                "\"END_PHOTO_INDEX\" INTEGER NOT NULL ," + // 11: endPhotoIndex
                "\"GEOMETRY_TYPE\" INTEGER NOT NULL ," + // 12: geometryType
                "\"GEOMETRY_LAT_LNG_LIST\" TEXT," + // 13: geometryLatLngList
                "\"ROUTE_OVERLAP\" INTEGER NOT NULL ," + // 14: routeOverlap
                "\"RESOLUTION_RATE\" REAL NOT NULL ," + // 15: resolutionRate
                "\"IS_ALTITUDE_FIXED\" INTEGER NOT NULL ," + // 16: isAltitudeFixed
                "\"ALTITUDE\" INTEGER NOT NULL ," + // 17: altitude
                "\"FLY_SPEED\" INTEGER NOT NULL ," + // 18: flySpeed
                "\"ENTRY_HEIGHT\" INTEGER NOT NULL ," + // 19: entryHeight
                "\"IS_REVERSE\" INTEGER NOT NULL ," + // 20: isReverse
                "\"CHANNEL_END_EXTEND\" INTEGER NOT NULL ," + // 21: channelEndExtend
                "\"ACTION_MODE\" INTEGER NOT NULL ," + // 22: actionMode
                "\"RETURN_MODE\" INTEGER NOT NULL ," + // 23: returnMode
                "\"RECODE_MODE\" INTEGER NOT NULL ," + // 24: recodeMode
                "\"BASE_LINE_HEIGHT\" INTEGER NOT NULL );"); // 25: baseLineHeight
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_Mission_ChannelPatrol\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MissionChannelPatrol entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String missionId = entity.getMissionId();
        if (missionId != null) {
            stmt.bindString(2, missionId);
        }
 
        String missionBatchId = entity.getMissionBatchId();
        if (missionBatchId != null) {
            stmt.bindString(3, missionBatchId);
        }
        stmt.bindLong(4, entity.getMissionType());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String snapshot = entity.getSnapshot();
        if (snapshot != null) {
            stmt.bindString(6, snapshot);
        }
        stmt.bindLong(7, entity.getStatus());
 
        java.util.Date createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(8, createDate.getTime());
        }
 
        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(9, startTime.getTime());
        }
 
        java.util.Date endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(10, endTime.getTime());
        }
        stmt.bindLong(11, entity.getStartPhotoIndex());
        stmt.bindLong(12, entity.getEndPhotoIndex());
        stmt.bindLong(13, entity.getGeometryType());
 
        List geometryLatLngList = entity.getGeometryLatLngList();
        if (geometryLatLngList != null) {
            stmt.bindString(14, geometryLatLngListConverter.convertToDatabaseValue(geometryLatLngList));
        }
        stmt.bindLong(15, entity.getRouteOverlap());
        stmt.bindDouble(16, entity.getResolutionRate());
        stmt.bindLong(17, entity.getIsAltitudeFixed() ? 1L: 0L);
        stmt.bindLong(18, entity.getAltitude());
        stmt.bindLong(19, entity.getFlySpeed());
        stmt.bindLong(20, entity.getEntryHeight());
        stmt.bindLong(21, entity.getIsReverse() ? 1L: 0L);
        stmt.bindLong(22, entity.getChannelEndExtend());
        stmt.bindLong(23, entity.getActionMode());
        stmt.bindLong(24, entity.getReturnMode());
        stmt.bindLong(25, entity.getRecodeMode());
        stmt.bindLong(26, entity.getBaseLineHeight());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MissionChannelPatrol entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String missionId = entity.getMissionId();
        if (missionId != null) {
            stmt.bindString(2, missionId);
        }
 
        String missionBatchId = entity.getMissionBatchId();
        if (missionBatchId != null) {
            stmt.bindString(3, missionBatchId);
        }
        stmt.bindLong(4, entity.getMissionType());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String snapshot = entity.getSnapshot();
        if (snapshot != null) {
            stmt.bindString(6, snapshot);
        }
        stmt.bindLong(7, entity.getStatus());
 
        java.util.Date createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(8, createDate.getTime());
        }
 
        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(9, startTime.getTime());
        }
 
        java.util.Date endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(10, endTime.getTime());
        }
        stmt.bindLong(11, entity.getStartPhotoIndex());
        stmt.bindLong(12, entity.getEndPhotoIndex());
        stmt.bindLong(13, entity.getGeometryType());
 
        List geometryLatLngList = entity.getGeometryLatLngList();
        if (geometryLatLngList != null) {
            stmt.bindString(14, geometryLatLngListConverter.convertToDatabaseValue(geometryLatLngList));
        }
        stmt.bindLong(15, entity.getRouteOverlap());
        stmt.bindDouble(16, entity.getResolutionRate());
        stmt.bindLong(17, entity.getIsAltitudeFixed() ? 1L: 0L);
        stmt.bindLong(18, entity.getAltitude());
        stmt.bindLong(19, entity.getFlySpeed());
        stmt.bindLong(20, entity.getEntryHeight());
        stmt.bindLong(21, entity.getIsReverse() ? 1L: 0L);
        stmt.bindLong(22, entity.getChannelEndExtend());
        stmt.bindLong(23, entity.getActionMode());
        stmt.bindLong(24, entity.getReturnMode());
        stmt.bindLong(25, entity.getRecodeMode());
        stmt.bindLong(26, entity.getBaseLineHeight());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MissionChannelPatrol readEntity(Cursor cursor, int offset) {
        MissionChannelPatrol entity = new MissionChannelPatrol( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // missionId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // missionBatchId
            cursor.getInt(offset + 3), // missionType
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // snapshot
            cursor.getInt(offset + 6), // status
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // createDate
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // startTime
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // endTime
            cursor.getInt(offset + 10), // startPhotoIndex
            cursor.getInt(offset + 11), // endPhotoIndex
            cursor.getInt(offset + 12), // geometryType
            cursor.isNull(offset + 13) ? null : geometryLatLngListConverter.convertToEntityProperty(cursor.getString(offset + 13)), // geometryLatLngList
            cursor.getInt(offset + 14), // routeOverlap
            cursor.getDouble(offset + 15), // resolutionRate
            cursor.getShort(offset + 16) != 0, // isAltitudeFixed
            cursor.getInt(offset + 17), // altitude
            cursor.getInt(offset + 18), // flySpeed
            cursor.getInt(offset + 19), // entryHeight
            cursor.getShort(offset + 20) != 0, // isReverse
            cursor.getInt(offset + 21), // channelEndExtend
            cursor.getInt(offset + 22), // actionMode
            cursor.getInt(offset + 23), // returnMode
            cursor.getInt(offset + 24), // recodeMode
            cursor.getInt(offset + 25) // baseLineHeight
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MissionChannelPatrol entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMissionId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMissionBatchId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMissionType(cursor.getInt(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSnapshot(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStatus(cursor.getInt(offset + 6));
        entity.setCreateDate(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setStartTime(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setEndTime(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setStartPhotoIndex(cursor.getInt(offset + 10));
        entity.setEndPhotoIndex(cursor.getInt(offset + 11));
        entity.setGeometryType(cursor.getInt(offset + 12));
        entity.setGeometryLatLngList(cursor.isNull(offset + 13) ? null : geometryLatLngListConverter.convertToEntityProperty(cursor.getString(offset + 13)));
        entity.setRouteOverlap(cursor.getInt(offset + 14));
        entity.setResolutionRate(cursor.getDouble(offset + 15));
        entity.setIsAltitudeFixed(cursor.getShort(offset + 16) != 0);
        entity.setAltitude(cursor.getInt(offset + 17));
        entity.setFlySpeed(cursor.getInt(offset + 18));
        entity.setEntryHeight(cursor.getInt(offset + 19));
        entity.setIsReverse(cursor.getShort(offset + 20) != 0);
        entity.setChannelEndExtend(cursor.getInt(offset + 21));
        entity.setActionMode(cursor.getInt(offset + 22));
        entity.setReturnMode(cursor.getInt(offset + 23));
        entity.setRecodeMode(cursor.getInt(offset + 24));
        entity.setBaseLineHeight(cursor.getInt(offset + 25));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MissionChannelPatrol entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MissionChannelPatrol entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MissionChannelPatrol entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
