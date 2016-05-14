package com.service.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.demo.main.R;
import com.service.data.ServiceDataSource;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Contacts.People;
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

public class AppTracerControlActivity extends Activity {
	//MyAdapter mListAdapter;
	private ServiceDataSource data;
	private Spinner spinner;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apptrack);
		
		data = new ServiceDataSource( this );
		data.open();
		//List<String> appList = data.getSavedAppsList();
		//AppProps[] ap = null;
		ArrayList<AppProps> ap = new ArrayList<AppProps>();
		//setContentView(R.layout.apptrack);
		
		
		//Cursor myCur = null;
		
		//myCur = this.getContentResolver().query(People.CONTENT_URI, null, null, null, null);
        //startManagingCursor(myCur);

        //myCur = do_stuff_here_to_obtain_a_cursor_of_query_results();

        //mListAdapter = new MyAdapter(AppTracerControlActivity.this, myCur);
        //setListAdapter(mListAdapter);
		
		/*
		
		ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.simple_list_item_single_choice,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
				myCur,                                              // Pass in the cursor to bind to.
                new String[] {People.NAME, People.NUMBER},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1, android.R.id.text2});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        setListAdapter(adapter);
		*/
		//Log.d("App Size", String.valueOf( appList.size()));
		
		spinner = (Spinner)findViewById(R.id.applist_spinner);
		//Textview Display
		TextView txtAppsList = (TextView)findViewById(R.id.appsList);
		
		txtAppsList.setMovementMethod(new ScrollingMovementMethod());
		
		//Iterator<String> it = appList.iterator();
		
		Cursor cursor = data.getSavedAppsListCursor();
		
		int i = 0;
		
		cursor.moveToFirst();
		while( cursor.isAfterLast() == false ){
			String isTraced = (cursor.getString(2) != null) ? "<---- TRACED" : "Not Traced";
			boolean isT = (cursor.getString(2) != null) ? true : false;
			//Log.d("XXXXXXXX", "Installed package :" + cursor.getString(0));
			
			Log.d("LOCLIST", "Installed package :" + cursor.getString(0)+ " -> "+isTraced+" "+cursor.getString(2));
			
			//lst.add( cursor.getString(0) );
			//if(isT){
				//txtAppsList.setTextColor(Color.parseColor("#FFFFFF"));
				txtAppsList.append( cursor.getString(0)+" - "+isTraced+"\n" );
			//}else{
				//txtAppsList.setTextColor(Color.parseColor("#FF0000"));
				//txtAppsList.append( cursor.getString(0)+" - "+isTraced+"\n" );
			//}
			
			//txtAppsList.setTextColor(Color.parseColor("#FF0000"));
			
			ap.add( new AppProps(cursor.getString(0), cursor.getString(1)) );
			
			i++;
			
			cursor.moveToNext();
		}
		
		cursor.close();
		/*
		while(it.hasNext()){
			txtAppsList.append( it.next()+"\n" );
		}
		*/
		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, appList);
		ArrayAdapter<AppProps> adapter = new ArrayAdapter<AppProps>(this,android.R.layout.simple_spinner_item, ap);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		Button btnStart = (Button)findViewById(R.id.btnRun);
		
		btnStart.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				//Log.d("Spinner@@", spinner.getSelectedItem().toString());
				//AppGlobal.getinstance().setAppToTrack( spinner.getSelectedItem().toString() );
				AppProps a = (AppProps)spinner.getSelectedItem();
				
				Log.d("Spinner@@", ""+ServiceDemo.getPidFromPs("strace"));
				
				if( ServiceDemo.getPidFromPs("strace") > 0 ){
					ServiceDemo.killProcess( ServiceDemo.getPidFromPs("strace") );
				}
				
				AppGlobal.getinstance().setAppToTrack( a.packageName );
				
				toastMessage( "App "+a.appName+" would be traced upon launch." );
				//if( AppGlobal.getinstance().getServiceStatus() )
					//toastMessage( "Please Start Service!!!" );
			}
		} );
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
}
