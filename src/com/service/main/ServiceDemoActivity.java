package com.service.main;

import com.demo.main.R;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ServiceDemoActivity extends Activity {
    /** Called when the activity is first created. */
	private Button btnStart;
	private Button btnStop;
	private Intent serviceIntent;
	
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
}