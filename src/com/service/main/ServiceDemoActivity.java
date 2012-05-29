package com.service.main;

import java.util.List;

import com.demo.main.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ServiceDemoActivity extends Activity {
    /** Called when the activity is first created. */
	private Button btnStart;
	private Button btnStop;
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
        
        setContentView(R.layout.main);
        
        final Intent intent = this.getIntent();
        final String action = intent.getAction();
        

        
        if( Intent.ACTION_PACKAGE_REMOVED.equals( action ) ){
        	Log.d("Service Activity", "Package Removed");
        }
        
        btnStart = (Button) findViewById( R.id.btnStart );
        btnStop  = (Button) findViewById( R.id.btnStop );
        
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


}