package com.demo.main;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ServiceDemo extends Service {

	private final int LocationTick = 15000;
	private final int MinDistance  = 0;
	private final int MaxDistance  = 0;
	
	private TimerTask notifyTask;
	Timer timer = new Timer();
	
	private LocationManager mgr;
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("onCreate()", "Service Created");
	}
	
	// This is the old onStart method that will be called on the pre-2.0
	// platform.  On 2.0 or later we override onStartCommand() so this
	// method will not be called.

	/*
	@Override
	public void onStart( Intent intent, int startId ){
		super.onStart(intent, startId);
		Log.d("onStart()", "Service Started");
	}
	*/
	
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.d("onLocationChanged()", "Location Update");
			dumpLocation( location );
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ){
		//super.onStartCommand(intent, flags, startId);
		
		final NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		final PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
		
		
		if( mgr != null ){
			mgr.removeUpdates( locationListener );
			mgr = null;
		}
		
		mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		String provider = mgr.getBestProvider(criteria, true);
		
		Log.d("bestProvider", provider);
		dumpLocation( mgr.getLastKnownLocation( provider ) );
		
		mgr.requestLocationUpdates(provider, LocationTick, 0, locationListener);
		
		Log.d("onStartCommand()", "Service Started");
		
		notifyTask = new TimerTask(){
			int i = 0;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				i++;
				Log.d("run()", "Service Running: value = "+i);
				
				long when = System.currentTimeMillis();
				
				Notification notify = new Notification( R.drawable.ic_launcher, "ServiceDemo", when );
				notify.setLatestEventInfo(ServiceDemo.this, "ServiceDemo", "Testing Service", contentIntent);
				
				notificationManager.notify( i++, notify );
			}
			
		};
		
		//timer.schedule( notifyTask, 1000, 10000 );
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.

		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		if( mgr != null ){
			mgr.removeUpdates( locationListener );
			mgr = null;
		}
		
		if( timer != null ){
			timer.cancel();
		}
		
		Log.d("onDestroy()", "Service Destroyed");
	}
	
	@Override
	public IBinder onBind( Intent intent ){
		return null;
	}
	
	private void dumpLocation( Location location ){
		if( location == null ){
			Log.d("dumpLocation()", "Location [unknown]");
		}else{
			Log.d("dumpLocation()", location.toString());
		}
	}
}
