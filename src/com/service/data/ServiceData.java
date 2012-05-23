package com.service.data;

import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServiceData extends SQLiteOpenHelper {
	public static final String TABLE_APPSLIST             = "installed_apps";
	public static final String TABLE_APPSLIST_APPNAME     = "package_name";
	public static final String TABLE_APPSLIST_STRACE      = "strace_output";
	
	private static final String DATABASE_NAME = "mobsec.db";
	private static final int DATABASE_VERSION = 1;

	public ServiceData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
			// creating the app list table
		  /*
	      db.execSQL("CREATE TABLE applist (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, time"
	              + " INTEGER, appname TEXT NOT NULL);");
	      */
	      
			// creating the app list table
	      /*
	      db.execSQL("CREATE TABLE applist (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, time"
	              + " INTEGER, appname TEXT NOT NULL);");
	      */
	      db.execSQL("CREATE TABLE permission_levels ("+
	    		  	"level varchar(15)  NOT NULL,"+
	    		  	"permissions varchar(255)  NOT NULL,"+
	    		  	"PRIMARY KEY (level));");
	      
	      db.execSQL("CREATE TABLE location_details ("+
	    		  	"locationID varchar(15)  NOT NULL,"+
	    		  	"lat varchar(15)  NOT NULL,"+
	    		  	"lng varchar(15)  NOT NULL,"+
	    		  	"PRIMARY KEY (locationID, lat, lng));");
	      /*
	      db.execSQL("CREATE TABLE installed_apps ( "+
	    		    "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
	    		  	"package_name TEXT  NULL,"+
	    		  	"strace_output varchar(255)  NULL,"+
	    		  	"verified int(1)  NULL"+
	    		  	");");
	    	*/
	      db.execSQL("CREATE TABLE installed_apps (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, time"
	              + " INTEGER, package_name TEXT NOT NULL, " +
	                " strace_output varchar(255)  NULL, UNIQUE(package_name));");
	      
	      db.execSQL("CREATE TABLE location_permissions ("+
	    		  	"locationID varchar(15)  NOT NULL,"+
	    		  	"permission_level varchar(15)  NOT NULL,"+
	    		  	"PRIMARY KEY (locationID, permission_level));");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      //db.execSQL("DROP TABLE IF EXISTS applist");
			db.execSQL("DROP TABLE IF EXISTS permission_levels");
			db.execSQL("DROP TABLE IF EXISTS location_details");
			db.execSQL("DROP TABLE IF EXISTS installed_apps");
			db.execSQL("DROP TABLE IF EXISTS location_permissions");
	        onCreate(db);
		
	}

}
