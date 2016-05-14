package com.service.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceDemoActivity extends Activity {
    /** Called when the activity is first created. */
	private Button btnStart;
	private Button btnStop;
	private Button runCommand;
	private Button locPerms;
	private Button sendFiles;
	private Button appReport;
	private Button globalConfig;
	private Intent serviceIntent;
	private ServiceDemo mService;
	private ServerTask appWatchTask;
	private Intent onStartIntent;
	private int notificationCounter;
	
	private ServiceDataSource data;
	
	boolean mBound = false;
	
	private Timer autoUpdate;
	
	//private String ServerURL = "http://192.168.1.4:80/~dinusha/androsec/save_trace_data.php";
	//private String ServerURL = "http://192.168.1.2/~dinusha/androsec/test_upload.php";
	//private String MalwareListURL = "http://192.168.1.2/~dinusha/androsec/malware_list.php";
	private String ServerURL      = "";
	private String MalwareListURL = "";
	private String phoneID        = "";
	private String wifiOnly       = "";
	//private String ServerURL = "http://192.168.1.4:80";

	
		private ServiceConnection mConnection = new ServiceConnection() {
			@Override
	        public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        mService = ((ServiceDemo.LocalBinder)service).getService();
	        
	        
	        
	        mBound = true;
	        
	        Log.d("## ANDROSEC Service Bounding Called", String.valueOf(mBound) );
	
	    }
	
	    public void onServiceDisconnected(ComponentName className) {
	        mService = null;
	        //unbindService(mConnection);
	        mBound = false;
	    }
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
        final Intent intent = this.getIntent();
        final String action = intent.getAction();
         
        if( Intent.ACTION_PACKAGE_REMOVED.equals( action ) ){
        	Log.d("Service Activity", "Package Removed");
        }
        
        if( Intent.ACTION_PACKAGE_ADDED.equals( action ) ){
        	Log.d("Service Activity", "Package Installed");
        }
        
        data = new ServiceDataSource( this );
		data.open();
		
		ServerURL      = data.getGlobalParam( "trace_upload_path" );
		MalwareListURL = data.getGlobalParam( "malware_list_path" );
		wifiOnly       = data.getGlobalParam( "upload_only_wifi" );
		
		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		phoneID = tManager.getDeviceId();

		//Log.d("Service Activity##Phone id", phoneID);
		
		notificationCounter = 0;
		//phoneID = Secure.getString( getBaseContext().getContextResolver(), Secure.ANDROID_ID ); 
        
        //btnStart = (Button) findViewById( R.id.btnStart );
        //btnStop  = (Button) findViewById( R.id.btnStop );
        
        /*
        LinearLayout l1 = (LinearLayout)findViewById(R.id.linearLayout1);
        
        Button btnStart = new Button( this );
        btnStart.setText("Start Service");
        Button btnStop  = new Button( this );
        btnStop.setText("Stop Service");
        
        btnStart.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceIntent = new Intent( ServiceDemoActivity.this, ServiceDemo.class );
				
				startService( serviceIntent );
				bindService(serviceIntent,mConnection,0);

			}
		} );
        
        btnStop.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceIntent = new Intent( ServiceDemoActivity.this, ServiceDemo.class );
				
				stopService( serviceIntent );
			}
		} );
		*/
        
        //if( isServiceRunning( ServiceDemo.class.getCanonicalName() ) ){
        	//l1.addView( btnStop );
        //}else{
        	//l1.addView( btnStart );
        	
        //}
        
        /*
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        */
        
        	setServiceControls();
        	
          
        	
        	//data.close();

    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//setServiceControls();
    }
    
    private void updateMalwareList(){		
		if( isNetworkAvailable() ){
			Log.d("Net !!!", "Task running******************");
			sendFiles();
			//appWatchTask = (ServerTask) new ServerTask().execute(new String[]{});
		}
	}
    
    private void runCommand(){
    	try {
    		TextView commandOut = (TextView)findViewById(R.id.commandOut);

    	    // Executes the command.
    		Process process = Runtime.getRuntime().exec("su");
    	    //process = Runtime.getRuntime().exec("/system/bin/ps");
    	    process = Runtime.getRuntime().exec("id");
    	    
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
    	    commandOut.append(output);

    	    // Waits for the command to finish.
    	    process.waitFor();
    	    
    	    //return output.toString();
    	} catch (IOException e) {
    	    //throw new RuntimeException(e);
    	} catch (InterruptedException e) {
    	    //throw new RuntimeException(e);
    	}
    }
    
    private void runStrace(){
    	try {
    		TextView commandOut = (TextView)findViewById(R.id.commandOut);
    		
    		int pid = getServicePid( "com.android.phone" );
    		//toastMessage("pid = "+String.valueOf(pid));
    	    // Executes the command.
    		Process process = Runtime.getRuntime().exec("su");
    		//Process process = Runtime.getRuntime().exec("su");
    		//OutputStream os;
    		//os = process.getOutputStream();
    		DataOutputStream os = new DataOutputStream(process.getOutputStream());
    		String com = "/system/xbin/strace -p 124 -o /sdcard/strace/stout.txt";
    		//String com = "/system/xbin/strace -p 124";
    		os.writeBytes( com );
    		os.flush();
    		os.close();
    		//process.waitFor();

    	    //process = Runtime.getRuntime().exec("/system/bin/strace -p 1 -o /sdcard/stout.txt");
    	    //process = Runtime.getRuntime().exec("/system/xbin/strace -p "+String.valueOf(pid)+" -o /sdcard/strace/stout.txt");
    	    
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
    	        toastMessage(buffer.toString());
    	    }
    	    reader.close();
    	    commandOut.append(output);

    	    // Waits for the command to finish.
    	    process.waitFor();
    	    
    	    //return output.toString();
    	} catch (IOException e) {
    	    //throw new RuntimeException(e);
    	} catch (InterruptedException e) {
    	    //throw new RuntimeException(e);
    	}
    }
    
    private void testRooting(){
    	Process p;
    	
    	try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){
                System.out.println("Can write.");
                File def_file = new File(root, "default.txt");
                FileWriter fw = new FileWriter(def_file);
                BufferedWriter out = new BufferedWriter(fw);
                String defbuf = "default";
                out.write(defbuf);
                out.flush();
                out.close();
            }
            else
                System.out.println("Can't write.");
    }catch (IOException e) {
            e.printStackTrace();
    }

    	
    	try {
    	   // Preform su to get root privledges
    		//String command[] = { "su", "-c", "echo \"Do I have root?\" >/mnt/sdcard/temporary.txt\n" };
    	   p = Runtime.getRuntime().exec("su");
    	   //p = Runtime.getRuntime().exec(command); 
    	   
    	   // Attempt to write a file to a root-only
    	   DataOutputStream os = new DataOutputStream(p.getOutputStream());
    	   //os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");
    	   os.writeBytes("echo \"Do I have root?\" >/mnt/sdcard/temporary.txt\n");

    	   // Close the terminal
    	   os.writeBytes("exit\n");
    	   os.flush();
    	   try {
    	      p.waitFor();
    	           if (p.exitValue() != 255) {
    	        	  // TODO Code to run on success
    	              toastMessage("root");
    	              Log.d("Service Activity", "Root");
    	           }
    	           else {
    	        	   // TODO Code to run on unsuccessful
    	        	   toastMessage("not root");
    	        	   Log.d("Service Activity", "Not Root");
    	           }
    	   } catch (InterruptedException e) {
    	      // TODO Code to run in interrupted exception
    		   toastMessage("not root");
    		   Log.d("Service Activity", "Not Root");
    	   }
    	} catch (IOException e) {
    	   // TODO Code to run in input/output exception
    		toastMessage("not root");
    		Log.d("Service Activity", "Not Root");
    	}
    }
    
    private void readLog(){
    	try
        {
    		TextView commandOut = (TextView)findViewById(R.id.commandOut);
    		
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
            //Toast.makeText(getApplicationContext(),w, Toast.LENGTH_LONG).show();
            commandOut.append(w);
        }
        catch (Exception e) 
        {
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    
    private void toastMessage(String str){
    	Toast t = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
    	t.show();
    }
    
    private void setServiceControls(){
    	
    	setContentView(R.layout.main);
    	
    	LinearLayout l1 = (LinearLayout)findViewById(R.id.linearLayout1);
    	
    	if( btnStop != null )
    		l1.removeView( btnStop );
    	
    	if( btnStart != null )
    		l1.removeView( btnStart );
    
        
        Button btnStart = new Button( this );
        btnStart.setText("Start Service");
        Button btnStop  = new Button( this );
        btnStop.setText("Stop Service");
        
        btnStart.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceIntent = new Intent( ServiceDemoActivity.this, ServiceDemo.class );
				
				startService( serviceIntent );
				//bindService(serviceIntent,mConnection,0);
				//bindService(serviceIntent,mConnection,Context.BIND_AUTO_CREATE);
				//Context.BIND_AUTO_CREATE
				setServiceControls();
				
				Log.d("## ANDROSEC Service Start() Called", String.valueOf(mBound) );

			}
		} );
        
        btnStop.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//serviceIntent = new Intent( ServiceDemoActivity.this, ServiceDemo.class );
				serviceIntent = new Intent( getApplicationContext(), ServiceDemo.class );
				
				stopService( serviceIntent );
				/*
				if( mBound ){
		    		// Remove this when putting into phone
		    		unbindService(mConnection);
		    		mBound = false;
		    	}
		    	*/
				//while( isServiceRunning( ServiceDemo.class.getCanonicalName() ) );
				setServiceControls();
			}
		} );
        
        if( isServiceRunning( ServiceDemo.class.getCanonicalName() ) ){
        	l1.addView( btnStop );
        }else{
        	l1.addView( btnStart );
        	
        }
        
        // Monitoring Stopper ------------------------
        
        if( ServiceDemo.getPidFromPs("strace") > 0 ){
        	
        	 Button btnTraceStopper = new Button( this );
        	 btnTraceStopper.setText("Stop App Tracing");
             
        	 btnTraceStopper.setOnClickListener( new View.OnClickListener() {
     			
     			@Override
     			public void onClick(View v) {
     				//ServiceDemo.killProcess( ServiceDemo.getPidFromPs("strace") );
     				ServiceDemo.killProcess( ServiceDemo.getPidFromPs("strace") );
     				setServiceControls();
     			}
     		} );
        	 
        	 l1.addView( btnTraceStopper );
        	
        }
        
        //--------------------------------------------
        
        runCommand = (Button) findViewById( R.id.btnRun );
        runCommand.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//runCommand();
				//readLog();
				//runStrace();
				//testRooting();
				launchActivity( "app_tracing_settings" );
			}
		} );
        
        locPerms = (Button) findViewById( R.id.btnLocPerms );
        locPerms.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//runCommand();
				//readLog();
				//runStrace();
				//testRooting();
				launchActivity( "location_settings" );
			}
		} );
        
        sendFiles = (Button) findViewById( R.id.btnSendFiles );
        sendFiles.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ServerURL      = data.getGlobalParam( "trace_upload_path" );
				
				//runCommand();
				//readLog();
				//runStrace();
				//testRooting();
				//launchActivity( "location_settings" );
				sendFiles();
			}
		} );
        
        globalConfig = (Button) findViewById( R.id.btnGlobalSettings );
        globalConfig.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//runCommand();
				//readLog();
				//runStrace();
				//testRooting();
				//launchActivity( "location_settings" );
				launchActivity( "global_settings" );
			}
		} );
        
        appReport = (Button) findViewById( R.id.btnAppReport );
        appReport.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				launchActivity( "app_report" );
			}
		} );
    }
    
    private void launchActivity( String activity )
    {
    	Intent i = null;
    	
    	if( activity.equals("app_tracing_settings") ){
    		i = new Intent( this, AppTracerControlActivity.class );
    	}else if( activity.equals("location_settings") ){
    		i = new Intent( this, LocationServicesActivity.class );
    	}else if( activity.equals("global_settings") ){
    		i = new Intent( this, GlobalSettingsActivity.class );
    	}else if( activity.equals("app_report") ){
    		i = new Intent( this, AppReportActivity.class );
    	}
    	
    	startActivity(i);
    }
    
    private void sendFiles(){
    	if( isNetworkAvailable() ){
    		if( (wifiOnly.equals("Y") && networkType().toString().equals("WIFI")) || wifiOnly.equals("N") ){
    			if( ServerURL.length() > 4 ){
    				toastMessage( "Uploading Files...." );
    				appWatchTask = (ServerTask) new ServerTask().execute(new String[]{});
    			}else{
    				toastMessage( "Please Enter a Valid Upload URL" );
    			}
    		}else{
    			toastMessage( "This is not a Wifi Connection" );
    		}
    		
    		
    	}else{
    		toastMessage( "No Internet Connection Available" );
    	}
    	
    	//scanMalwaresList();
    }
    
    private void sendNotification( Intent intent, long when, String message, int i ){
		final NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		final PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
		
		Notification notify = new Notification( R.drawable.ic_launcher, "AndroSec", when  );
		//notify.setLatestEventInfo(ServiceDemo.this, "ServiceDemo", message, contentIntent);
		notify.setLatestEventInfo(ServiceDemoActivity.this, "ServiceDemo", message, contentIntent);
		
		notificationManager.notify( i, notify );
	}
    
    /*
     * @Override
    protected void onPause() {
        super.onPause();
        
        data.close();

    }
	
	@Override
    protected void onResume() 
    {
		super.onResume();
		
		data.open();
    }
     */
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        
        data.open();
        // The activity has become visible (it is now "resumed").
        //if(isServiceRunning("com.service.main.ServiceDemo")){}
        //Log.d("## Service Activity ##", Boolean.toString(AppGlobal.getinstance().getServiceStatus()));
        //if( isServiceRunning("com.service.main.ServiceDemo") ){
        if( isServiceRunning( ServiceDemo.class.getCanonicalName() ) ){
        	Log.d("## Service Activity ##", "Service Running");
        }
        
        setServiceControls();
        /*
        autoUpdate = new Timer();
		  autoUpdate.schedule(new TimerTask() {
		   @Override
		   public void run() {
			 runOnUiThread(new Runnable() {
		     public void run() {
			 updateMalwareList();
		     }
		    });
		   }
		  }, 0, 40000);
		  */
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	//autoUpdate.cancel();
    	Log.d("## ANDROSEC onPause() Called", String.valueOf(mBound) );
    	
    	/*
    	if( mBound ){
    		// Remove this when putting into phone
    		unbindService(mConnection);
    		mBound = false;
    	}
    	*/
    
    	if(appWatchTask != null)
    		appWatchTask.cancel(true);
    	
        

        data.close();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	/*
    	if( mBound ){
    		// Remove this when putting into phone
    		unbindService(mConnection);
    		mBound = false;
    	}
    	*/
    	
    	data.close();
        
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        /*
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        */
        
        data.close();
    }
    
    public boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo runningServiceInfo : services) {
        	//Log.d("## Service Running ##", runningServiceInfo.service.getClassName());
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
     }
    
    public int getServicePid(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)getSystemService( Context.ACTIVITY_SERVICE );
        //final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        final List<RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();

        for (RunningAppProcessInfo runningServiceInfo : services) {
        	Log.d("## Service Running ##", runningServiceInfo.processName );
            if (runningServiceInfo.processName.equals(serviceClassName)){
                return runningServiceInfo.pid;
            }
        }
        return -1;
     }
    /*
    private void scanMalwaresList(){
		String packageName = "";
		String appName = "";
		Cursor cursor = null;

		cursor = data.getMalwaresList();
		
		if( cursor != null && cursor.getCount() > 0 ){
			cursor.moveToFirst();
			
			while( cursor.isAfterLast() == false ){
				packageName    = cursor.getString(0);
				appName        = cursor.getString(1);
				//Log.d("## Scanning ##", packageName );
				if( data.checkMalwarePresent( packageName ) ){
					toastMessage( "Package : "+appName+" ("+packageName+") is installed, Please remove this package!!!" );
					//Log.d("%%%%%%%%%%", "Package : "+appName+" ("+packageName+") is installed, Please remove this package!!!" );
					sendNotification( onStartIntent, System.currentTimeMillis(), "Remove : "+appName+" ("+packageName+")", ++notificationCounter );
					
				}
				
				cursor.moveToNext();
			}
		//checkMalwarePresent( String packageName );
			cursor.close();
		}
	}
	*/
    
    // Checking for internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
 // Checking for internet connection type (e.g. Wifi)
    private String networkType() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        if( activeNetworkInfo != null && activeNetworkInfo.isConnected() ){
			
			String typeName    = activeNetworkInfo.getTypeName();
			String subTypeName = activeNetworkInfo.getSubtypeName();
			String extraInfo   = activeNetworkInfo.getExtraInfo();
			
			//String ssid =  getCurrentSsid(getApplicationContext());
			
			//if(ssid.length() > 0)
				//typeName = typeName + "|" + ssid;
			
			//Log.d("NETSTATE", "<"+typeName+">, <"+subTypeName+">, <"+extraInfo+">" );
			
			//data.updateAppTraceStatus(app, "1", "<"+typeName+">, <"+subTypeName+">, <"+extraInfo+">", app+".txt");
			return typeName;
		}else{
			//data.updateAppTraceStatus(app, "1", "No Network Connection", app+".txt");
			return "-";
		}
    }


    public class ServerTask  extends AsyncTask<String, Integer , Void>
	{
    	
    	HttpURLConnection uploadFile( FileInputStream fileInputStream, String fileName, HashMap<String, String> meta ){
    		//final String serverFileName = "test"+ (int) Math.round(Math.random()*1000) + ".jpg";
    		final String serverFileName = fileName;
    		final String lineEnd = "\r\n";
			final String twoHyphens = "--";
			final String boundary = "*****";
			
			try
			{
				URL url = new URL(ServerURL);
				//URL url = new URL("http://192.168.1.4/~dinusha/androsec/test_upload.php");
				//"http://192.168.1.4/~dinusha/androsec/test_upload.php"
				// Open a HTTP connection to the URL
				final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				// Allow Inputs
				conn.setDoInput(true);				
				// Allow Outputs
				conn.setDoOutput(true);				
				// Don't use a cached copy.
				conn.setUseCaches(false);
				
				conn.setChunkedStreamingMode(0);
				
				//String data = URLEncoder.encode("p_id", "UTF-8") + "=" + URLEncoder.encode("456", "UTF-8");
			    //data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");
				
				// Use a post method.
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
				
				DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
				//////////////
				dos.writeBytes(twoHyphens + boundary + lineEnd);

				// Sending phone id
				//dos.writeBytes("--" + boundary).append(CRLF);
				dos.writeBytes("Content-Disposition: form-data; name=\"p_id\"");
				dos.writeBytes(lineEnd);
				dos.writeBytes("Content-Type: text/plain; charset=UTF-8");
				dos.writeBytes(lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes( phoneID );
				dos.writeBytes(lineEnd);
				dos.flush();
				
				dos.writeBytes( twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"appname\""+ lineEnd + lineEnd + meta.get( ServiceData.TABLE_APPSLIST_APPNAME ) + lineEnd );
				dos.writeBytes( twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"pckname\""+ lineEnd + lineEnd + meta.get( ServiceData.TABLE_APPSLIST_PCKNAME ) + lineEnd );
				dos.writeBytes( twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"tracedate\""+ lineEnd + lineEnd + meta.get( ServiceData.TABLE_APPSLIST_TRACEDATE ) + lineEnd );
				dos.writeBytes( twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"networkinfo\""+ lineEnd + lineEnd + meta.get( ServiceData.TABLE_APPSLIST_TRACENETW ) + lineEnd );
				dos.flush();
				
				// Send data
				//dos.writeBytes(data);
				//dos.flush();
				//dos.writeBytes(lineEnd);
				//String feed = new String("--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"feed\"\r\n\r\n" + topost + "\r\n");
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + serverFileName +"\"" + lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes(boundary);
				dos.flush();
				//dos.writeBytes("POST /~dinusha/androsec/save_trace_data.php HTTP/1.0\r\n");
				//dos.writeBytes("GET /~dinusha/ HTTP/1.1\r\n");
				//dos.writeBytes("Content-Length: "+data.length()+"\r\n");
				//dos.writeBytes("Content-Type: application/x-www-form-urlencoded\r\n");
				//dos.writeBytes("\r\n");

			    // Send data
				//dos.writeBytes(data);
			    //dos.flush();
			    
			    

				// create a buffer of maximum size
				int bytesAvailable = fileInputStream.available();
				int maxBufferSize = 1024;
				//int maxBufferSize = 24000;
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);
				byte[] buffer = new byte[bufferSize];
				
				// read file and write it into form...
				int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				
				while (bytesRead > 0)
				{
					
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				Log.e("ANDROSEC", "WROTE DATA: "+buffer+serverFileName );
				// send multipart form data after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				//publishProgress(SERVER_PROC_STATE);
				// close streams
				fileInputStream.close();
				dos.flush();
				dos.close();
				
				return conn;
			}
			catch (MalformedURLException ex){
				Log.e("ANDROSEC", "error: " + ex.getMessage(), ex);
				return null;
			}
			catch (IOException ioe){
				Log.e("ANDROSEC", "error: " + ioe.getMessage(), ioe);
				return null;
			}
    	}
    	
    	/*
    	 void saveMalwareList( String path ) throws URISyntaxException, ClientProtocolException, IOException{
    		BufferedReader in = null;
    		 HttpClient client;
    		 HttpGet method;
    		 String url= path;
    		 String pkName = "";
    		 String otherNames = "";
    		 String threatLevel = "";

    	    try {
    	    	method = new HttpGet(url);
    	        client = new DefaultHttpClient();
    	        HttpGet request = new HttpGet();
    	        request.setURI(new URI(url));
    	        HttpResponse response = client.execute(method);
    	        
    	        in = new BufferedReader
    	        (new InputStreamReader(response.getEntity().getContent()));
    	        StringBuffer sb = new StringBuffer("");
    	        String line = "";
    	        String NL = System.getProperty("line.separator");
    	        while ((line = in.readLine()) != null) {
    	            sb.append(line + NL);
    	        }
    	        in.close();
    	        page = sb.toString();
    	        
    	        //System.out.println(page);
    	        
    	         BufferedReader reader = new BufferedReader(new InputStreamReader( response.getEntity().getContent() ));
				    try {
				        String line;
				        while ((line = reader.readLine()) != null) {
				             String[] RowData = line.split(",");
				             pkName      = RowData[0];
				             otherNames  = RowData[1];
				             threatLevel = RowData[2];
				             //value = RowData[1];
				            // do something with "data" and "value"
				             
				             Log.d("## Scanning ##", pkName + " - " + otherNames + " - " + threatLevel );
				             
				             data.updateMalwaresList(pkName, otherNames, threatLevel);
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
    	    

    	}
*/
    	
    	void saveAppReport( String path ) throws URISyntaxException, ClientProtocolException, IOException{
    		BufferedReader in = null;
    		 HttpClient client;
    		 HttpGet method;
    		 String url= path;
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
    	        
    	        in = new BufferedReader
    	        (new InputStreamReader(response.getEntity().getContent()));
    	        StringBuffer sb = new StringBuffer("");
    	        String line = "";
    	        String NL = System.getProperty("line.separator");
    	        while ((line = in.readLine()) != null) {
    	            sb.append(line + NL);
    	        }
    	        in.close();
    	        //page = sb.toString();
    	        
    	        //System.out.println(page);
    	        
    	         BufferedReader reader = new BufferedReader(new InputStreamReader( response.getEntity().getContent() ));
				    try {
				        //String line;
				        while ((line = reader.readLine()) != null) 
				        {
				             String[] RowData = line.split(",");
				             
				             appName    = RowData[0];
				             pckName    = RowData[1];
				             appDetails = RowData[2];
				             appScore   = RowData[3];
				             //value = RowData[1];
				            // do something with "data" and "value"
				             
				             //Log.d("## Scanning ##", pkName + " - " + otherNames + " - " + threatLevel );
				             
				             //data.updateMalwaresList(pkName, otherNames, threatLevel);
				             data.SaveAppReport(appName, pckName, appDetails, appScore);
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
    	    

    	}
    	
		@Override
		protected Void doInBackground(String... arg0) {
			Log.e("ANDROSEC", "UPLOAD: ");
			processFiles();
			//updateAppReport();
			return null;
		}
		
		private void processFiles(){
			//String inputFilePath = "/data/data/com.demo.main/databases/mobsec.db";
			String inputFilePath = "/mnt/sdcard/strace/";
			//String inputFilePath = "/mnt/sdcard/androsec/";
			
			
			Cursor cursor = data.getStracedAppsCursor();
			
			cursor.moveToFirst();
			while( cursor.isAfterLast() == false ){
				if( cursor.getString(2).trim().length() > 0 ){
					File tmp = new File( inputFilePath+cursor.getString(2) );
					HashMap<String, String> meta = new HashMap<String, String>();
					
					if( tmp.isFile() ){
						
						meta.put( ServiceData.TABLE_APPSLIST_APPNAME , cursor.getString(0));
						meta.put( ServiceData.TABLE_APPSLIST_PCKNAME , cursor.getString(1));
						meta.put( ServiceData.TABLE_APPSLIST_TRACEDATE , cursor.getString(4));
						meta.put( ServiceData.TABLE_APPSLIST_TRACENETW , cursor.getString(5));
						
						//Log.e("ANDROSEC", "UPLOAD: "+ cursor.getString(0) + meta.get( ServiceData.TABLE_APPSLIST_TRACENETW ) + cursor.getString(4) + cursor.getString(5) );
						
						try {

							FileInputStream fileInputStream  = new FileInputStream(tmp);
							Log.e("ANDROSEC", "UPLOAD: "+fileInputStream.available());
							//upload photo
					    	final HttpURLConnection  conn = uploadFile(fileInputStream, tmp.getName(), meta);
					    	
					    	//get processed photo from server
					    	if (conn != null){
					    	//getResultImage(conn);
					    		//conn.disconnect();
					    	}
							fileInputStream.close();
						}
				        catch (FileNotFoundException ex){
				        	Log.e("ANDROSEC", ex.toString());
				        }
				        catch (IOException ex){
				        	Log.e("ANDROSEC", ex.toString());
				        }
						
						tmp = null;
						
					}
					//appReport.append( cursor.getString(0)+"("+cursor.getString(1)+") - "+cursor.getString(2)+"\n" );
				
				
				}
				
				cursor.moveToNext();
			}
			
			cursor.close();
			
			/*
			File[] inputFiles = new File(inputFilePath).listFiles();
			for( File file : inputFiles  ){
				if( file.isFile() ){
					try {

						FileInputStream fileInputStream  = new FileInputStream(file);
						Log.e("ANDROSEC", "UPLOAD: "+fileInputStream.available());
						//upload photo
				    	final HttpURLConnection  conn = uploadFile(fileInputStream, file.getName());
				    	
				    	//get processed photo from server
				    	if (conn != null){
				    	//getResultImage(conn);
				    		//conn.disconnect();
				    	}
						fileInputStream.close();
					}
			        catch (FileNotFoundException ex){
			        	Log.e("ANDROSEC", ex.toString());
			        }
			        catch (IOException ex){
			        	Log.e("ANDROSEC", ex.toString());
			        }
				}
			}
			*/
		}
		
		private void updateAppReport(){
			try {
			
			//get malwares list
		    saveAppReport( MalwareListURL );
			//Log.e("ANDROSEC LIST -- ", output);	
		    	
			}
	        catch (URISyntaxException ex){
	        	Log.e("ANDROSEC", ex.toString());
	        }
			catch( ClientProtocolException ex ){
				Log.e("ANDROSEC", ex.toString());
			}
	        catch (IOException ex){
	        	Log.e("ANDROSEC", ex.toString());
	        }
		}
		
		/*private void updateMalwareList(){
			try {
			
			//get malwares list
			saveMalwareList( MalwareListURL );
			//Log.e("ANDROSEC LIST -- ", output);	
		    	
			}
	        catch (URISyntaxException ex){
	        	Log.e("ANDROSEC", ex.toString());
	        }
			catch( ClientProtocolException ex ){
				Log.e("ANDROSEC", ex.toString());
			}
	        catch (IOException ex){
	        	Log.e("ANDROSEC", ex.toString());
	        }
		}*/
		
		
	}

}