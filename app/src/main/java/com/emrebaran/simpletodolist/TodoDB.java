package com.emrebaran.simpletodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mree on 12.11.2016.
 */

public class TodoDB extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbTodo";
    public static final String TABLE_TODO = "tbTodo";

    //Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TODO = "word";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_DONE = "done";


    public TodoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_TODO +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TODO + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_DONE + " INTEGER"
                + ")";
        db.execSQL(CREATE_WORDS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }


    // Adding new contact
    long addTodo(TodoClass td) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO, td.getTodo());
        values.put(KEY_DATE, td.getDate());
        values.put(KEY_TIME, td.getTime());
        values.put(KEY_DONE, td.getDone());

        long inserted_id;

        // Inserting Row
        inserted_id = db.insert(TABLE_TODO, null, values);

        db.close(); // Closing database connection

        return inserted_id;

    }


    // Getting All Words
    public List<TodoClass> getAllTodos() {
        List<TodoClass> tdList = new ArrayList<TodoClass>();
        // Select All Query Order By Days Left
        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " ORDER BY "+ KEY_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TodoClass td = new TodoClass();
                td.setID(Integer.parseInt(cursor.getString(0)));
                td.setTodo(cursor.getString(1));
                td.setDate(cursor.getString(2));
                td.setTime(cursor.getString(3));
                td.setDone(Integer.parseInt(cursor.getString(4)));

                // Adding people to list
                tdList.add(td);
            } while (cursor.moveToNext());
        }


        db.close();

        return tdList;
    }


    //get row count
    public int getRowCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TODO;
        Cursor cursor = db.rawQuery(selectQuery, null);

        int count = cursor.getCount();

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        db.close();

        return count;
    }



    // Updating done status
    public int updateDone(TodoClass td,int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DONE, td.getDone());

        // updating row
        return db.update(TABLE_TODO, values, KEY_ID + " = ?", new String[] { String.valueOf(id) });
    }




    // Deleting single todo
    public void deleteTodo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }


}