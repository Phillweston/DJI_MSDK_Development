package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint;


/** 
 * DAO for table "FLY_AREA_POINT".
*/
public class FlyAreaPointDao extends AbstractDao<FlyAreaPoint, String> {

    public static final String TABLENAME = "FLY_AREA_POINT";

    /**
     * Properties of entity FlyAreaPoint.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property TargetId = new Property(1, String.class, "targetId", false, "TARGET_ID");
        public final static Property RootTaskId = new Property(2, String.class, "rootTaskId", false, "ROOT_TASK_ID");
        public final static Property Latitude = new Property(3, double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(4, double.class, "longitude", false, "LONGITUDE");
        public final static Property Altitude = new Property(5, float.class, "altitude", false, "ALTITUDE");
        public final static Property Speed = new Property(6, double.class, "speed", false, "SPEED");
        public final static Property Pitch = new Property(7, int.class, "pitch", false, "PITCH");
        public final static Property Yaw = new Property(8, int.class, "yaw", false, "YAW");
        public final static Property CreateTime = new Property(9, double.class, "createTime", false, "CREATE_TIME");
    }


    public FlyAreaPointDao(DaoConfig config) {
        super(config);
    }
    
    public FlyAreaPointDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FLY_AREA_POINT\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"TARGET_ID\" TEXT NOT NULL ," + // 1: targetId
                "\"ROOT_TASK_ID\" TEXT NOT NULL ," + // 2: rootTaskId
                "\"LATITUDE\" REAL NOT NULL ," + // 3: latitude
                "\"LONGITUDE\" REAL NOT NULL ," + // 4: longitude
                "\"ALTITUDE\" REAL NOT NULL ," + // 5: altitude
                "\"SPEED\" REAL NOT NULL ," + // 6: speed
                "\"PITCH\" INTEGER NOT NULL ," + // 7: pitch
                "\"YAW\" INTEGER NOT NULL ," + // 8: yaw
                "\"CREATE_TIME\" REAL NOT NULL );");
      
        db.execSQL("CREATE INDEX " + constraint + "IDX_FLY_AREA_POINT_TARGET_ID_CREATE_TIME ON \"FLY_AREA_POINT\"" +
                " (\"TARGET_ID\" ASC,\"CREATE_TIME\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FLY_AREA_POINT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FlyAreaPoint entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindString(2, entity.getTargetId());
        stmt.bindString(3, entity.getRootTaskId());
        stmt.bindDouble(4, entity.getLatitude());
        stmt.bindDouble(5, entity.getLongitude());
        stmt.bindDouble(6, entity.getAltitude());
        stmt.bindDouble(7, entity.getSpeed());
        stmt.bindLong(8, entity.getPitch());
        stmt.bindLong(9, entity.getYaw());
        stmt.bindDouble(10, entity.getCreateTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FlyAreaPoint entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindString(2, entity.getTargetId());
        stmt.bindString(3, entity.getRootTaskId());
        stmt.bindDouble(4, entity.getLatitude());
        stmt.bindDouble(5, entity.getLongitude());
        stmt.bindDouble(6, entity.getAltitude());
        stmt.bindDouble(7, entity.getSpeed());
        stmt.bindLong(8, entity.getPitch());
        stmt.bindLong(9, entity.getYaw());
        stmt.bindDouble(10, entity.getCreateTime());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public FlyAreaPoint readEntity(Cursor cursor, int offset) {
        FlyAreaPoint entity = new FlyAreaPoint( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.getString(offset + 1), // targetId
            cursor.getString(offset + 2), // rootTaskId
            cursor.getDouble(offset + 3), // latitude
            cursor.getDouble(offset + 4), // longitude
            cursor.getFloat(offset + 5), // altitude
            cursor.getDouble(offset + 6), // speed
            cursor.getInt(offset + 7), // pitch
            cursor.getInt(offset + 8), // yaw
            cursor.getDouble(offset + 9) // createTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FlyAreaPoint entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTargetId(cursor.getString(offset + 1));
        entity.setRootTaskId(cursor.getString(offset + 2));
        entity.setLatitude(cursor.getDouble(offset + 3));
        entity.setLongitude(cursor.getDouble(offset + 4));
        entity.setAltitude(cursor.getFloat(offset + 5));
        entity.setSpeed(cursor.getDouble(offset + 6));
        entity.setPitch(cursor.getInt(offset + 7));
        entity.setYaw(cursor.getInt(offset + 8));
        entity.setCreateTime(cursor.getDouble(offset + 9));
     }
    
    @Override
    protected final String updateKeyAfterInsert(FlyAreaPoint entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(FlyAreaPoint entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FlyAreaPoint entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
