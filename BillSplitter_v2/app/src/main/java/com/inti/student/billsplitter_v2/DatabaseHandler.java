package com.inti.student.billsplitter_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ady on 14/6/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "billHistory",
            TABLE_HISTORY = "bill",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PRICE = "price";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_HISTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PRICE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);

        onCreate(db);
    }

    public void createHistory(Bill_list bill_list) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, bill_list.getName());
        values.put(KEY_PRICE, bill_list.getPrice());

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    /*public Bill_list getHistory (int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[] { KEY_ID, KEY_NAME, KEY_PRICE }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Bill_list bill_list = new Bill_list(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        db.close();
        cursor.close();
        return bill_list;
    }*/

    public void deleteHistory(Bill_list bill_list) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_NAME + "=?", new String[] { String.valueOf(bill_list.getName()) });
        db.close();
    }


    public int getHistoryCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HISTORY, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

   /*public int updateHistory(Bill_list bill_list) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, bill_list.getName());
        values.put(KEY_PRICE, bill_list.getPrice());
        int rowsAffected = db.update(TABLE_HISTORY, values, KEY_ID + "=?", new String[] { String.valueOf(bill_list.getId()) });
        db.close();

        return rowsAffected;
    }*/

    public List<Bill_list> getAllHistory() {
        List<Bill_list> bill_lists = new ArrayList<Bill_list>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HISTORY, null);

        if (cursor.moveToFirst()) {
            do {
                bill_lists.add(new Bill_list(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bill_lists;
    }

    public void deleteAllHistory (){

        SQLiteDatabase db= getWritableDatabase();
        db.delete(TABLE_HISTORY,null,null);
        db.close();
    }
}
