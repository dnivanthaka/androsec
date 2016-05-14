package com.service.data;

import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServiceData extends SQLiteOpenHelper {
	public static final String TABLE_APPSLIST                       = "installed_apps";
	public static final String TABLE_APPSLIST_APPNAME               = "app_name";
	public static final String TABLE_APPSLIST_PCKNAME               = "package_name";
	public static final String TABLE_APPSLIST_TRACEDATE             = "traced_date";
	public static final String TABLE_APPSLIST_TRACENETW             = "traced_network";
	public static final String TABLE_LOCATION_PERMS                 = "location_permissions";
	public static final String TABLE_LOCATION_PERMS_NAME            = "loc_name";
	public static final String TABLE_LOCATION_PERM_TYPE             = "perm_type";
	public static final String TABLE_LOCATION_PERM_SSID             = "perm_ssid";
	public static final String TABLE_LOCATION_PERMS_LAT             = "lat";
	public static final String TABLE_LOCATION_PERMS_LNG             = "lng";
	public static final String TABLE_LOCATION_PERMS_WIFI            = "wifi_s";
	public static final String TABLE_LOCATION_PERMS_BLU             = "blu_s";
	public static final String TABLE_LOCATION_PERMS_SCR             = "scr_s";
	public static final String TABLE_GLOBAL_SETTINGS_SETTING_NAME   = "setting_name";
	public static final String TABLE_GLOBAL_SETTINGS_SETTING_VALUE  = "setting_value";
	
	
	public static final String TABLE_APPSLIST_STRACED     = "is_straced";
	public static final String TABLE_APPSLIST_STRACE      = "strace_output";
	public static final String TABLE_GLOBAL_SETTINGS      = "app_settings";
	
	/*
	public static final String TABLE_MALWARES_LIST         = "malware_list";
	public static final String TABLE_MALWARE_PACKAGE       = "package_name";
	public static final String TABLE_MALWARE_NAMES         = "other_names";
	public static final String TABLE_MALWARE_THREAT_LEVEL  = "threat_level";
	public static final String TABLE_GLOBAL_SETTINGS       = "app_settings";
	*/
	public static final String TABLE_APP_REPORT            = "app_report";
	public static final String TABLE_APP_REPORT_APPNAME    = "app_name";
	public static final String TABLE_APP_REPORT_PCKNAME    = "package_name";
	public static final String TABLE_APP_REPORT_DETAILS    = "details";
	public static final String TABLE_APP_REPORT_SCORE      = "behaviour_level";

	
	private static final String DATABASE_NAME = "mobsec.db";
	private static final int DATABASE_VERSION = 2;

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
	              + " INTEGER, app_name TEXT NOT NULL, "
	              + " package_name TEXT NOT NULL, is_straced"
	              + " INT(1), traced_date TEXT NULL, traced_network TEXT NULL, "+
	                " strace_output varchar(255)  NULL, UNIQUE(package_name));");
	      
	      /*
	      db.execSQL("CREATE TABLE location_permissions ("+
	    		  	"locationID varchar(15)  NOT NULL,"+
	    		  	"permission_level varchar(15)  NOT NULL,"+
	    		  	"PRIMARY KEY (locationID, permission_level));");
	    */
	      
	      db.execSQL("CREATE TABLE location_permissions ("+ _ID
	    		  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	    		  + "loc_name varchar(50) NOT NULL,"
	    		  +	"perm_type varchar(15)  NOT NULL,"
	    		  +	"perm_ssid varchar(25)  NULL,"
	    		  +	"lat REAL  NOT NULL,"
	    		  +	"lng REAL  NOT NULL,"
	    		  +	"wifi_s varchar(15)  NOT NULL,"
	    		  +	"blu_s varchar(15)  NOT NULL,"
	    		  +	"scr_s varchar(15)  NOT NULL"
	    		  +	");");
	      /*
	      db.execSQL("CREATE TABLE malware_list (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, package_name"
	              + " TEXT, other_names TEXT NOT NULL, details"
	              + " TEXT, threat_level varchar(25), "+
	                " UNIQUE(package_name));");
	      */
	      db.execSQL("CREATE TABLE app_report (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, app_name TEXT, "
	              + " package_name TEXT, details"
	              + " TEXT, behaviour_level varchar(25), "+
	                " UNIQUE(package_name));");
	      
	      db.execSQL("CREATE TABLE app_settings (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, setting_name"
	              + " TEXT, setting_value TEXT NOT NULL, "+
	                " UNIQUE(setting_name));");
	      
	      db.execSQL("INSERT INTO app_settings (setting_name, setting_value) VALUES ('trace_upload_path', '-')");
	      db.execSQL("INSERT INTO app_settings (setting_name, setting_value) VALUES ('malware_list_path', '-')");
	      db.execSQL("INSERT INTO app_settings (setting_name, setting_value) VALUES ('upload_only_wifi', 'Y')");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      //db.execSQL("DROP TABLE IF EXISTS applist");
			db.execSQL("DROP TABLE IF EXISTS permission_levels");
			db.execSQL("DROP TABLE IF EXISTS location_details");
			db.execSQL("DROP TABLE IF EXISTS installed_apps");
			db.execSQL("DROP TABLE IF EXISTS location_permissions");
			//db.execSQL("DROP TABLE IF EXISTS malware_list");
			db.execSQL("DROP TABLE IF EXISTS app_report");
			db.execSQL("DROP TABLE IF EXISTS app_settings");
	        onCreate(db);
		
	}

}
