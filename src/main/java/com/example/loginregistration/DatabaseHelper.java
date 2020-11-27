package com.example.loginregistration;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "userInfo.db";
    public static final String TABLE_NAME = "registeruser";
    public static final String COL_1 = "ID"; //all columns in DB
    public static final String COL_2 = "Username";
    public static final String COL_3 = "Password";
    public static final String COL_4 = "Name";
    public static final String COL_5 = "Email"; //TODO: Jason remember to handle this
    public static final String COL_6 = "Address"; //TODO: Jason remember to handle this
    public static final String COL_7 = "Phone"; //TODO: Jason remember to handle this
    public static final String COL_8 = "Security_Question"; //TODO: Jason remember to handle this
    public static final String COL_9 = "Security_Answer"; //TODO: Jason remember to handle this
    public static final String TABLE_NAME1 = "medication";
    public static final String COL_10 = "Medication_ID";
    public static final String COL_11 = "Medication_Name";
    public static final String COL_12 = "Time_To_Take";
    public static final String COL_13 = "How_Much";
    public static final String COL_14 = "How_Long";
    public static final String COL_15 = "User_ID";
    public static String currentUser;
    public static int currentUserID;
    //TODO: Might need to add variables for "diet"

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE registeruser (ID INTEGER PRIMARY KEY AUTOINCREMENT, Username TEXT UNIQUE, Password TEXT, Name TEXT, Email TEXT, Address TEXT, Phone TEXT, Security_Question TEXT, Security_Answer TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE medication (Medication_ID INTEGER PRIMARY KEY AUTOINCREMENT, Medication_Name TEXT, Time_To_Take TEXT, How_Much TEXT, How_Long TEXT, User_ID INTEGER, FOREIGN KEY(User_ID) REFERENCES registeruser(ID) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        onCreate(sqLiteDatabase);

    }

    public boolean addUser(String user, String password, String name, String email, String address, String phone, String securityQuestion, String securityAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, user);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, name);
        contentValues.put(COL_5, email);
        contentValues.put(COL_6, address);
        contentValues.put(COL_7, phone);
        contentValues.put(COL_8, securityQuestion);
        contentValues.put(COL_9, securityAnswer);
        long res = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        if (res == -1)
            return false;
        else
            return true;
    }

    public boolean checkUserExists(String username, String password) {
        String[] columns = {COL_1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0)
            return true;
        else
            return false;
    }
//checkUser exists
    public boolean addMedication(String medName, String timeToTake, String howMuch, String howLong) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase read = getReadableDatabase();
        String[] columns = {COL_1};
        String selection = COL_2 + "=?";
        String[] loggedIn = {currentUser};
        //int id = getIntent().getIntExtra();
        //Log.d("User:",loggedIn[0]);
        Cursor cursor = read.query(TABLE_NAME,columns,selection,loggedIn,null,null,null);
        cursor.moveToFirst();
        currentUserID = cursor.getInt(0);
        //System.out.println(cursor.getInt(0));
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11, medName);
        contentValues.put(COL_12, timeToTake);
        contentValues.put(COL_13, howMuch);
        contentValues.put(COL_14, howLong);
        contentValues.put(COL_15, currentUserID);
        cursor.close();

        long res = db.insert(TABLE_NAME1, null, contentValues);
        db.close();
        viewMedicine();
        if (res == -1)
            return false;
        else
            return true;
    }

    public void viewMedicine(){
        SQLiteDatabase read = getReadableDatabase();
        String[] columns = {COL_11, COL_12, COL_13, COL_14};
        String selection = COL_15 + "=?";
        String[] loggedIn = {String.valueOf(currentUserID)};
        Cursor cursor = read.query(TABLE_NAME1, columns, selection, loggedIn,null,null,null);
        cursor.moveToFirst();
        try {
            while (cursor.moveToNext()) {
                for(int i = 0; i < 4; i++)
                System.out.println(cursor.getString(i));
            }
        } finally {
            cursor.close();
        }
    }
    //SELECT * FROM medication WHERE User_ID=currentUser;
    //SELECT * FROM medicine WHERE User_ID=currentUserID;

}
