package com.demo.main;

import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServiceData extends SQLiteOpenHelper {
	public static final String TABLE_APPSLIST             = "applist";
	public static final String TABLE_APPSLIST_APPNAME     = "appname";
	public static final String TABLE_APPSLIST_INSTALLTIME = "appname";
	
	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 1;

	public ServiceData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
			// creating the app list table
	      db.execSQL("CREATE TABLE applist (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, time"
	              + " INTEGER, appname TEXT NOT NULL);");
	      
			// creating the app list table
	      /*
	      db.execSQL("CREATE TABLE applist (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, time"
	              + " INTEGER, appname TEXT NOT NULL);");
	      */
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      db.execSQL("DROP TABLE IF EXISTS applist");
	      onCreate(db);
		
	}

}
