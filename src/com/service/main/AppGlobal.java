package com.service.main;

import android.app.Application;
import android.util.Log;

public class AppGlobal extends Application {
	private boolean serviceRunning = false;
	private static AppGlobal instance;
	private String appToTrack = null;
	private String appToTrackName = "";
	private int stracePID = -1;
	
	private AppGlobal(){}
	
	static{
		instance = new AppGlobal();
	}
	
	public static AppGlobal getinstance(){
		return AppGlobal.instance;
	}
	
	public boolean getServiceStatus(){
		return serviceRunning;
	}
	
	public void setServiceStatus( boolean s ){
		this.serviceRunning = s;
	}
	
	public void setAppToTrack(String s){
		appToTrack = s;
	}
	
	public String getAppToTrack(){
		return appToTrack;
	}
	
	public void setAppToTrackName(String s){
		appToTrackName = s;
	}
	
	public String getAppToTrackName(){
		return appToTrackName;
	}
	
	public void setStracePID(int s){
		Log.d("*******PIDSET", ""+s);
		stracePID = s;
	}
	
	public int getStracePID(){
		return stracePID;
	}
}
