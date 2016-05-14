package com.service.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo.main.R;
import com.service.data.ServiceDataSource;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocationPermissionsActivity extends Activity {
	Spinner wifi_spinner;
	Spinner blut_spinner;
	Spinner scr_spinner;
	EditText permName;
	EditText permLat;
	EditText permLng;
	
	private ServiceDataSource data;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.locpermslist);
		
		data = new ServiceDataSource( this );
		data.open();
		
		showSavedLocations();
	}
	
	@Override
    protected void onPause() 
	{
        super.onPause();
        
        data.close();

    }
	
	@Override
    protected void onResume() 
    {
		super.onResume();
		
		data.open();
    }
	
	public void showSavedLocations()
	{
		// Setting TextView
		TextView txtPermsList = (TextView)findViewById(R.id.txtPermsList);
		txtPermsList.setMovementMethod(new ScrollingMovementMethod());

		Cursor cursor = data.getSavedLocPermsListCursor();

		cursor.moveToFirst();
		txtPermsList.append("---------------------------------------------------------------\n");
		txtPermsList.append("| Name | Type | SSID | Lat | Long | Wifi | BLT | Screen Timeout\n");
		txtPermsList.append("---------------------------------------------------------------\n");
		while( cursor.isAfterLast() == false ){

			//Log.d("XXXXXXXX", "Installed package :" + cursor.getString(0));
			//lst.add( cursor.getString(0) );
			txtPermsList.append( cursor.getString(0)+" | "+cursor.getString(1)+" | "+cursor.getString(2)+
					" | "+cursor.getString(3)+" | "+cursor.getString(4)+" | "+cursor.getString(5)+" | "+cursor.getString(6)+" | "+cursor.getString(7)+"\n" );
			txtPermsList.append("---------------------------------------------------------------\n");
			cursor.moveToNext();
		}

		cursor.close();
				//
	}
	
	private void toastMessage(String str)
	{
    	Toast t = Toast.makeText(this, str, Toast.LENGTH_SHORT);
    	t.show();
    }
}
