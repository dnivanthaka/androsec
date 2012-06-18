package com.service.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.demo.main.R;
import com.service.data.ServiceDataSource;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class ServiceDemo extends Service {

	private final int LocationTick = 15000;
	private final int MinDistance  = 0;
	private final int MaxDistance  = 0;
	
	private TimerTask notifyTask;
	Timer timer = new Timer();
	
	private LocationManager mgr;
	private Intent onStartIntent;
	private int notificationCounter;
	private ServiceDataSource data;
	private HashMap<String, String> runningProcsList;
	
	public static boolean isRunning = false;
	
	public class LocalBinder extends Binder {
		ServiceDemo getService() {
            return ServiceDemo.this;
        }
    }
	
	private class AppLaunchWatcher extends AsyncTask<Context, Void, Void>
	{
		
		
		@Override
		protected Void doInBackground(Context ... params) {
			
			List<String> appList = data.getSavedAppsList();

			while( isRunning ){
				String processName = "";
			    ActivityManager am = (ActivityManager)params[0].getSystemService(ACTIVITY_SERVICE);
			    List l = am.getRunningAppProcesses();
			    Iterator i = l.iterator();
			    PackageManager pm = params[0].getPackageManager();
			    runningProcsList.clear();
			    
			    while(i.hasNext()) 
			    {
			          ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			          try 
			          { 
			              
			                  CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
			                  Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
			                  if( appList.contains( info.processName ) ){
				                  if( !runningProcsList.containsValue( info.pid ) ){
				                  //if( !runningProcsList.containsKey( info.processName ) &&  !runningProcsList.containsValue( info.pid ) ){
				                	  runningProcsList.put(info.processName, String.valueOf( info.pid ));
				                	  
				                	  
				                	  // Add only one process at a time
				                	  break;
				                  }
			                  }
			                  //processName = c.toString();
			                  //processName = info.processName;
			              
			          }
			          catch(Exception e) 
			          {
			                //Log.d("Process", "Error>> :"+ e.toString());
			          }
			   }
			    
			    //l.clear();
			    
			    
			    /// Run strace
			    
			    Iterator it = runningProcsList.entrySet().iterator();
			    Process process = null;
			    try {
					process = Runtime.getRuntime().exec("su");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			    
			    while( it.hasNext() ){
			    	Map.Entry hm = (Map.Entry) it.next();
			    	Log.d("Process", "Id: "+ hm.getValue() +" ProcessName: "+ hm.getKey() );
			    	
				   
					try {
						if( !hm.getKey().toString().equals("com.android.alarmclock") && 
								!hm.getKey().toString().equals("com.android.launcher") &&
								!hm.getKey().toString().equals("com.android.settings") &&
								!hm.getKey().toString().equals("jp.co.omronsoft.openwnn") && 
								!hm.getKey().toString().equals("com.android.defcontainer")
								){
							
							if( hm.getKey().toString().equals("com.android.browser") ){
							//process = Runtime.getRuntime().exec("su");
							//process = Runtime.getRuntime().exec("echo 0 > /proc/sys/kernel/yama/ptrace_scope");
						
				    		//Process process = Runtime.getRuntime().exec("su");
				    		//OutputStream os;
				    		//os = process.getOutputStream();
							synchronized (process) {
								
							
				    		DataOutputStream os = new DataOutputStream(process.getOutputStream());
				    		//String com = "/system/xbin/strace -p 124 -o /sdcard/strace/stout.txt";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -o /sdcard/strace/"+hm.getKey()+".txt -e trace=open,close uname";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -s 200 -o /sdcard/strace/"+hm.getKey()+".txt";
				    		String com = "/system/xbin/strace -p "+hm.getValue()+" -s 200 -o /sdcard/strace/"+hm.getKey()+".txt";
				    		//String com = "/sdcard/strace/strace -p "+hm.getValue()+" -o /sdcard/strace/"+hm.getKey()+".txt -e trace=open,close uname";
				    		//String com = "/system/xbin/strace -p 124";
				    		Log.d("XX##XX", com);
				    		os.writeBytes( com );
				    		os.flush();
				    		os.close();
				    		
				    		
				    		//process.wait(5000);
				    		//process.destroy();
				    		//process.exitValue()
				    		process.waitFor();
							}
							}
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block 
						//e1.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
			    }
			}
			
			return null;
		}
	}
	
	private String getAppName(int pID)
	{
	    String processName = "";
	    ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
	    List l = am.getRunningAppProcesses();
	    Iterator i = l.iterator();
	    PackageManager pm = this.getPackageManager();
	    while(i.hasNext()) 
	    {
	          ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
	          try 
	          { 
	              if(info.pid == pID)
	              {
	                  CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
	                  Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
	                  //processName = c.toString();
	                  //processName = info.processName;
	              }
	          }
	          catch(Exception e) 
	          {
	                //Log.d("Process", "Error>> :"+ e.toString());
	          }
	   }
	    return processName;
	}


	
	@Override
	public void onCreate(){
		super.onCreate();
		data = new ServiceDataSource( this );
		runningProcsList = new HashMap<String, String>();
		
		notificationCounter = 0;
		
		if( mgr != null ){
			mgr.removeUpdates( locationListener );
			mgr = null;
		}
		
		mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Log.d("onCreate()", "Service Created");
		
		data.open();
		data.saveAppsList();
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
			sendNotification( onStartIntent, System.currentTimeMillis(), location.toString(), ++notificationCounter );
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
		
		onStartIntent = intent;
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		String provider = mgr.getBestProvider(criteria, true);
		
		Log.d("bestProvider", provider);
		dumpLocation( mgr.getLastKnownLocation( provider ) );
		
		mgr.requestLocationUpdates(provider, LocationTick, 0, locationListener);
		
		Log.d("onStartCommand()", "Service Started");
		
		isRunning = true;
		
		//new AppLaunchWatcher().execute(null);
		new AppLaunchWatcher().execute( this );
		
		notifyTask = new TimerTask(){
			int i = 0;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				i++;
				Log.d("run()", "Service Running: value = "+i);
				
				AppGlobal.getinstance().setServiceStatus( true );
				
				long when = System.currentTimeMillis();
				
				
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
		
		data.close();
		
		//this.deleteDatabase( "mobsec.db" );
		isRunning = false;
		AppGlobal.getinstance().setServiceStatus( false );
		
		
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
	
	private void sendNotification( Intent intent, long when, String message, int i ){
		final NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		final PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
		
		Notification notify = new Notification( R.drawable.ic_launcher, "ServiceDemo", when );
		notify.setLatestEventInfo(ServiceDemo.this, "ServiceDemo", message, contentIntent);
		
		notificationManager.notify( i, notify );
	}
}
