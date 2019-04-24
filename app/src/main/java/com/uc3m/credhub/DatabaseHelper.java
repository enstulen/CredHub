package com.uc3m.credhub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "credhub.db";
    public static final String TABLE_NAME = "password_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "description";
    public static final String COL_3 = "username";
    public static final String COL_4 = "password";
    public Context context;
    private static DatabaseHelper single_instance = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new DatabaseHelper(context);
        }
        return single_instance;
    }

    /**
     * Create database with static variables above.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION TEXT, USERNAME TEXT, PASSWORD TEXT) ");

    }

    /**
     * Upgrade database by dropping the table and creating it from scratch again with onCreate(db)
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert data in the database. The three paramaters corresponds to the columns in the database.
     * @param description
     * @param username
     * @param password
     * @return
     */
    public boolean insertData(String description, String username, String password) {
        SQLiteDatabase.loadLibs(context);
        SQLiteDatabase db = this.getWritableDatabase("password");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, username);
        contentValues.put(COL_4, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;

    }

    /**
     * Returns all data in the database
     * @return
     */
    public Cursor getAllData() {
        SQLiteDatabase.loadLibs(context);
        SQLiteDatabase db = this.getWritableDatabase("password");
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    /**
     * Delete the data in the database
     * @param id
     * @return
     */
    public Integer deleteData(String id) {
        SQLiteDatabase.loadLibs(context);
        SQLiteDatabase db = this.getWritableDatabase("password");
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }
}
