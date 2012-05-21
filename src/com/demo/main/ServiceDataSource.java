package com.demo.main;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ServiceDataSource {
	private SQLiteDatabase database;
	private ServiceData dbHelper;
	private Context context;
	
	public ServiceDataSource( Context ctx ){
		dbHelper = new ServiceData(ctx);
		context = ctx;
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void saveAppInfo(){
		
		
	}
	
	public void saveAppsList(){
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ApplicationInfo> pkgAppsList = context.getPackageManager().getInstalledApplications( PackageManager.GET_META_DATA );
		
		for (ApplicationInfo packageInfo : pkgAppsList) {
			ContentValues cv = new ContentValues();
			/*
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG,
                    "Launch Activity :"
                            + pm.getLaunchIntentForPackage(packageInfo.packageName)); 
            */
			//packageInfo.
			Log.d("Service **", "Installed package :" + packageInfo.packageName + packageInfo.processName);
			cv.put(ServiceData.TABLE_APPSLIST_APPNAME, packageInfo.packageName);
			//database.insert(ServiceData.TABLE_APPSLIST, null, cv);
        }
	}
}
