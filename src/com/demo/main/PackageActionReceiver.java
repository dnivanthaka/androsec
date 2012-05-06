package com.demo.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageActionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED)) {
            String added_package = intent.getData().toString();
            Log.i("PackageReceiver", "Package Added = " +added_package);

		}else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED)) {
            String removed_package = intent.getData().toString();
            //MyDBHelper DB = new MyDBHelper(context);
            Log.i("PackageReceiver", "Package removed = " +removed_package);
            //DB.open();
            //DB.deleteStmt = DB.db.compileStatement(QueryManager.ApplicationDelete);
            //DB.deleteStmt.bindString(1, removed_package);
            //DB.close();

			}
	}

}
