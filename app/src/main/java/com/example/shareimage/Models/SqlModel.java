package com.example.shareimage.Models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.shareimage.ViewModels.MyApplication;

import java.util.LinkedList;
import java.util.List;

public class SqlModel {
    final public static SqlModel instance = new SqlModel();
    MyHelper mDbHelper;

    //TODO: for models like user
    public SqlModel() {
        mDbHelper = new MyHelper(MyApplication.getAppContext());
    }

    public List<UserModel> getAllUsers() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        LinkedList<UserModel> data = new LinkedList<UserModel>();
        Cursor cursor = db.query("users", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int emailIndex = cursor.getColumnIndex("email");
            int passwordIndex = cursor.getColumnIndex("password");
            int userNameIndex = cursor.getColumnIndex("userName");
            int fullNameIndex = cursor.getColumnIndex("fullName");
            int bioIndex = cursor.getColumnIndex("bio");
            int imageIndex = cursor.getColumnIndex("image");

            do {
                String id = cursor.getString(idIndex);
                String email = cursor.getString(emailIndex);
                String password = cursor.getString(passwordIndex);
                String userName = cursor.getString(userNameIndex);
                String fullName = cursor.getString(fullNameIndex);
                String bio = cursor.getString(bioIndex);
                String image = cursor.getString(imageIndex);
                UserModel user = new UserModel(id, email, password, userName, fullName, image, bio);
                data.add(user);
            } while (cursor.moveToNext());
        }
        return data;
    }

    public void addUser(UserModel user) {
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("userName", user.getUserName());
        values.put("fullName", user.getFullName());
        values.put("bio", user.getBio());
        values.put("image", user.getImageUrl());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert("users", "id", values);
    }

    class MyHelper extends SQLiteOpenHelper {

        public MyHelper(@Nullable Context context) {
            super(context, "database.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //TODO: set users info in the table
            db.execSQL("create table users(id text primary key,email text,password text,userName text,fullName text,bio text,image text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//if upgrade version drop and new
            db.execSQL("drop table if exists users");
            //TODO: same like before
            db.execSQL("create table users(id text primary key,email text,password text,userName text,fullName text,bio text,image text)");

        }


    }
}
