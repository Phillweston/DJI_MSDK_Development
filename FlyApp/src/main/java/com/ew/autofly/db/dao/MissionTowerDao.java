package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.entity.MissionTower;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_Mission_Tower".
*/
public class MissionTowerDao extends AbstractDao<MissionTower, Long> {

    public static final String TABLENAME = "t_Mission_Tower";

    /**
     * Properties of entity MissionTower.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MissionId = new Property(1, String.class, "missionId", false, "MISSION_ID");
        public final static Property TowerNo = new Property(2, String.class, "towerNo", false, "TOWER_NO");
        public final static Property GridLineName = new Property(3, String.class, "gridLineName", false, "GRID_LINE_NAME");
        public final static Property Latitude = new Property(4, double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(5, double.class, "longitude", false, "LONGITUDE");
        public final static Property FlyAltitude = new Property(6, float.class, "flyAltitude", false, "FLY_ALTITUDE");
    }


    public MissionTowerDao(DaoConfig config) {
        super(config);
    }
    
    public MissionTowerDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_Mission_Tower\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MISSION_ID\" TEXT," + // 1: missionId
                "\"TOWER_NO\" TEXT," + // 2: towerNo
                "\"GRID_LINE_NAME\" TEXT," + // 3: gridLineName
                "\"LATITUDE\" REAL NOT NULL ," + // 4: latitude
                "\"LONGITUDE\" REAL NOT NULL ," + // 5: longitude
                "\"FLY_ALTITUDE\" REAL NOT NULL );"); // 6: flyAltitude
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_Mission_Tower\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MissionTower entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String missionId = entity.getMissionId();
        if (missionId != null) {
            stmt.bindString(2, missionId);
        }
 
        String towerNo = entity.getTowerNo();
        if (towerNo != null) {
            stmt.bindString(3, towerNo);
        }
 
        String gridLineName = entity.getGridLineName();
        if (gridLineName != null) {
            stmt.bindString(4, gridLineName);
        }
        stmt.bindDouble(5, entity.getLatitude());
        stmt.bindDouble(6, entity.getLongitude());
        stmt.bindDouble(7, entity.getFlyAltitude());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MissionTower entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String missionId = entity.getMissionId();
        if (missionId != null) {
            stmt.bindString(2, missionId);
        }
 
        String towerNo = entity.getTowerNo();
        if (towerNo != null) {
            stmt.bindString(3, towerNo);
        }
 
        String gridLineName = entity.getGridLineName();
        if (gridLineName != null) {
            stmt.bindString(4, gridLineName);
        }
        stmt.bindDouble(5, entity.getLatitude());
        stmt.bindDouble(6, entity.getLongitude());
        stmt.bindDouble(7, entity.getFlyAltitude());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MissionTower readEntity(Cursor cursor, int offset) {
        MissionTower entity = new MissionTower( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // missionId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // towerNo
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gridLineName
            cursor.getDouble(offset + 4), // latitude
            cursor.getDouble(offset + 5), // longitude
            cursor.getFloat(offset + 6) // flyAltitude
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MissionTower entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMissionId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTowerNo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGridLineName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLatitude(cursor.getDouble(offset + 4));
        entity.setLongitude(cursor.getDouble(offset + 5));
        entity.setFlyAltitude(cursor.getFloat(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MissionTower entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MissionTower entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MissionTower entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}