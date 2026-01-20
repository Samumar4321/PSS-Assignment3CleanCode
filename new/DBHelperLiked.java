package it.marcosoft.ticketwave.util.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import it.marcosoft.ticketwave.data.LikedData;

public class DBHelperLiked extends BaseDBHelper {

    private static final String DATABASE_NAME = "liked_events.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LIKED = "liked_events";
    
    private static final String COL_ID = "event_id";
    private static final String COL_USER = "user_id";
    private static final String COL_TITLE = "event_title";
    private static final String COL_LOC = "event_location";
    private static final String COL_DATE = "event_date";
    private static final String COL_DESC = "event_description";
    private static final String COL_IMG = "event_image_url";

    public DBHelperLiked(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableQuery());
    }

    private String getCreateTableQuery() {
        return String.format(
            "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
            TABLE_LIKED, COL_ID, COL_USER, COL_TITLE, COL_LOC, COL_DATE, COL_DESC, COL_IMG
        );
    }

    public void addLikedEvent(LikedData data) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.insert(TABLE_LIKED, null, createContentValues(data));
        }
    }

    public boolean removeLikedEvent(String eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String whereClause = COLUMN_EVENT_ID + " = ? ";
            String[] whereArgs = {eventId};
            db.delete(TABLE_LIKED_EVENTS, whereClause, whereArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean isEventLiked(String eventId, String userId) {
        String selection = COL_ID + " = ? AND " + COL_USER + " = ?";
        String[] args = {eventId, userId};

        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_LIKED, null, selection, args, null, null, null)) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    public LikedData getLikedEventById(String eventId, String userId) {
       String selection = COL_ID + " = ? AND " + COL_USER + " = ?";
       String[] args = {eventId, userId};
   
       try (SQLiteDatabase db = getReadableDatabase();
          Cursor cursor = db.query(TABLE_LIKED, null, selection, args, null, null, null)) {
        
          if (cursor != null && cursor.moveToFirst()) {
              return mapCursorToLikedData(cursor);           }
     }
     return null;
    }

    public List<LikedData> getAllLikedEvents() {
        List<LikedData> events = new ArrayList<>();
        // Uso di try-with-resources per gestione automatica chiusura
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LIKED, null)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    events.add(mapCursorToLikedData(cursor));
                } while (cursor.moveToNext());
            }
        }
        return events;
    }

    private LikedData mapCursorToLikedData(Cursor cursor) {
        return new LikedData(
            getString(cursor, COL_ID),
            getString(cursor, COL_USER),
            getString(cursor, COL_TITLE),
            getString(cursor, COL_LOC),
            getString(cursor, COL_DATE),
            getString(cursor, COL_DESC),
            getString(cursor, COL_IMG)
        );
    }

    private String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    private ContentValues createContentValues(LikedData data) {
        ContentValues values = new ContentValues();
        values.put(COL_ID, data.getEventId());
        values.put(COL_USER, data.getUserId());
        values.put(COL_TITLE, data.getEventTitle());
        values.put(COL_LOC, data.getEventLocation());
        values.put(COL_DATE, data.getEventDate());
        values.put(COL_DESC, data.getEventDescription());
        values.put(COL_IMG, data.getEventImageUrl());
        return values;
    }
}