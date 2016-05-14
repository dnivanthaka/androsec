package com.service.main;

import com.demo.main.R;
import com.service.data.ServiceDataSource;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class GlobalSettingsActivity extends Activity{
	private ServiceDataSource data;
	private Button btnSave;
	private Button btnReadAppsList;
	private Button btnClearAppsList;
	private EditText servTraceURL;
	private EditText servMalwareURL;
	private CheckBox wifiOnly;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.globalsettings);
		
		btnSave = (Button)findViewById(R.id.btnSave);
		btnReadAppsList = (Button)findViewById(R.id.btnReadAppList);
		btnClearAppsList = (Button)findViewById(R.id.btnClearAppList);
		
		servTraceURL   = (EditText)findViewById( R.id.servTraceURL );
		servMalwareURL = (EditText)findViewById( R.id.servMalwareURL );
		wifiOnly       = (CheckBox)findViewById( R.id.wifionly );
		
		data = new ServiceDataSource( this );
		data.open();
		
		servTraceURL.setText( data.getGlobalParam( "trace_upload_path" ) );
		servMalwareURL.setText( data.getGlobalParam( "malware_list_path" ) );
		if( data.getGlobalParam( "upload_only_wifi" ).equals("Y") ){
			wifiOnly.setChecked(true);
		}else{
			wifiOnly.setChecked(false);
		}
		//wifiOnly.setText( data.getGlobalParam( "malware_list_path" ) );
	
		
		btnSave.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				//Log.d("Spinner@@", spinner.getSelectedItem().toString());
				//AppGlobal.getinstance().setAppToTrack( spinner.getSelectedItem().toString() );
				String tmp = "";
				
				data.saveGlobalParam( "trace_upload_path", servTraceURL.getText().toString() );
				data.saveGlobalParam( "malware_list_path", servMalwareURL.getText().toString() );
				
				if(wifiOnly.isChecked()){
					tmp = "Y";
				}else{
					tmp = "N";
				}
				
				data.saveGlobalParam( "upload_only_wifi", tmp );
			}
		} );
		
		btnReadAppsList.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				//Log.d("Spinner@@", spinner.getSelectedItem().toString());
				//AppGlobal.getinstance().setAppToTrack( spinner.getSelectedItem().toString() );
				
				data.saveAppsList();
			}
		} );
		
		btnClearAppsList.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				//Log.d("Spinner@@", spinner.getSelectedItem().toString());
				//AppGlobal.getinstance().setAppToTrack( spinner.getSelectedItem().toString() );
				
				data.clearAppsList();
			}
		} );
		
		//data.close();
	}
	
	@Override
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
}
