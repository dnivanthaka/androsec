package com.service.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.demo.main.R;
import com.service.data.ServiceDataSource;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AppReportActivity extends Activity {
	//MyAdapter mListAdapter;
	private ServiceDataSource data;
	private Button updateReport;
	private TextView appReport;
	private String MalwareListURL = "";
	private String phoneID        = "";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appreport);
		
		data = new ServiceDataSource( this );
		data.open();
		
		MalwareListURL = data.getGlobalParam( "malware_list_path" );
		//MalwareListURL = "http://192.168.1.2/~dinusha/androsec/malware_list.php";
		
		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		phoneID = tManager.getDeviceId();
		
		appReport = (TextView)findViewById(R.id.appsReportList);
		updateReport = (Button)findViewById(R.id.btnUpdateAppReport);
		
		appReport.setMovementMethod(new ScrollingMovementMethod());
		
		updateReport.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//runCommand();
				//readLog();
				//runStrace();
				//testRooting();
				//launchActivity( "location_settings" );
				//launchActivity( "global_settings" );
				if( isNetworkAvailable() ){
					if( MalwareListURL.length() > 4 ){
						toastMessage( "Downloading updated list...." );
						updateAppReport();
						populateList();
					}else{
						toastMessage( "Please Enter a Valid Update URL" );
					}
				}else{
					toastMessage( "No Internet Connection Available" );
				}
			}
		} );
		
		//Iterator<String> it = appList.iterator();
		
		populateList();
		
	}
	
	private void populateList()
	{
		
		Cursor cursor = data.getSavedAppReportCursor();
		
		appReport.setText("");
		
		int i = 0;
		
		cursor.moveToFirst();
		while( cursor.isAfterLast() == false ){
			//String isTraced = (cursor.getString(2) != null) ? "<---- TRACED" : "Not Traced";
			//boolean isT = (cursor.getString(2) != null) ? true : false;
			//Log.d("XXXXXXXX", "Installed package :" + cursor.getString(0));
			
			Log.d("APPLIST", cursor.getString(0)+"("+cursor.getString(1)+") - "+cursor.getString(2));
			
			//lst.add( cursor.getString(0) );
			//if(isT){
				//txtAppsList.setTextColor(Color.parseColor("#FFFFFF"));
				appReport.append( cursor.getString(0)+"("+cursor.getString(1)+") - "+cursor.getString(2)+"\n" );
			//}else{
				//txtAppsList.setTextColor(Color.parseColor("#FF0000"));
				//txtAppsList.append( cursor.getString(0)+" - "+isTraced+"\n" );
			//}
			i++;
			
			cursor.moveToNext();
		}
		
		cursor.close();
		/*
		while(it.hasNext()){
			txtAppsList.append( it.next()+"\n" );
		}
		*/
	}
	
	private void toastMessage(String str){
    	Toast t = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
    	t.show();
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
	/*
	
	private class MyAdapter extends ResourceCursorAdapter {

        public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.mylist, cur);
        }

        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.mylist, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cur) {
            TextView tvListText = (TextView)view.findViewById(R.id.list_text);
            CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.list_checkbox);

            tvListText.setText(cur.getString(cur.getColumnIndex(Datenbank.DB_NAME)));
            cbListCheck.setChecked((cur.getInt(cur.getColumnIndex(Datenbank.DB_STATE))==0? false:true))));
        }
    }
	*/
	
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


	void saveAppReport( String path ) throws URISyntaxException, ClientProtocolException, IOException{
		BufferedReader in = null;
		 HttpClient client;
		 HttpGet method;
		 String url = path+"?p_id="+phoneID;
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
			             String[] RowData = line.split(",");
			             
			             appName    = RowData[0].trim();
			             pckName    = RowData[1].trim();
			             appDetails = RowData[2].trim();
			             appScore   = RowData[3].trim();
			             //value = RowData[1];
			            // do something with "data" and "value"
			             
			             //Log.d("## Scanning ##", appName + " - " + pckName + " - " + appScore );
			             
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

	// Checking for Internet connection
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
}
