package com.service.data;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ServiceDataSource {
	private SQLiteDatabase database;
	private ServiceData dbHelper;
	private Context context;
	
	int locationRangeMeters = 100;
	int earthRadius         = 6371;
	//double deltaDegrees     = ((locationRangeMeters/1000) / earthRadius) * (180 / 3.142);
	double deltaDegrees     = 0.002;
	
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
		PackageManager pm = context.getPackageManager();
		final List<ApplicationInfo> pkgAppsList = pm.getInstalledApplications( PackageManager.GET_META_DATA );
		
		for (ApplicationInfo packageInfo : pkgAppsList) {
			ContentValues cv = new ContentValues();
			/*
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG,
                    "Launch Activity :"
                            + pm.getLaunchIntentForPackage(packageInfo.packageName)); 
            */
			//packageInfo
			Log.d("Service **", "Installed package :" + packageInfo.packageName + packageInfo.processName);
			cv.put(ServiceData.TABLE_APPSLIST_APPNAME, packageInfo.loadLabel(pm).toString());
			cv.put(ServiceData.TABLE_APPSLIST_PCKNAME, packageInfo.packageName);
			//database.insert(ServiceData.TABLE_APPSLIST, null, cv);
			database.replace(ServiceData.TABLE_APPSLIST, null, cv);
			//packageInfo.
        }
	}
	
	public void savePackagesList(){
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm = context.getPackageManager();
		final List<PackageInfo> pkgAppsList = pm.getInstalledPackages( PackageManager.GET_PERMISSIONS );
		
		for (PackageInfo packageInfo : pkgAppsList) {
			ContentValues cv = new ContentValues();
			/*
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG,
                    "Launch Activity :"
                            + pm.getLaunchIntentForPackage(packageInfo.packageName)); 
            */
			//packageInfo
			//Log.d("Service **", "Installed package :" + packageInfo.packageName + packageInfo.processName);
			//cv.put(ServiceData.TABLE_APPSLIST_APPNAME, packageInfo.loadLabel(pm).toString());
			
			PermissionInfo[] permsList = packageInfo.permissions;
			for( PermissionInfo perm : permsList ){
				
			}
			//cv.put(ServiceData.TABLE_APPSLIST_PCKNAME, packageInfo.packageName);
			//database.insert(ServiceData.TABLE_APPSLIST, null, cv);
			//database.replace(ServiceData.TABLE_APPSLIST, null, cv);
			//packageInfo.
        }
	}
	
	public void clearAppsList(){
		//database.rawQuery("DELETE FROM "+ServiceData.TABLE_APPSLIST, null);
		database.delete(ServiceData.TABLE_APPSLIST, null, null);
		Log.d("Service **", "CLEARING OUT :");
	}
	
	public List<String> getSavedAppsList(){
		List<String> lst = new ArrayList<String>();
		//String select = "SELECT package_name FROM installed_apps";
		Cursor cursor = database.query(ServiceData.TABLE_APPSLIST, new String[] {ServiceData.TABLE_APPSLIST_PCKNAME}, 
                null, null, null, null, null);
		cursor.moveToFirst();
		while( cursor.isAfterLast() == false ){
			Log.d("XXXXXXXX", "Installed package :" + cursor.getString(0));
			lst.add( cursor.getString(0) );
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return lst;
	}
	/*
	public void updateMalwaresList( String pkName, String names, String threatLevel ){
		ContentValues cv = new ContentValues();
		
		//Log.d("Service **", "Installed package :" + packageInfo.packageName + packageInfo.processName);
		cv.put(ServiceData.TABLE_MALWARE_PACKAGE, pkName);
		cv.put(ServiceData.TABLE_MALWARE_NAMES, names);
		cv.put(ServiceData.TABLE_MALWARE_THREAT_LEVEL, threatLevel);
		//database.insert(ServiceData.TABLE_APPSLIST, null, cv);
		database.replace(ServiceData.TABLE_MALWARES_LIST, null, cv);
	}
	*/
	/*
	public Cursor getMalwaresList(){
		Cursor cursor = database.query(ServiceData.TABLE_MALWARES_LIST, new String[] {
				ServiceData.TABLE_MALWARE_PACKAGE, ServiceData.TABLE_MALWARE_NAMES, ServiceData.TABLE_MALWARE_THREAT_LEVEL
				}, 
                null, null, null, null, null);
		
		return cursor;
	}
	*/
	
	public void updateAppTraceStatus(String app, String st, String netp, String filename){
		ContentValues cv = new ContentValues();
		
		cv.put(ServiceData.TABLE_APPSLIST_STRACED, st);
		
		Date todaysDate = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = formatter.format(todaysDate);
		
		cv.put(ServiceData.TABLE_APPSLIST_TRACEDATE, formattedDate);
		//cv.put(ServiceData.TABLE_APPSLIST_TRACEDATE, "Date('now')");
		cv.put(ServiceData.TABLE_APPSLIST_TRACENETW, netp);
		cv.put(ServiceData.TABLE_APPSLIST_STRACE, filename);
		
		Log.d(ServiceDataSource.class.getName(), "APP = "+app+" ST = "+st);
		
		String where = ServiceData.TABLE_APPSLIST_PCKNAME+"=?";
		String[] whereArgs = { app };
		
		database.update(ServiceData.TABLE_APPSLIST, cv, where, whereArgs);
	}
	
	public Cursor getSavedAppsListCursor(){
		Cursor cursor = database.query(ServiceData.TABLE_APPSLIST, new String[] {
				ServiceData.TABLE_APPSLIST_APPNAME, ServiceData.TABLE_APPSLIST_PCKNAME, "is_straced"}, 
                null, null, null, null, null);
		
		return cursor;
	}
	
	public Cursor getSavedAppReportCursor(){
		Cursor cursor = database.query(ServiceData.TABLE_APP_REPORT, new String[] {
				ServiceData.TABLE_APP_REPORT_APPNAME, ServiceData.TABLE_APP_REPORT_PCKNAME, ServiceData.TABLE_APP_REPORT_SCORE}, 
                null, null, null, null, null);
		
		return cursor;
	}
	
	public Cursor getStracedAppsCursor(){
		String where = ServiceData.TABLE_APPSLIST_STRACED+"=?";
		String[] whereArgs = { "1" };
		
		Cursor cursor = database.query(ServiceData.TABLE_APPSLIST, new String[] {
				ServiceData.TABLE_APPSLIST_APPNAME, ServiceData.TABLE_APPSLIST_PCKNAME, ServiceData.TABLE_APPSLIST_STRACE, 
				ServiceData.TABLE_APPSLIST_STRACED, ServiceData.TABLE_APPSLIST_TRACEDATE, ServiceData.TABLE_APPSLIST_TRACENETW
				}, 
				where, whereArgs, null, null, null);
		
		return cursor;
	}
	
	public void SaveAppReport( String appName, String appPck, String details, String score ){
		ContentValues cv = new ContentValues();
		cv.put(ServiceData.TABLE_APP_REPORT_APPNAME, appName);
		cv.put(ServiceData.TABLE_APP_REPORT_PCKNAME, appPck);
		cv.put(ServiceData.TABLE_APP_REPORT_DETAILS, details);
		cv.put(ServiceData.TABLE_APP_REPORT_SCORE, score);
		
		Log.d("APPREPORT", "Package :" + appName);
		
		database.replace(ServiceData.TABLE_APP_REPORT, null, cv);
	}
	
	public boolean checkMalwarePresent( String packageName ){
		Cursor cursor = database.query(ServiceData.TABLE_APPSLIST, new String[] {ServiceData.TABLE_APPSLIST_PCKNAME}, 
                "package_name = '"+packageName+"'", null, null, null, null);
		
		if (cursor.getCount() == 0 ){
			cursor.close();
			return false;
		}else{
			cursor.close();
			return true;
		}
	}
	
	public Cursor getSavedLocPermsListCursor(){
		Cursor cursor = database.query(ServiceData.TABLE_LOCATION_PERMS, new String[] {
				ServiceData.TABLE_LOCATION_PERMS_NAME,
				ServiceData.TABLE_LOCATION_PERM_TYPE,
				ServiceData.TABLE_LOCATION_PERM_SSID,
				ServiceData.TABLE_LOCATION_PERMS_LAT,
				ServiceData.TABLE_LOCATION_PERMS_LNG,
				ServiceData.TABLE_LOCATION_PERMS_WIFI,
				ServiceData.TABLE_LOCATION_PERMS_BLU,
				ServiceData.TABLE_LOCATION_PERMS_SCR
				}, 
                null, null, null, null, null);
		
		return cursor;
	}
	
	public Cursor getLocationPermissions(Double lat, Double lng){
		Cursor cursor = database.query(ServiceData.TABLE_LOCATION_PERMS, 
				new String[] {
					ServiceData.TABLE_LOCATION_PERMS_NAME,
					ServiceData.TABLE_LOCATION_PERM_TYPE,
					ServiceData.TABLE_LOCATION_PERM_SSID,
					ServiceData.TABLE_LOCATION_PERMS_LAT, 
					ServiceData.TABLE_LOCATION_PERMS_LNG,
					ServiceData.TABLE_LOCATION_PERMS_WIFI,
					ServiceData.TABLE_LOCATION_PERMS_BLU,
					ServiceData.TABLE_LOCATION_PERMS_SCR
				}, 
				ServiceData.TABLE_LOCATION_PERMS_LAT+" = "+String.valueOf(lat)+" AND "+ServiceData.TABLE_LOCATION_PERMS_LNG+" = "+String.valueOf(lng), 
				null, null, null, null );
		
				//Log.d("DELTA", "D :" + deltaDegrees);

		return cursor;
	}
	
	public Cursor getLocationPermissions(String ssid){
		Cursor cursor = database.query(ServiceData.TABLE_LOCATION_PERMS, 
				new String[] {
					ServiceData.TABLE_LOCATION_PERMS_NAME,
					ServiceData.TABLE_LOCATION_PERM_TYPE,
					ServiceData.TABLE_LOCATION_PERM_SSID,
					ServiceData.TABLE_LOCATION_PERMS_LAT, 
					ServiceData.TABLE_LOCATION_PERMS_LNG,
					ServiceData.TABLE_LOCATION_PERMS_WIFI,
					ServiceData.TABLE_LOCATION_PERMS_BLU,
					ServiceData.TABLE_LOCATION_PERMS_SCR
				}, 
				ServiceData.TABLE_LOCATION_PERM_SSID+" = '"+ssid+"'", 
				null, null, null, null );
		
				//Log.d("DELTA", "D :" + deltaDegrees);

		return cursor;
	}
	
	public Cursor getLocationPermissionRange(Double lat, Double lng){
		Cursor cursor = database.query(ServiceData.TABLE_LOCATION_PERMS, 
				new String[] {
					ServiceData.TABLE_LOCATION_PERMS_NAME,
					ServiceData.TABLE_LOCATION_PERM_TYPE,
					ServiceData.TABLE_LOCATION_PERM_SSID,
					ServiceData.TABLE_LOCATION_PERMS_LAT, 
					ServiceData.TABLE_LOCATION_PERMS_LNG,
					ServiceData.TABLE_LOCATION_PERMS_WIFI,
					ServiceData.TABLE_LOCATION_PERMS_BLU,
					ServiceData.TABLE_LOCATION_PERMS_SCR
				}, 
				"("+ServiceData.TABLE_LOCATION_PERMS_LAT+" >= "+String.valueOf((lat - deltaDegrees))+" AND "+ServiceData.TABLE_LOCATION_PERMS_LAT+" <= "+String.valueOf((lat + deltaDegrees))+") " +
						"AND ("+ServiceData.TABLE_LOCATION_PERMS_LNG+" >= "+String.valueOf((lng - deltaDegrees)) +" AND "+ServiceData.TABLE_LOCATION_PERMS_LNG+" <= "+String.valueOf((lng + deltaDegrees))+")", 
				null, null, null, null );
		
				//Log.d("DELTA", "D :" + String.valueOf((lat - deltaDegrees)));
				
		return cursor;
	}
	
	public Cursor getGeneralPermissions(String chr){
		Cursor cursor = database.query(ServiceData.TABLE_LOCATION_PERMS, 
				new String[] {
					ServiceData.TABLE_LOCATION_PERMS_NAME,
					ServiceData.TABLE_LOCATION_PERM_TYPE,
					ServiceData.TABLE_LOCATION_PERM_SSID,
					ServiceData.TABLE_LOCATION_PERMS_LAT, 
					ServiceData.TABLE_LOCATION_PERMS_LNG,
					ServiceData.TABLE_LOCATION_PERMS_WIFI,
					ServiceData.TABLE_LOCATION_PERMS_BLU,
					ServiceData.TABLE_LOCATION_PERMS_SCR
				}, 
				ServiceData.TABLE_LOCATION_PERMS_NAME+" = '"+chr.toString()+"'", 
				null, null, null, null );

		return cursor;
	}
	
	public void saveLocationPermission( String name, String type, String type_ssid, Double lat, Double lng, String wifi, String blu, String scr ){
		ContentValues cv = new ContentValues();
		
		cv.put(ServiceData.TABLE_LOCATION_PERMS_NAME, name);
		cv.put(ServiceData.TABLE_LOCATION_PERM_TYPE, type);
		cv.put(ServiceData.TABLE_LOCATION_PERM_SSID, type_ssid);
		cv.put(ServiceData.TABLE_LOCATION_PERMS_LAT, lat);
		cv.put(ServiceData.TABLE_LOCATION_PERMS_LNG, lng);
		cv.put(ServiceData.TABLE_LOCATION_PERMS_WIFI, wifi);
		cv.put(ServiceData.TABLE_LOCATION_PERMS_BLU, blu);
		cv.put(ServiceData.TABLE_LOCATION_PERMS_SCR, scr);
		
		//Log.d("XXXXXXXX", "Lat :" + cursor.getString(0));
		/*
		String sql = "INSERT INTO "+ServiceData.TABLE_LOCATION_PERMS+" (_id, loc_name, lat, lng, wifi_s, blu_s, scr_s) " +
				"VALUES (2, "+name+", "+lat+", "+lng+", "+wifi+", "+blu+", "+scr+")";
				*/
		
		String sql = "INSERT INTO "+ServiceData.TABLE_LOCATION_PERMS+" (loc_name, perm_type, perm_ssid, lat, lng, wifi_s, blu_s, scr_s) " +
				"VALUES ('"+name+"', '"+type+"', '"+type_ssid+"', '"+lat+"', '"+lng+"', '"+wifi+"', '"+blu+"', '"+scr+"')";
		Log.d("########", "Query :" + sql);
		//database.rawQuery(sql, null);
		
		database.insert(ServiceData.TABLE_LOCATION_PERMS, null, cv);
	}
	
	public void deletePermissionSetting( String permName ){
		//String sql = "DELETE FROM "+ServiceData.TABLE_LOCATION_PERMS+" WHERE "+ServiceData.TABLE_LOCATION_PERMS_NAME+" = '"+permName+"'";
		//Log.d("########", "Query :" + sql);
		//database.rawQuery(sql, null);
		database.delete(ServiceData.TABLE_LOCATION_PERMS, ServiceData.TABLE_LOCATION_PERMS_NAME + "=?", new String[]{ permName });
	}
	
	public boolean checkLocationPermission( Long lat, Long lng ){
		Cursor mCursor =
				database.query(true, ServiceData.TABLE_LOCATION_PERMS, new String[] {
						ServiceData.TABLE_LOCATION_PERMS_NAME}, 
						ServiceData.TABLE_LOCATION_PERMS_LAT + "=" + lat.toString() + " AND  "+ServiceData.TABLE_LOCATION_PERMS_LNG + "=" + lng.toString(), 
                        null,
                        null, 
                        null, 
                        null, 
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            if(mCursor.getCount() > 0){
            	mCursor.close();
            	return true;
            }else{
            	mCursor.close();
            	return false;
            }
        }else{
        	return false;
        }

	}
	
	public boolean checkLocationPermission( String type, String ssid ){
		Cursor mCursor =
				database.query(true, ServiceData.TABLE_LOCATION_PERMS, new String[] {
						ServiceData.TABLE_LOCATION_PERMS_NAME}, 
						ServiceData.TABLE_LOCATION_PERM_TYPE + "='" + type + "' AND "+ServiceData.TABLE_LOCATION_PERM_SSID + "='"+ssid+"'", 
                        null,
                        null, 
                        null, 
                        null, 
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            if(mCursor.getCount() > 0){
            	mCursor.close();
            	return true;
            }else{
            	mCursor.close();
            	return false;
            }
        }else{
        	return false;
        }

	}
	/*
	public String getApptoTrack2(){
		//return "com.android.browser";
		return "com.android.calculator2";
		
	}
	*/
	
	public void saveGlobalParam(String name, String value)
	{
		ContentValues cv = new ContentValues();
		cv.put(ServiceData.TABLE_GLOBAL_SETTINGS_SETTING_VALUE, value);
		
		String where = ServiceData.TABLE_GLOBAL_SETTINGS_SETTING_NAME+"=?";
		String[] whereArgs = { name };
		
		database.update(ServiceData.TABLE_GLOBAL_SETTINGS, cv, where, whereArgs);
	}
	
	public String getGlobalParam(String name)
	{
		String where = ServiceData.TABLE_GLOBAL_SETTINGS_SETTING_NAME+"=?";
		String[] whereArgs = { name };
		String value = "";
		
		
		
		/*
		if(!cursor.moveToFirst())
			return "";
		else{
			value = cursor.getString(0);
			
			return value;
		}*/
		
		Cursor cursor = database.rawQuery(
				"SELECT setting_value FROM "+ServiceData.TABLE_GLOBAL_SETTINGS+" WHERE "+ServiceData.TABLE_GLOBAL_SETTINGS_SETTING_NAME+" = '"+name+"'", null);
		cursor.moveToFirst();
		value = cursor.getString(0);
		
		cursor.close();
		return value;
	}
	
	
}
