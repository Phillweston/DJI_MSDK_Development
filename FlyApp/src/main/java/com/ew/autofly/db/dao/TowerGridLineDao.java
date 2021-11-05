package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.entity.TowerGridLine;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_TowerGridLine".
*/
public class TowerGridLineDao extends AbstractDao<TowerGridLine, Long> {

    public static final String TABLENAME = "t_TowerGridLine";

    /**
     * Properties of entity TowerGridLine.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "Id");
        public final static Property LineName = new Property(1, String.class, "lineName", false, "lineName");
        public final static Property Voltage = new Property(2, String.class, "voltage", false, "voltage");
        public final static Property GroupName = new Property(3, String.class, "groupName", false, "groupName");
    }


    public TowerGridLineDao(DaoConfig config) {
        super(config);
    }
    
    public TowerGridLineDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_TowerGridLine\" (" + //
                "\"Id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"lineName\" TEXT," + // 1: lineName
                "\"voltage\" TEXT," + // 2: voltage
                "\"groupName\" TEXT);"); // 3: groupName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_TowerGridLine\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TowerGridLine entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String lineName = entity.getLineName();
        if (lineName != null) {
            stmt.bindString(2, lineName);
        }
 
        String voltage = entity.getVoltage();
        if (voltage != null) {
            stmt.bindString(3, voltage);
        }
 
        String groupName = entity.getGroupName();
        if (groupName != null) {
            stmt.bindString(4, groupName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TowerGridLine entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String lineName = entity.getLineName();
        if (lineName != null) {
            stmt.bindString(2, lineName);
        }
 
        String voltage = entity.getVoltage();
        if (voltage != null) {
            stmt.bindString(3, voltage);
        }
 
        String groupName = entity.getGroupName();
        if (groupName != null) {
            stmt.bindString(4, groupName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TowerGridLine readEntity(Cursor cursor, int offset) {
        TowerGridLine entity = new TowerGridLine( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // lineName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // voltage
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // groupName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TowerGridLine entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLineName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setVoltage(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGroupName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TowerGridLine entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TowerGridLine entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TowerGridLine entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
