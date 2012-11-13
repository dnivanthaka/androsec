package com.service.main;

import android.app.Application;

public class AppGlobal extends Application {
	private boolean serviceRunning = false;
	private static AppGlobal instance;
	private String appToTrack = null;
	
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
}
