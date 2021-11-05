package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.entity.MultiSpectralMissionConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "multispectral_mission_config".
*/
public class MultiSpectralMissionConfigDao extends AbstractDao<MultiSpectralMissionConfig, String> {

    public static final String TABLENAME = "multispectral_mission_config";

    /**
     * Properties of entity MultiSpectralMissionConfig.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property MissionId = new Property(0, String.class, "missionId", true, "mission_id");
        public final static Property Capture_mode = new Property(1, String.class, "capture_mode", false, "CAPTURE_MODE");
        public final static Property Capture_type = new Property(2, String.class, "capture_type", false, "CAPTURE_TYPE");
        public final static Property Timelapse_param = new Property(3, double.class, "timelapse_param", false, "TIMELAPSE_PARAM");
        public final static Property Gps_param = new Property(4, double.class, "gps_param", false, "GPS_PARAM");
        public final static Property Overlap_param = new Property(5, double.class, "overlap_param", false, "OVERLAP_PARAM");
        public final static Property Sensors_mask = new Property(6, int.class, "sensors_mask", false, "SENSORS_MASK");
    }


    public MultiSpectralMissionConfigDao(DaoConfig config) {
        super(config);
    }
    
    public MultiSpectralMissionConfigDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"multispectral_mission_config\" (" + //
                "\"mission_id\" TEXT PRIMARY KEY NOT NULL ," + // 0: missionId
                "\"CAPTURE_MODE\" TEXT," + // 1: capture_mode
                "\"CAPTURE_TYPE\" TEXT," + // 2: capture_type
                "\"TIMELAPSE_PARAM\" REAL NOT NULL ," + // 3: timelapse_param
                "\"GPS_PARAM\" REAL NOT NULL ," + // 4: gps_param
                "\"OVERLAP_PARAM\" REAL NOT NULL ," + // 5: overlap_param
                "\"SENSORS_MASK\" INTEGER NOT NULL );"); // 6: sensors_mask
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"multispectral_mission_config\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MultiSpectralMissionConfig entity) {
        stmt.clearBindings();
 
        String missionId = entity.getMissionId();
        if (missionId != null) {
            stmt.bindString(1, missionId);
        }
 
        String capture_mode = entity.getCapture_mode();
        if (capture_mode != null) {
            stmt.bindString(2, capture_mode);
        }
 
        String capture_type = entity.getCapture_type();
        if (capture_type != null) {
            stmt.bindString(3, capture_type);
        }
        stmt.bindDouble(4, entity.getTimelapse_param());
        stmt.bindDouble(5, entity.getGps_param());
        stmt.bindDouble(6, entity.getOverlap_param());
        stmt.bindLong(7, entity.getSensors_mask());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MultiSpectralMissionConfig entity) {
        stmt.clearBindings();
 
        String missionId = entity.getMissionId();
        if (missionId != null) {
            stmt.bindString(1, missionId);
        }
 
        String capture_mode = entity.getCapture_mode();
        if (capture_mode != null) {
            stmt.bindString(2, capture_mode);
        }
 
        String capture_type = entity.getCapture_type();
        if (capture_type != null) {
            stmt.bindString(3, capture_type);
        }
        stmt.bindDouble(4, entity.getTimelapse_param());
        stmt.bindDouble(5, entity.getGps_param());
        stmt.bindDouble(6, entity.getOverlap_param());
        stmt.bindLong(7, entity.getSensors_mask());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public MultiSpectralMissionConfig readEntity(Cursor cursor, int offset) {
        MultiSpectralMissionConfig entity = new MultiSpectralMissionConfig( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // missionId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // capture_mode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // capture_type
            cursor.getDouble(offset + 3), // timelapse_param
            cursor.getDouble(offset + 4), // gps_param
            cursor.getDouble(offset + 5), // overlap_param
            cursor.getInt(offset + 6) // sensors_mask
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MultiSpectralMissionConfig entity, int offset) {
        entity.setMissionId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCapture_mode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCapture_type(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTimelapse_param(cursor.getDouble(offset + 3));
        entity.setGps_param(cursor.getDouble(offset + 4));
        entity.setOverlap_param(cursor.getDouble(offset + 5));
        entity.setSensors_mask(cursor.getInt(offset + 6));
     }
    
    @Override
    protected final String updateKeyAfterInsert(MultiSpectralMissionConfig entity, long rowId) {
        return entity.getMissionId();
    }
    
    @Override
    public String getKey(MultiSpectralMissionConfig entity) {
        if(entity != null) {
            return entity.getMissionId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MultiSpectralMissionConfig entity) {
        return entity.getMissionId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
