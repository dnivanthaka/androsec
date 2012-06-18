package com.service.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import com.demo.main.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
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
	private Intent serviceIntent;
	private ServiceDemo mService;

	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
        public void onServiceConnected(ComponentName className,
            IBinder service) {
        mService = ((ServiceDemo.LocalBinder)service).getService();

    }

    public void onServiceDisconnected(ComponentName className) {
        mService = null;
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
    	Toast t = Toast.makeText(this, str, Toast.LENGTH_SHORT);
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
				bindService(serviceIntent,mConnection,0);
				setServiceControls();

			}
		} );
        
        btnStop.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceIntent = new Intent( ServiceDemoActivity.this, ServiceDemo.class );
				
				stopService( serviceIntent );
				while( isServiceRunning( ServiceDemo.class.getCanonicalName() ) );
				setServiceControls();
			}
		} );
        
        if( isServiceRunning( ServiceDemo.class.getCanonicalName() ) ){
        	l1.addView( btnStop );
        }else{
        	l1.addView( btnStart );
        	
        }
        
        runCommand = (Button) findViewById( R.id.btnRun );
        runCommand.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//runCommand();
				//readLog();
				runStrace();
				//testRooting();
			}
		} );
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        //if(isServiceRunning("com.service.main.ServiceDemo")){}
        //Log.d("## Service Activity ##", Boolean.toString(AppGlobal.getinstance().getServiceStatus()));
        //if( isServiceRunning("com.service.main.ServiceDemo") ){
        if( isServiceRunning( ServiceDemo.class.getCanonicalName() ) ){
        	Log.d("## Service Activity ##", "Service Running");
        }
        
        setServiceControls();
    }
    @Override
    protected void onPause() {
        super.onPause();

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


}