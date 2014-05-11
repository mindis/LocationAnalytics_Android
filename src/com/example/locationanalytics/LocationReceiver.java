package com.example.locationanalytics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
//import com.commonsware.cwac.locpoll.LocationPoller;
import com.example.locationanalytics.*;
import com.google.android.maps.GeoPoint;

public class LocationReceiver extends BroadcastReceiver implements
		LocationListener {

	public MainScreen mainScreenObj;
	Geocoder geocoder; // <3>

	NotificationManager NM;

	@Override
	public void onReceive(Context context, Intent intent) {

		MainScreen mainScreenObj1 = MainScreen.instance;

		File log = new File(Environment.getExternalStorageDirectory(),
				"LocationLog.txt");

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					log.getAbsolutePath(), log.exists()));

			out.write(new Date().toString());
			out.write(" : ");

			Bundle b = intent.getExtras();
			Location loc = (Location) b.get(LocationPoller.EXTRA_LOCATION);
			String msg;

			if (loc == null) {
				loc = (Location) b.get(LocationPoller.EXTRA_LASTKNOWN);

				if (loc == null) {
					msg = intent.getStringExtra(LocationPoller.EXTRA_ERROR);
				} else {
					msg = "TIMEOUT, lastKnown=" + loc.toString();
				}
			} else {
				msg = loc.toString();
				mainScreenObj1.getLocationInfo();
			//	this.sendNotification();
			}

			if (msg == null) {
				msg = "Invalid broadcast received!";
			}

			out.write(msg);
			out.write("\n");
			out.close();
		} catch (IOException e) {
			Log.e(getClass().getName(), "Exception appending to log file", e);
		}
	}

	@SuppressWarnings("deprecation")
	public void sendNotification()
	{
		String title = "hello";
		String subject = "Hi";
		String body = "bye";
		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification(
				android.R.drawable.stat_notify_more, title,
				System.currentTimeMillis());
		PendingIntent pending = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(), 0);
		notify.setLatestEventInfo(getApplicationContext(), subject, body,
				pending);
		NM.notify(0, notify);
	}

	private Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	private NotificationManager getSystemService(String notificationService) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {

	}
}
