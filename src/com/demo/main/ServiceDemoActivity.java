package com.demo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        
        btnStart = (Button) findViewById( R.id.btnStart );
        btnStop = (Button) findViewById( R.id.btnStop );
        
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
    }
}