package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.db.entity.FlightRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "flight_record".
*/
public class FlightRecordDao extends AbstractDao<FlightRecord, String> {

    public static final String TABLENAME = "flight_record";

    /**
     * Properties of entity FlightRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "id");
        public final static Property CreatedTime = new Property(1, String.class, "createdTime", false, "created_time");
        public final static Property StartTime = new Property(2, String.class, "startTime", false, "start_time");
        public final static Property EndTime = new Property(3, String.class, "endTime", false, "end_time");
        public final static Property MaxHeight = new Property(4, int.class, "maxHeight", false, "max_height");
        public final static Property ProductName = new Property(5, String.class, "productName", false, "product_name");
        public final static Property ProductSerialNumber = new Property(6, String.class, "productSerialNumber", false, "product_serial_number");
        public final static Property BatterySerialNumber = new Property(7, String.class, "batterySerialNumber", false, "battery_serial_number");
        public final static Property IsUpload = new Property(8, boolean.class, "isUpload", false, "is_upload");
        public final static Property Distance = new Property(9, double.class, "distance", false, "distance");
        public final static Property Longitude = new Property(10, double.class, "longitude", false, "longitude");
        public final static Property Latitude = new Property(11, double.class, "latitude", false, "latitude");
    }


    public FlightRecordDao(DaoConfig config) {
        super(config);
    }
    
    public FlightRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"flight_record\" (" + //
                "\"id\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"created_time\" TEXT," + // 1: createdTime
                "\"start_time\" TEXT," + // 2: startTime
                "\"end_time\" TEXT," + // 3: endTime
                "\"max_height\" INTEGER NOT NULL ," + // 4: maxHeight
                "\"product_name\" TEXT," + // 5: productName
                "\"product_serial_number\" TEXT," + // 6: productSerialNumber
                "\"battery_serial_number\" TEXT," + // 7: batterySerialNumber
                "\"is_upload\" INTEGER NOT NULL ," + // 8: isUpload
                "\"distance\" REAL NOT NULL ," + // 9: distance
                "\"longitude\" REAL NOT NULL ," + // 10: longitude
                "\"latitude\" REAL NOT NULL );"); // 11: latitude
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"flight_record\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FlightRecord entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String createdTime = entity.getCreatedTime();
        if (createdTime != null) {
            stmt.bindString(2, createdTime);
        }
 
        String startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindString(3, startTime);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(4, endTime);
        }
        stmt.bindLong(5, entity.getMaxHeight());
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(6, productName);
        }
 
        String productSerialNumber = entity.getProductSerialNumber();
        if (productSerialNumber != null) {
            stmt.bindString(7, productSerialNumber);
        }
 
        String batterySerialNumber = entity.getBatterySerialNumber();
        if (batterySerialNumber != null) {
            stmt.bindString(8, batterySerialNumber);
        }
        stmt.bindLong(9, entity.getIsUpload() ? 1L: 0L);
        stmt.bindDouble(10, entity.getDistance());
        stmt.bindDouble(11, entity.getLongitude());
        stmt.bindDouble(12, entity.getLatitude());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FlightRecord entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String createdTime = entity.getCreatedTime();
        if (createdTime != null) {
            stmt.bindString(2, createdTime);
        }
 
        String startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindString(3, startTime);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(4, endTime);
        }
        stmt.bindLong(5, entity.getMaxHeight());
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(6, productName);
        }
 
        String productSerialNumber = entity.getProductSerialNumber();
        if (productSerialNumber != null) {
            stmt.bindString(7, productSerialNumber);
        }
 
        String batterySerialNumber = entity.getBatterySerialNumber();
        if (batterySerialNumber != null) {
            stmt.bindString(8, batterySerialNumber);
        }
        stmt.bindLong(9, entity.getIsUpload() ? 1L: 0L);
        stmt.bindDouble(10, entity.getDistance());
        stmt.bindDouble(11, entity.getLongitude());
        stmt.bindDouble(12, entity.getLatitude());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public FlightRecord readEntity(Cursor cursor, int offset) {
        FlightRecord entity = new FlightRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // createdTime
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // startTime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // endTime
            cursor.getInt(offset + 4), // maxHeight
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // productName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // productSerialNumber
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // batterySerialNumber
            cursor.getShort(offset + 8) != 0, // isUpload
            cursor.getDouble(offset + 9), // distance
            cursor.getDouble(offset + 10), // longitude
            cursor.getDouble(offset + 11) // latitude
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FlightRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCreatedTime(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStartTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setEndTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMaxHeight(cursor.getInt(offset + 4));
        entity.setProductName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setProductSerialNumber(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBatterySerialNumber(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIsUpload(cursor.getShort(offset + 8) != 0);
        entity.setDistance(cursor.getDouble(offset + 9));
        entity.setLongitude(cursor.getDouble(offset + 10));
        entity.setLatitude(cursor.getDouble(offset + 11));
     }
    
    @Override
    protected final String updateKeyAfterInsert(FlightRecord entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(FlightRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FlightRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
