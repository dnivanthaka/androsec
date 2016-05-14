package com.service.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo.main.R;
import com.service.data.ServiceDataSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocationServicesActivity extends Activity {
	Spinner permType;
	Spinner wifi_spinner;
	Spinner blut_spinner;
	Spinner scr_spinner;
	EditText permName;
	EditText permLat;
	EditText permLng;
	
	TextView lblLat;
	TextView lblLng;
	
	
	//int locationRangeMeters = 100;
	//int earthRadius         = 6371;
	//double deltaDegrees     = ((locationRangeMeters/1000) / earthRadius) * (180 / 3.142);
	
	private ServiceDataSource data;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.locperms);
		
		data = new ServiceDataSource( this );
		data.open();
		
		List <String> wifi_s = new ArrayList<String>();
		wifi_s.add("On");
		wifi_s.add("Off");
		
		List <String> blut_s = new ArrayList<String>();
		blut_s.add("On");
		blut_s.add("Off");
		
		List <String> scr_s = new ArrayList<String>();
		scr_s.add("15 Seconds");
		scr_s.add("30 Seconds");
		scr_s.add("1 Minute");
		scr_s.add("2 Minutes");
		scr_s.add("10 Minutes");
		scr_s.add("30 Minutes");
		
		List <String> perm_type_s = new ArrayList<String>();
		perm_type_s.add("LOC");
		perm_type_s.add("WIFI");
		
		//showSavedLocations();
		
		permName = (EditText)findViewById( R.id.permNameTxt );
		permLat  = (EditText)findViewById( R.id.permLatTxt );
		permLng  = (EditText)findViewById( R.id.permLngTxt );
		
		lblLat = (TextView)findViewById( R.id.permLat );
		lblLng = (TextView)findViewById( R.id.permLng );
		
		
		wifi_spinner = (Spinner)findViewById( R.id.wifi_status );
		blut_spinner = (Spinner)findViewById( R.id.blut_status );
		scr_spinner = (Spinner)findViewById( R.id.scr_status );
		permType    = (Spinner) findViewById( R.id.perm_type );
		
		ArrayAdapter wifi_s_adapter       = new ArrayAdapter( this, android.R.layout.simple_spinner_item, wifi_s );
		ArrayAdapter blut_s_adapter       = new ArrayAdapter( this, android.R.layout.simple_spinner_item, blut_s );
		ArrayAdapter scr_s_adapter        = new ArrayAdapter( this, android.R.layout.simple_spinner_item, scr_s );
		ArrayAdapter perm_type_s_adapter  = new ArrayAdapter( this, android.R.layout.simple_spinner_item, perm_type_s );
		
		wifi_spinner.setAdapter(wifi_s_adapter);
		blut_spinner.setAdapter(blut_s_adapter);
		scr_spinner.setAdapter(scr_s_adapter);
		permType.setAdapter(perm_type_s_adapter);
		
		permType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Log.d("PERMTYPE", position + " - "+id);
		        if( id == 1 ){
		        	//permLat.setFocusable(false);
		        	//permLng.setFocusable(false);
		        	
		        	//permLat.setVisibility(View.INVISIBLE);
		        	permLng.setVisibility(View.INVISIBLE);
		        	
		        	//lblLat.setVisibility(View.INVISIBLE);
		        	lblLat.setText("SSID Name");
		    		lblLng.setVisibility(View.INVISIBLE);
		        	
		        	permLat.setText("");
		        	permLng.setText("");
		        	
		        }else{
		        	//permLat.setFocusable(true);
		        	//permLng.setFocusable(true);
		        	//lblLat.setVisibility(View.VISIBLE);
		        	lblLat.setText("Latitude");
		    		lblLng.setVisibility(View.VISIBLE);
		        	
		        	//permLat.setVisibility(View.VISIBLE);
		        	permLng.setVisibility(View.VISIBLE);
		        }
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		Button btnAdd = (Button)findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				saveLocationSettings( permName.getText().toString(), Long.parseLong( permLat.getText().toString() ), 
						Long.parseLong( permLng.getText().toString() ), wifi_spinner.getSelectedItem().toString(), 
						blut_spinner.getSelectedItem().toString(), scr_spinner.getSelectedItem().toString() );
				*/
				toastMessage( permLat.getText().toString() );
				if( permType.getSelectedItem().toString().equals("WIFI") ){
					saveLocationSettings( permName.getText().toString().trim(), permType.getSelectedItem().toString(), permLat.getText().toString().trim(), 0.0, 
						0.0, wifi_spinner.getSelectedItem().toString(), 
						blut_spinner.getSelectedItem().toString(), scr_spinner.getSelectedItem().toString() );
				}else{
					saveLocationSettings( permName.getText().toString().trim(), permType.getSelectedItem().toString(), "", Double.parseDouble( permLat.getText().toString().trim() ), 
							Double.parseDouble( permLng.getText().toString().trim() ), wifi_spinner.getSelectedItem().toString(), 
							blut_spinner.getSelectedItem().toString(), scr_spinner.getSelectedItem().toString() );
				}
			}
		} );
		
		Button btnDelete = (Button)findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				saveLocationSettings( permName.getText().toString(), Long.parseLong( permLat.getText().toString() ), 
						Long.parseLong( permLng.getText().toString() ), wifi_spinner.getSelectedItem().toString(), 
						blut_spinner.getSelectedItem().toString(), scr_spinner.getSelectedItem().toString() );
						*/
				deletePermissionSetting( permName.getText().toString().trim() );
			}
			
		});
		
		Button btnGetLoc = (Button)findViewById(R.id.btnGetLocation);
		btnGetLoc.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Double[] pos = getCurrentLocation();
		
				permLng.setText( String.valueOf(pos[0]) );
				permLat.setText( String.valueOf(pos[1]) );
			}
			
		});
		
		
		Button btnLocPerms = (Button)findViewById(R.id.btnLocationList);
		btnLocPerms.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent( getApplicationContext(), LocationPermissionsActivity.class );
				startActivity( i );
			}
			
		});
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
	
	private Double[] getCurrentLocation()
	{
		//Map<Double, Double> out = new HashMap<Double, Double>();
		Double[] out = new Double[2];
		Criteria ctr = new Criteria();
		
		ctr.setAccuracy(Criteria.ACCURACY_COARSE);
		
		//ctr.setAccuracy(Criteria.ACCURACY_FINE);
		
		try{
			LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
			//Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location location = lm.getLastKnownLocation( lm.getBestProvider(ctr, false) );
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			
			
			out[0] = longitude;
			out[1] = latitude;
		}catch( Exception e ){}
		
		return out;
	}
	/*
	public void showSavedLocations(){
		// Setting TextView
		TextView txtPermsList = (TextView)findViewById(R.id.permsList);
		txtPermsList.setMovementMethod(new ScrollingMovementMethod());

		Cursor cursor = data.getSavedLocPermsListCursor();

		cursor.moveToFirst();
		txtPermsList.append("Name Lat Long Wifi BLT Screen Timeout\n\n");
		while( cursor.isAfterLast() == false ){

			//Log.d("XXXXXXXX", "Installed package :" + cursor.getString(0));
			//lst.add( cursor.getString(0) );
			txtPermsList.append( cursor.getString(0)+" - "+cursor.getString(1)+" - "+cursor.getString(2)+
					" - "+cursor.getString(3)+" - "+cursor.getString(4)+" - "+cursor.getString(5)+"\n" );
			cursor.moveToNext();
		}
		
		

		cursor.close();
				//
	}
	*/
	
	public void deletePermissionSetting( String perm ){
		toastMessage( "Deleting Location Settings" );
		data.deletePermissionSetting( perm );
		
		//TextView txtPermsList = (TextView)findViewById(R.id.permsList);
		//txtPermsList.setText("");
		//showSavedLocations();
	}
	
	public void saveLocationSettings( String name, String type, String ssid,  Double lat, Double lng, String wifi, String blu, String scr  ){
		Cursor cursor;
		
		if( type.equals("LOC") ){
			cursor = data.getLocationPermissions(lat, lng);
		}else{
			cursor = data.getLocationPermissions(ssid);
		}
		String wifi_s = (wifi.equals("On"))? "1" : "0";
		String blut_s = (blu.equals("On"))? "1" : "0";
	
		if( cursor != null ){
			cursor.moveToFirst();
			if( cursor.getCount() > 0 ){
		//if( data.checkLocationPermission( lat, lng ) ){
				if( type.equals("LOC") ){
					toastMessage( "These Coordinates are Already Defined." );
				}else{
					toastMessage( "This SSID is Already Defined." );
				}
			}else{
				toastMessage( "Saving Settings" );
				
					data.saveLocationPermission( name, type, ssid, lat, lng, wifi_s, blut_s, scr );
			}
			
			cursor.close();
		}else{
			//toastMessage( "sdsdsdsdsds" );
			toastMessage( "Saving Settings" );
			//if( type.equals("WIFI") ){
			data.saveLocationPermission( name, type, ssid, lat, lng, wifi_s, blut_s, scr );
		}
		
		//TextView txtPermsList = (TextView)findViewById(R.id.permsList);
		//txtPermsList.setText("");
		//showSavedLocations();
		//data.close();
	}
	
	private void toastMessage(String str){
    	Toast t = Toast.makeText(this, str, Toast.LENGTH_SHORT);
    	t.show();
    }
}
