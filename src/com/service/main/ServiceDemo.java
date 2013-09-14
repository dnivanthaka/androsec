package com.service.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.demo.main.R;
import com.service.data.ServiceData;
import com.service.data.ServiceDataSource;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
	//private AppLaunchWatcher appWatchTask;
	private HashMap<String, String> runningProcsList;
	
	private Timer autoUpdate;
	TracerThread thrd;

	
	public static boolean isRunning = false;
	
	public class LocalBinder extends Binder {
		ServiceDemo getService() {
            return ServiceDemo.this;
        }
    }
	
	
/*	private class AppLaunchWatcher extends AsyncTask<Context, Void, Void>
	{
		
		@Override
        protected void onPreExecute(){
			//process = Runtime.getRuntime().exec("su");
        }

		
		
		@Override
		protected Void doInBackground(Context ... params) {
			
			List<String> appList = data.getSavedAppsList();
			//String trackApp = data.getApptoTrack();
			String trackApp = AppGlobal.getinstance().getAppToTrack();
			//String trackApp = "com.android.browser";
			//String trackApp = "com.android.gallery";
			//String trackApp = "";
			
			//Log.d("Spinner2@@", "asasasas");
			
			boolean isProcessing = false;

			while( isRunning ){
				
				
				if( isProcessing ) continue;
				
				//isProcessing = true;
				
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
			              
			        	  	  trackApp = AppGlobal.getinstance().getAppToTrack();
			                  CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
			                  //Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
			                  //if( appList.contains( info.processName ) ){
				                  //if( !runningProcsList.containsValue( info.pid ) ){
				                  //if( !runningProcsList.containsKey( info.processName ) &&  !runningProcsList.containsValue( info.pid ) ){
			                  	if( info.processName.equals( trackApp ) ){
			                  		Log.d("KK Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
			                  		  //toastMessage( "Process "+trackApp+" Detected" );
				                	  runningProcsList.put(info.processName, String.valueOf( info.pid ));
				                	  break;
			                  	}  
				                	  
				                	  // Add only one process at a time
				                	  
				                  //}
			                  //}
			                  //processName = c.toString();
			                  //processName = info.processName;
			              
			          }
			          catch(Exception e) 
			          {
			                //Log.d("Process", "Error>> :"+ e.toString());
			          }
			   }
			   
			    
			    //l.clear();
			    
			    //runningProcsList.put("", "");
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
								
							
							//if( hm.getKey().toString().equals("com.android.browser") ){
							//process = Runtime.getRuntime().exec("su");
							//process = Runtime.getRuntime().exec("echo 0 > /proc/sys/kernel/yama/ptrace_scope");
						
				    		process = Runtime.getRuntime().exec("su");
				    		//OutputStream os;
				    		//os = process.getOutputStream();
							//synchronized (process) {
				    		//toastMessage( "Process "+trackApp+" is Tracked" );
				    		String app = hm.getKey().toString();
				    		String app_pid = hm.getValue().toString();
				    		// Updating the DB
				    		data.updateAppTraceStatus(app, "1");
							
				    		DataOutputStream os = new DataOutputStream(process.getOutputStream());
				    		//String com = "/system/xbin/strace -p 124 -o /sdcard/strace/stout.txt";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -o /sdcard/strace/"+hm.getKey()+".txt -e trace=open,close uname";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -s 200 -o /sdcard/strace/"+hm.getKey()+".txt";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -s 200 -o /sdcard/strace/"+hm.getKey()+".txt\n";
				    		//String com = "/system/xbin/strace -p "+app_pid+" -s 200 -o /sdcard/strace/"+app+".txt\n";
				    		String com = "/system/xbin/strace -p "+app_pid+" -s 200 -o /sdcard/strace/"+app+".txt\n";
				    		//String com = "/system/xbin/strace -p 564 -s 200 -o /sdcard/strace/com.android.browser.txt";
				    		//String com = "/sdcard/strace/strace -p "+hm.getValue()+" -o /sdcard/strace/"+hm.getKey()+".txt -e trace=open,close uname";
				    		//String com = "/system/xbin/strace -p 124";
				    		Log.d("XX##XX", com);
				    		//Log.d("XXMyPID", String.valueOf( android.os.Process.myPid() ));
				    		
				    		os.writeBytes( com );
				    		os.flush();
				    		//os.writeBytes( "echo $!" );
				    		//os.flush();
				    		
				    		
				    		InputStream stdout = process.getInputStream();
				    		byte[] buffer = new byte[2];
				    		int read;
				    		String out = new String();
				    		//read method will wait forever if there is nothing in the stream
				    		//so we need to read it in another way than while((read=stdout.read(buffer))>0)
				    		while(true){
				    		    read = stdout.read(buffer);
				    		    out += new String(buffer, 0, read);
				    		    if(read<4){
				    		        //we have read everything
				    		        break;
				    		    }
				    		}
				    		
				    		//Log.d("XX##XXStrace", Integer.parseInt(out));
				    		Log.d("XX##XXStrace", out);
				    		
				    		
				    		os.writeBytes("exit\n");
							os.flush();
				    		os.close();
				    		
				    		//Log.d("XX##XXStrace", String.valueOf(getPidFromPs("strace")));
				    		
				    		
				    		//process.wait(5000);
				    		//process.destroy();
				    		//process.exitValue()
				    		process.waitFor();
				    		
				    		appList.remove( hm.getKey() );
				    		isProcessing = true;
				    		break;
							//}
							//}
						//}
					} catch (IOException e1) {
						// TODO Auto-generated catch block 
						//e1.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
			    }
			    
			    try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
			return null;
		}

		
	}
	
	*/
	
	private class TracerThread extends Thread{
		
		@Override
		public void run(){
			List<String> appList = data.getSavedAppsList();
			//String trackApp = data.getApptoTrack();
			String trackApp = AppGlobal.getinstance().getAppToTrack();
			//String trackApp = "com.android.browser";
			//String trackApp = "com.android.gallery";
			//String trackApp = "";
			
			Log.d(this.getClass().getName(), "TRACER_THREAD");
			
			boolean isProcessing = false;

			while( isRunning ){
				
				
				if( isProcessing ) continue;
				
				//isProcessing = true;
				
				Log.d("TRACER_THREAD", "Running....");
				
				String processName = "";
				ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE);
			    //ActivityManager am = (ActivityManager)params[0].getSystemService(ACTIVITY_SERVICE);
			    List l = am.getRunningAppProcesses();
			    Iterator i = l.iterator();
			    PackageManager pm = getApplicationContext().getPackageManager();
			    //PackageManager pm = params[0].getPackageManager();
			    runningProcsList.clear();
			    
			    while(i.hasNext()) 
			    {
			    	  Log.d("TRACER_THREAD", "App List....");
			          ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			          try 
			          { 
			        	      trackApp = AppGlobal.getinstance().getAppToTrack();
			                  CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
			                  //Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
			                  //if( appList.contains( info.processName ) ){
				                  //if( !runningProcsList.containsValue( info.pid ) ){
				                  //if( !runningProcsList.containsKey( info.processName ) &&  !runningProcsList.containsValue( info.pid ) ){
			                  	if( info.processName.equals( trackApp ) ){
			                  		Log.d("KK Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
			                  		  //toastMessage( "Process "+trackApp+" Detected" );
				                	  runningProcsList.put(info.processName, String.valueOf( info.pid ));
				                	  break;
			                  	}  
				                	  
				                	  // Add only one process at a time
				                	  
				                  //}
			                  //}
			                  //processName = c.toString();
			                  //processName = info.processName;
			              
			          }
			          catch(Exception e) 
			          {
			                //Log.d("Process", "Error>> :"+ e.toString());
			          }
			   }
			   
			    
			    //l.clear();
			    
			    //runningProcsList.put("", "");
			    /// Run strace
			    
			    Iterator it = runningProcsList.entrySet().iterator();
			    Process process = null;
			    /*
			    try {
					process = Runtime.getRuntime().exec("su");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				*/
			    
			    while( it.hasNext() ){
			    	Map.Entry hm = (Map.Entry) it.next();
			    	Log.d("Process", "Id: "+ hm.getValue() +" ProcessName: "+ hm.getKey() );
			    	
				   
					try {
						/*
						if( !hm.getKey().toString().equals("com.android.alarmclock") && 
								!hm.getKey().toString().equals("com.android.launcher") &&
								!hm.getKey().toString().equals("com.android.settings") &&
								!hm.getKey().toString().equals("jp.co.omronsoft.openwnn") && 
								!hm.getKey().toString().equals("com.android.defcontainer")
								){
								*/
							
							//if( hm.getKey().toString().equals("com.android.browser") ){
							//process = Runtime.getRuntime().exec("su");
							//process = Runtime.getRuntime().exec("echo 0 > /proc/sys/kernel/yama/ptrace_scope");
						
				    		process = Runtime.getRuntime().exec("su");
				    		//OutputStream os;
				    		//os = process.getOutputStream();
							//synchronized (process) {
				    		//toastMessage( "Process "+trackApp+" is Tracked" );
				    		String app = hm.getKey().toString();
				    		String app_pid = hm.getValue().toString();
				    		
				    		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				    		NetworkInfo info = cm.getActiveNetworkInfo();
				    		// Updating the DB
				    		//data.updateAppTraceStatus(app, "1");
				    		if( info != null && info.isConnected() ){
				    			
				    			String typeName    = info.getTypeName();
				    			String subTypeName = info.getSubtypeName();
				    			String extraInfo   = info.getExtraInfo();
				    			
				    			String ssid =  getCurrentSsid(getApplicationContext());
				    			
				    			if(ssid.length() > 0)
				    				typeName = typeName + "|" + ssid;
				    			
				    			Log.d("NETSTATE", "<"+typeName+">, <"+subTypeName+">, <"+extraInfo+">" );
				    			
				    			data.updateAppTraceStatus(app, "1", "<"+typeName+">, <"+subTypeName+">, <"+extraInfo+">", app+".txt");
				    		}else{
				    			data.updateAppTraceStatus(app, "1", "No Network Connection", app+".txt");
				    		}
							
				    		DataOutputStream os = new DataOutputStream(process.getOutputStream());
				    		//String com = "/system/xbin/strace -p 124 -o /sdcard/strace/stout.txt";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -o /sdcard/strace/"+hm.getKey()+".txt -e trace=open,close uname";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -s 200 -o /sdcard/strace/"+hm.getKey()+".txt";
				    		//String com = "/system/xbin/strace -p "+hm.getValue()+" -s 200 -o /sdcard/strace/"+hm.getKey()+".txt\n";
				    		//String com = "/system/xbin/strace -p "+app_pid+" -s 200 -o /sdcard/strace/"+app+".txt\n";
				    		String com = "/system/xbin/strace -p "+app_pid+" -s 200 -o /sdcard/strace/"+app+".txt\n";
				    		
				    		//String com = "/system/xbin/strace -p 564 -s 200 -o /sdcard/strace/com.android.browser.txt";
				    		//String com = "/sdcard/strace/strace -p "+hm.getValue()+" -o /sdcard/strace/"+hm.getKey()+".txt -e trace=open,close uname";
				    		//String com = "/system/xbin/strace -p 124";
				    		Log.d("XX##XX", com);
				    		//Log.d("XXMyPID", String.valueOf( android.os.Process.myPid() ));
				    		
				    		os.writeBytes( com );
				    		os.flush();
				    		//os.writeBytes( "echo $!" );
				    		//os.flush();
				    		
				    		/*
				    		InputStream stdout = process.getInputStream();
				    		byte[] buffer = new byte[2];
				    		int read;
				    		String out = new String();
				    		//read method will wait forever if there is nothing in the stream
				    		//so we need to read it in another way than while((read=stdout.read(buffer))>0)
				    		while(true){
				    		    read = stdout.read(buffer);
				    		    out += new String(buffer, 0, read);
				    		    if(read<4){
				    		        //we have read everything
				    		        break;
				    		    }
				    		}
				    		
				    		//Log.d("XX##XXStrace", Integer.parseInt(out));
				    		Log.d("XX##XXStrace", out);
				    		*/
				    		
				    		os.writeBytes("exit\n");
							os.flush();
				    		os.close();
				    		
				    		//AppGlobal.getinstance().setStracePID( getPidFromPs("strace") );
				    		
				    		//Log.d("XX##XXStrace", String.valueOf(getPidFromPs("strace")));
				    		
				    		
				    		//process.wait(5000);
				    		//process.destroy();
				    		//process.exitValue()
				    		process.waitFor();
				    		
				    		appList.remove( hm.getKey() );
				    		isProcessing = true;
				    		break;
							//}
							//}
						//}
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
			    
			    try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
			Log.d("TRACER_THREAD", "Exiting....");
			
			//return null;
		}
	}
	
	
	public static String getCurrentSsid(Context context) {
	  	String ssid = null;
	  	ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	  	NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		  	if (networkInfo.isConnected()) {
		    	final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		    	final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
		    	if (connectionInfo != null && connectionInfo.getSSID().toString().length() > 0 ) {
		    		ssid = connectionInfo.getSSID();
		    	}
		  	}
  		return ssid;
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
		//data.saveAppsList();
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
			//sendNotification( onStartIntent, System.currentTimeMillis(), location.toString(), ++notificationCounter );
			// Apply Security Rules
			applySecurityRules( location );
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
	public boolean onUnbind(Intent intent) {
	    Log.d(this.getClass().getName(), "UNBIND");
	    return true;
	}
	
	public void applySecurityRules(Location location){
		//location.
		Cursor cursor = null;
		//startManagingCursor(cursor);
		//cursor = data.getLocationPermissions(location.getLatitude(), location.getLongitude());
		cursor = data.getLocationPermissionRange(location.getLatitude(), location.getLongitude());
		//toastMessage("Location XX "+ location.getLongitude());
		//toastMessage("Location XX "+ (Double)location.getLatitude());
		//cursor = data.getLocationPermissions(10.0, 20.0);
		
		if( cursor != null && cursor.getCount() > 0 ){
			cursor.moveToFirst();
			
			/*
			 * ServiceData.TABLE_LOCATION_PERMS_NAME,
					ServiceData.TABLE_LOCATION_PERMS_LAT, 
					ServiceData.TABLE_LOCATION_PERMS_LNG,
					ServiceData.TABLE_LOCATION_PERMS_WIFI,
					ServiceData.TABLE_LOCATION_PERMS_BLU,
					ServiceData.TABLE_LOCATION_PERMS_SCR
			 * */
			
			String loc_name = cursor.getString(0);
			String wifi_s   = cursor.getString(3);
			String blu_s    = cursor.getString(4);
			String scr_s    = cursor.getString(5);
			//cursor.getCount();
			//Log.d("$$", "Location Rule Found "+cursor.getCount());
			//toastMessage("Location Rule "+ cursor.getString(0) +" Applied");
			//toastMessage("Location Rule "+ cursor.getColumnName(0) +" Applied");
			
			toastMessage("Location Rule '"+ cursor.getString(0) +"' Applied");
			
			// Controlling WiFi -------------
			boolean status = (wifi_s.equals("1")) ? true : false;
			WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(status);			
			//-------------------------------
			// Controlling WiFi -------------
			status = (blu_s.equals("1")) ? true : false;
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
			if( mBluetoothAdapter != null ){
				if( status ){
					if(!mBluetoothAdapter.isEnabled())
						mBluetoothAdapter.enable();
				}else{
					if(mBluetoothAdapter.isEnabled())
						mBluetoothAdapter.disable();
				}
			}
			//-------------------------------
			// Changing Screen timeout ------
			// In millis
			//int timeout = Integer.parseInt( scr_s );
			int timeout = 30;
			
			if( scr_s.equals("15 Seconds") ){
				timeout = 15;
			}else if( scr_s.equals("30 Seconds") ){ 
				timeout = 30;
			}else if( scr_s.equals("1 Minute") ){ 
				timeout = 60;
			}else if( scr_s.equals("2 Minutes") ){
				timeout = 120;
			}else if( scr_s.equals("10 Minutes") ){
				timeout = 60 * 10;
			}else if( scr_s.equals("30 Minutes") ){
				timeout = 60 * 30;
			}
			
			//int timeout = Integer.parseInt( "10" );
			//timeout = 30 * 1000;
			Log.d("Timeout %%", scr_s+" - "+timeout);
			timeout = timeout * 1000;
			Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
			//------------------------------
			cursor.close();
		}else if(cursor != null && cursor.getCount() == 0){
			cursor.close();
			//cursor.moveToFirst();
			
			cursor = null;
			
			cursor = data.getGeneralPermissions("*");
			
			if( cursor != null && cursor.getCount() > 0 ){
				cursor.moveToFirst();
				
				String wifi_s   = cursor.getString(3);
				String blu_s    = cursor.getString(4);
				String scr_s    = cursor.getString(5);
				
				toastMessage("Location Rule General Applied");
				
				// Controlling WiFi -------------
				boolean status = (wifi_s.equals("1")) ? true : false;
				WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(status);			
				//-------------------------------
				// Controlling WiFi -------------
				status = (blu_s.equals("1")) ? true : false;
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
				if( mBluetoothAdapter != null ){
					if( status ){
						if(!mBluetoothAdapter.isEnabled())
							mBluetoothAdapter.enable();
					}else{
						if(mBluetoothAdapter.isEnabled())
							mBluetoothAdapter.disable();
					}
				}
				//-------------------------------
				// Changing Screen timeout ------
				// In millis
				//int timeout = Integer.parseInt( scr_s );
				int timeout = 30;
				
				if( scr_s.equals("15 Seconds") ){
					timeout = 15;
				}else if( scr_s.equals("30 Seconds") ){ 
					timeout = 30;
				}else if( scr_s.equals("1 Minute") ){ 
					timeout = 60;
				}else if( scr_s.equals("2 Minutes") ){
					timeout = 120;
				}else if( scr_s.equals("10 Minutes") ){
					timeout = 60 * 10;
				}else if( scr_s.equals("30 Minutes") ){
					timeout = 60 * 30;
				}
				
				//int timeout = Integer.parseInt( "10" );
				//timeout = 30 * 1000;
				Log.d("Timeout %%", scr_s+" - "+timeout);
				timeout = timeout * 1000;
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
				//------------------------------
				cursor.close();
			}
		}
		//data.close();
		
	}
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ){
		super.onStartCommand(intent, flags, startId);
		
		onStartIntent = intent;
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		String provider = mgr.getBestProvider(criteria, true);
		
		
		Log.d("bestProvider", provider);
		dumpLocation( mgr.getLastKnownLocation( provider ) );
		
		// Testing the GPS provider, for Mocking Locations
		//mgr.addTestProvider(LocationManager.GPS_PROVIDER, false, false,false, false, true, true, true, 0, 5);
		//mgr.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
		//mgr.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
		
		//mgr.requestLocationUpdates(provider, LocationTick, 0, locationListener);
		mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LocationTick, 0, locationListener);
		
		

		
		Log.d("onStartCommand()", "Service Started");
		
		isRunning = true;
		/*
		autoUpdate = new Timer();
		  autoUpdate.schedule(new TimerTask() {
		   @Override
		   public void run() {
		    //new Runnable() {
		     //public void run() {
		      //updateHTML();
		     //}
		    //};
		   }
		  }, 0, 10000);*/
		
		//new AppLaunchWatcher().execute(null);
		//appWatchTask = (AppLaunchWatcher) new AppLaunchWatcher().execute( this );
		
		//TracerThread thrd = new TracerThread();
		thrd = new TracerThread();
		thrd.start();
		
		  //DownloadFile downloadFile = new DownloadFile();
		  //downloadFile.execute( "" );
		
		// Firing the initial rules
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		// Or use LocationManager.GPS_PROVIDER
		// LocationProvider locationProvider = LocationManager.GPS_PROVIDER;

		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		if( lastKnownLocation != null )
			applySecurityRules(  lastKnownLocation );
		//else
			//applySecurityRules(  lastKnownLocation );
		
		
		
		
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
		
		// MockLocation
		//MockLocationTimerTask mk = new MockLocationTimerTask();
		//Timer mkTimer = new Timer();
		
		//mkTimer.schedule(mk, 3000, 1500);
		
		//timer.schedule( notifyTask, 1000, 10000 );
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.

		return START_STICKY;
	}
	
	private void updateHTML(){
		  // your logic here
		Log.d("!!!", "Task running******************");
		
		//if( isNetworkAvailable() ){
			
		//}
		
		try
	    {
	        Process mLogcatProc = null;
	        BufferedReader reader = null;
	        mLogcatProc = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});

	        reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

	        String line;
	        final StringBuilder log = new StringBuilder();
	        String separator = System.getProperty("line.separator"); 

	        while ((line = reader.readLine()) != null)
	        {
	            log.append(line);
	            log.append(separator);
	        }
	        String w = log.toString();
	        Toast.makeText(getApplicationContext(),w, Toast.LENGTH_LONG).show();
	    }
	    catch (Exception e) 
	    {
	        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
	    }
		
	}

	
	void cancellCollectTask(){
		int result = getPidFromPs("strace");
		//toastMessage( String.valueOf(getPidFromPs("/system/xbin/strace"))  );
		toastMessage( String.valueOf(getPidFromPs("strace"))  );
		//android.os.Process.sendSignal(getPidFromPs("strace"), 9);
		
		// I had a problem with this
		if( result > 0 ){
			killProcess( getPidFromPs("strace") );
		}
		
		/*
        if (appWatchTask != null && appWatchTask.getStatus() == AsyncTask.Status.RUNNING) 
        {
        	appWatchTask.cancel(true);
        	
        	appWatchTask = null;
        	
        	
        }
        */
        //if( thrd.isAlive() ){
        	//
        //}
        
		AppGlobal.getinstance().setStracePID( getPidFromPs("strace") );
    }
	
    // Checking for internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		cancellCollectTask();
		
		//autoUpdate.cancel();
		
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
		//notify.setLatestEventInfo(ServiceDemo.this, "ServiceDemo", message, contentIntent);
		notify.setLatestEventInfo(ServiceDemo.this, "ServiceDemo", message, contentIntent);
		
		notificationManager.notify( i, notify );
	}
	
	private void toastMessage(String str){
    	Toast t = Toast.makeText(this, str, Toast.LENGTH_SHORT);
    	t.show();
    }
	
	public static void killProcess( int pid ){
		Process suProcess;
		try {
			suProcess = Runtime.getRuntime().exec("su");
			
			DataOutputStream stdin = new DataOutputStream(suProcess.getOutputStream());
			//Log.d("#####", "# ps " + processName);
			//stdin.writeBytes("ps " + processName + "\n");
			stdin.writeBytes("kill -9 "+ String.valueOf(pid) +"\n");
			//stdin.writeBytes("ps " + processName + "\n");
			stdin.flush();
			//suProcess.waitFor();
			stdin.writeBytes("exit\n");
			stdin.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static int getPidFromPs(String processName) {
		int pid = -1;
	 
		try {
			Process suProcess = Runtime.getRuntime().exec("su");
	 
			// stdin
			DataOutputStream stdin = new DataOutputStream(suProcess.getOutputStream());
			Log.d("#####", "# ps " + processName);
			//stdin.writeBytes("ps " + processName + "\n");
			stdin.writeBytes("ps "+processName+"\n");
			//stdin.writeBytes("ps " + processName + "\n");
			stdin.flush();
			//suProcess.waitFor();
			stdin.writeBytes("exit\n");
			stdin.flush();
			
			
	 
			// stdout
			BufferedReader reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
			ArrayList<String> output = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				output.add(line);
			}
			
			//Log.d("#####111", "# ps " + line);
	 
			// parsing
			if (output.size() >= 2) {
				line = null;
				Iterator<String> itr = output.iterator();
				while (itr.hasNext()) {
					String element = itr.next();
					//Log.d("#####111", "# ps " + element);
					if( element.endsWith(processName) ){
					//if (element.matches("^\\S+\\s+[0-9]+\\s+.+\\s" + processName + "(\\s.+)?$")) {
					//if (element.matches("^*"+processName+"*$")) {
						//Log.d("#####111", "# ps " + element);
						line = element;
						break ;
					}
				}
				if (line != null) {
					
					//line = line.replaceFirst("^\\S+\\s+([0-9]+)\\s+.+\\s" + processName + "(\\s.+)?$", "$1");
					line = line.replaceFirst("^\\S+\\s+", "");
					line = line.replaceFirst("\\s+([0-9]+)\\s+.+\\s[/a-zA-Z_0-9]+$", "");
					//Log.d("#####111", "# ps " + line);
					if (line.matches("^[0-9]+$")) {
						pid = Integer.parseInt(line);
						Log.i("#####", "PID #" + pid);
					}
				}
			}
		}
		catch (Exception e) {
			Log.w("#####", "getPid(): " + e.getMessage());
		}
		return pid;
	}
	
	private class DownloadFile extends AsyncTask<String, Integer, String> {
	    @Override
	    protected String doInBackground(String... sUrl) {
	        try
		    {
		        Process mLogcatProc = null;
		        BufferedReader reader = null;
		        mLogcatProc = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});

		        reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

		        String line;
		        final StringBuilder log = new StringBuilder();
		        String separator = System.getProperty("line.separator"); 

		        while ((line = reader.readLine()) != null)
		        {
		            log.append(line);
		            log.append(separator);
		        }
		        String w = log.toString();
		        Toast.makeText(getApplicationContext(),w, Toast.LENGTH_LONG).show();
		    }
		    catch (Exception e) 
		    {
		        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		    }
	        return null;
	    }

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        //mProgressDialog.show();
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        //mProgressDialog.setProgress(progress[0]);
	    }
	}
	
	int i = 0;
	
	class MockLocationTimerTask extends TimerTask {
		 
		  public void run() {
			  // ERROR
			  Log.d("##MockLocationTimerTask##", "Running ...");
			// Mock Location
			  	
				Location loc = new Location("gps"); 
				/*
				if( i == 0){
					Log.d("##MockLocationTimerTask##", "Running ..." + i);
					loc.setLatitude(13.32); // just mock values 
					loc.setLongitude(13.32); 
					i = 1;
				}else{
					loc.setLatitude(15); // just mock values 
					loc.setLongitude(15);
					i = 0;
				}
				*/
				try{
				String tmp = getMockLocation( "http://192.168.1.2/~dinusha/androsec/mock_location.php" );
				String[] RowData = tmp.split(",");
				loc.setLatitude(Double.parseDouble(RowData[0].trim())); // just mock values 
				loc.setLongitude(Double.parseDouble(RowData[1].trim())); 
				mgr.setTestProviderLocation("gps", loc);
				Log.d("##MockLocationTimerTask##", "Lat "+loc.getLatitude());
				Log.d("##MockLocationTimerTask##", "Lng "+loc.getLongitude());
				}catch(Exception e){
					Log.d("##MockLocationTimerTask##", "EX "+e.getMessage());
				}
			 // how update TextView in link below  
	                 // http://android.okhelp.cz/timer-task-timertask-run-cancel-android-example/
	 
		    System.out.println("");
		  }
		}
	
	String getMockLocation( String path ) throws URISyntaxException, ClientProtocolException, IOException{
		BufferedReader in = null;
		 HttpClient client;
		 HttpGet method;
		 String url = path;
		 //String url = path;
		 String appName    = "";
		 String pckName    = "";
		 String appDetails = "";
		 String appScore   = "";
		 

	    try {
	    	method = new HttpGet(url);
	        client = new DefaultHttpClient();
	        HttpGet request = new HttpGet();
	        request.setURI(new URI(url));
	        HttpResponse response = client.execute(method);
	        String line = "";
	        
	        /*
	        in = new BufferedReader
	        (new InputStreamReader(response.getEntity().getContent()));
	        
	        StringBuffer sb = new StringBuffer("");
	        String line = "";
	        String NL = System.getProperty("line.separator");
	        while ((line = in.readLine()) != null) {
	            sb.append(line + NL);
	        }
	        in.close();
	        */
	        //page = sb.toString();
	        
	        //System.out.println(page);
	        
	         BufferedReader reader = new BufferedReader(new InputStreamReader( response.getEntity().getContent() ));
			    try {
			        //String line;
			        while ((line = reader.readLine()) != null) 
			        {
			             //String[] RowData = line.split(",");
			             
			             //appName    = RowData[0].trim();
			             //pckName    = RowData[1].trim();
			             //appDetails = RowData[2].trim();
			             //appScore   = RowData[3].trim();
			             //value = RowData[1];
			            // do something with "data" and "value"
			             
			             //Log.d("## Scanning ##", appName + " - " + pckName + " - " + appScore );
			             
			             //data.updateMalwaresList(pkName, otherNames, threatLevel);
			             //data.SaveAppReport(appName, pckName, appDetails, appScore);
			        	return line;
			        }
			        
			        // Scan the list for potential malwares
			        //scanMalwareList();
			    }
			    catch (IOException ex) {
			        // handle exception
			    }
			    finally {
			        
			    }

	         
	        } finally {
	        	if (in != null) {
	            try {
	                in.close();
	                } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    return "";

	}
}
