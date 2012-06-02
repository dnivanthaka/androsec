package com.service.data;

import java.io.IOException;
import java.io.OutputStream;
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
		database.close();
	}
	
	public void saveAppInfo(){
		
		
	}
	
	public String getStraceOutput( String appName ) throws IOException{
		Process process = Runtime.getRuntime().exec("su");
		OutputStream os;
		os = process.getOutputStream();
		os.write("your linux command here".getBytes());
		os.close();
		//http://gimite.net/en/index.php?Run%20native%20executable%20in%20Android%20App
		
		/*
		 * try {
		    // Executes the command.
		    Process process = Runtime.getRuntime().exec("/system/bin/ls /sdcard");
		    
		    // Reads stdout.
		    // NOTE: You can write to stdin of the command using
		    //       process.getOutputStream().
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    int read;
		    char[] buffer = new char[4096];
		    StringBuffer output = new StringBuffer();
		    while ((read = reader.read(buffer)) > 0) {
		        output.append(buffer, 0, read);
		    }
		    reader.close();
		    
		    // Waits for the command to finish.
		    process.waitFor();
		    
		    return output.toString();
		} catch (IOException e) {
		    throw new RuntimeException(e);
		} catch (InterruptedException e) {
		    throw new RuntimeException(e);
		}
		 * */

		
		return null;
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
			database.replace(ServiceData.TABLE_APPSLIST, null, cv);
			//packageInfo.
        }
	}
}
