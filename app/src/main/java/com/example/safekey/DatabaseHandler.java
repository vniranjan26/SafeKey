package com.example.safekey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
public class DatabaseHandler extends SQLiteOpenHelper {
    public static String auth_email;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "AndroidLogin";
    // Login table name
    private static final String TABLE_LOGIN = "login";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_BIOMETRIC = "biometric";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Table Create Statements
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UID + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_CREATED_AT + " TEXT,"
            + KEY_BIOMETRIC + " TEXT" + ")";
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        // Create tables again
        onCreate(db);
    }
    /**
     * Storing user details in database
     * */
    public void addUser(String uid, String name, String email, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid); // uid
        values.put(KEY_NAME, name); // FirstName
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_BIOMETRIC,0);
        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
        auth_email = email;
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("uid", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
            user.put("biometric",cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }
    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public boolean setBiometric(String state, String email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BIOMETRIC,state);
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_LOGIN + " WHERE email=?",new String[] {email});
        if (cursor.getCount()>0)
        {
            long result = db.update(TABLE_LOGIN, values, KEY_EMAIL + "=?", new String[]{email});
            if (result == -1) {
                db.close();
                return false;
            } else {
                db.close();
                return true;
            }
        }
        else
            {
                return false;
        }
    }
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
}