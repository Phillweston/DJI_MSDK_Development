package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.entity.TowerStudyRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_Tower_StudyRecord".
*/
public class TowerStudyRecordDao extends AbstractDao<TowerStudyRecord, Long> {

    public static final String TABLENAME = "t_Tower_StudyRecord";

    /**
     * Properties of entity TowerStudyRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TowerId = new Property(1, String.class, "towerId", false, "TOWER_ID");
        public final static Property MissionType = new Property(2, int.class, "missionType", false, "MISSION_TYPE");
        public final static Property MissionMode = new Property(3, int.class, "missionMode", false, "MISSION_MODE");
        public final static Property RecordStatus = new Property(4, int.class, "recordStatus", false, "RECORD_STATUS");
    }


    public TowerStudyRecordDao(DaoConfig config) {
        super(config);
    }
    
    public TowerStudyRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_Tower_StudyRecord\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TOWER_ID\" TEXT," + // 1: towerId
                "\"MISSION_TYPE\" INTEGER NOT NULL ," + // 2: missionType
                "\"MISSION_MODE\" INTEGER NOT NULL ," + // 3: missionMode
                "\"RECORD_STATUS\" INTEGER NOT NULL );"); // 4: recordStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_Tower_StudyRecord\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TowerStudyRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String towerId = entity.getTowerId();
        if (towerId != null) {
            stmt.bindString(2, towerId);
        }
        stmt.bindLong(3, entity.getMissionType());
        stmt.bindLong(4, entity.getMissionMode());
        stmt.bindLong(5, entity.getRecordStatus());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TowerStudyRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String towerId = entity.getTowerId();
        if (towerId != null) {
            stmt.bindString(2, towerId);
        }
        stmt.bindLong(3, entity.getMissionType());
        stmt.bindLong(4, entity.getMissionMode());
        stmt.bindLong(5, entity.getRecordStatus());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TowerStudyRecord readEntity(Cursor cursor, int offset) {
        TowerStudyRecord entity = new TowerStudyRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // towerId
            cursor.getInt(offset + 2), // missionType
            cursor.getInt(offset + 3), // missionMode
            cursor.getInt(offset + 4) // recordStatus
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TowerStudyRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTowerId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMissionType(cursor.getInt(offset + 2));
        entity.setMissionMode(cursor.getInt(offset + 3));
        entity.setRecordStatus(cursor.getInt(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TowerStudyRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TowerStudyRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TowerStudyRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
