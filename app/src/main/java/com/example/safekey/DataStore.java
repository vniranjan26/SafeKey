package com.example.safekey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataStore extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "LoginCard";
    private static final String TABLE_LOGINCARD = "card";
    private static final String KEY_UID = "auth_email";
    private static final String KEY_ID = "id";
    private static final String KEY_CARDNAME = "card_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";

    public DataStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static final String CREATE_TABLE = "CREATE TABLE  " + TABLE_LOGINCARD + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UID + " TEXT,"
            + KEY_CARDNAME + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_PASS + " TEXT" + ")";
    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insert(String auth_email, String login_card_name, String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, auth_email);
        values.put(KEY_CARDNAME, login_card_name);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASS, password);
        long result = db.insert(TABLE_LOGINCARD, null, values);
        if(result==-1)
        {
            db.close();
            return false;
        }
        else {
            db.close();
            return true;
        }
    }
    public boolean update(String auth_email, String login_card_name, String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASS, password);
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_LOGINCARD + " WHERE card_name=? and auth_email=?",new String[] {login_card_name,auth_email});
        if(cursor.getCount()>0) {
            long result = db.update(TABLE_LOGINCARD, values, KEY_CARDNAME + "=?", new String[]{login_card_name});
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
    public boolean delete(String login_card_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_LOGINCARD + " WHERE card_name=?",new String[] {login_card_name});
        if(cursor.getCount()>0) {
            long result = db.delete(TABLE_LOGINCARD, KEY_CARDNAME + "=?", new String[]{login_card_name});
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
    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_LOGINCARD, null);
        return cursor;
    }
}
