package com.example.locationanalytics;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

//import com.example.cwac.locpoll.LocationPoller;

public class MainScreen extends MapActivity implements LocationListener {

	private static final String TAG = "LocationActivity";

	int notifcntCount = 0;

	public static MainScreen instance = null;

	private static final int PERIOD = 3000000; // 
	private PendingIntent pi = null;
	private AlarmManager mgr = null;

	LocationManager locationManager; // <2>
	Geocoder geocoder; // <3>
	TextView locationText;
	MapView map;
	MapController mapController; // <4>
	String bestProvider;
	Location location;
	public double latitude;
	public double longitude;
	NotificationManager NM;
	int userId;

	int dealId;

	String adname;
	String addesc;

	Intent notificationIntent;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		instance = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mgr = (AlarmManager) getSystemService(ALARM_SERVICE);

		Intent i = new Intent(this, LocationPoller.class);

		i.putExtra(LocationPoller.EXTRA_INTENT, new Intent(this,
				LocationReceiver.class));
		i.putExtra(LocationPoller.EXTRA_PROVIDER,
				LocationManager.NETWORK_PROVIDER);

		pi = PendingIntent.getBroadcast(this, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), PERIOD, pi);

		Toast.makeText(this, "Location polling every 1 minute begun",
				Toast.LENGTH_LONG).show();

		if (android.os.Build.VERSION.SDK_INT > 7) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userId = extras.getInt("userid");
		}

		getLocationInfo();

	}

	@SuppressWarnings("deprecation")
	public void sendNotification() {

		notifcntCount++;

		String title = adname;
		String subject = addesc;

		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		android.app.Notification notification = new android.app.Notification(
				R.drawable.location, "Message from Location Analytics",
				System.currentTimeMillis());

		notificationIntent = new Intent(this, DealScreen.class);

		// int dealId = 3;

		notificationIntent.putExtra("adId", dealId);

		// startActivityForResult(notificationIntent, 0); // to

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// PendingIntent.FLAG_UPDATE_CURRENT

		notification.setLatestEventInfo(MainScreen.this, title, subject,
				pendingIntent);
		NM.notify(notifcntCount, notification);
		NM.cancel(1);
		NM.cancel(2);
//		NM.cancel(3);
//		NM.cancel(4);

	}

	public void getLocationInfo() {

		// sendNotification();

		locationText = (TextView) this.findViewById(R.id.lblLocationInfo);
		map = (MapView) this.findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);

		mapController = map.getController(); // <4>
		mapController.setZoom(16);

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE); // <2>

		geocoder = new Geocoder(this); // <3>w

		Criteria criteria = new Criteria();

		criteria.setAltitudeRequired(true);

		bestProvider = locationManager.getBestProvider(criteria, false);
		// location = locationManager.getLastKnownLocation(bestProvider);
		location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // <5>

		if (location != null) {
			Log.d(TAG, location.toString());
			this.onLocationChanged(location); // <6>

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1000, 10, this); // <7>
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this); // <8>
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onLocationChanged(Location location) { // <9>
		Log.d(TAG, "onLocationChanged with location " + location.toString());
		// String text = String.format(
		// "Lat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f",
		// location.getLatitude(), location.getLongitude(),
		// location.getAltitude(), location.getBearing());
		String text = String.format("Latitude:\t %f\nLongitude:\t %f\n",
				location.getLatitude(), location.getLongitude());
		this.locationText.setText(text);

		try {
			List<Address> addresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 10); // <10>
			for (Address address : addresses) {
				this.locationText.append("\n" + address.getAddressLine(0));
			}

			int latitude = (int) (location.getLatitude() * 1000000);
			int longitude = (int) (location.getLongitude() * 1000000);

			GeoPoint point = new GeoPoint(latitude, longitude);
			mapController.animateTo(point); // <11>

		} catch (IOException ex) {
			Log.e("LocateMe", "Could not get Geocoder data", ex);
		}

		latitude = location.getLatitude();
		longitude = location.getLongitude();

		// new RequestTask()
		// .execute("http://10.0.0.5:8080/RESTfulProject/REST/WebService/getDeals");

		new RequestTask()
				.execute("http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/getDeals");

		setResult(RESULT_OK);
		if (saveUser()) {
			// Log.d(TAG, "onLocationChanged with location " +
			// location.toString());

		}

	}

	// public void signUp(View view){
	//
	// setResult(RESULT_OK);
	// if(saveUser())
	// finish();
	//
	// }

	public boolean saveUser() {
		try {

			// getConnectingDialog().show();
			JSONObject json = JsonFactory.userToJson(String.valueOf(latitude),
					String.valueOf(longitude));
			CustomHttpClient
					.executeHttpPost(UrlBuilder.toUrl("location"), json);
			// CustomHttpClient.executeHttpGet(UrlBuilder.toUrl("location"));

			return true;
		} catch (Exception e) {
			getErrorConnectionDialog().show();
			return false;
		}
	}

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;

			org.json.JSONArray contacts = null;
			try {

				
//				(37.3348958,-121.884925)
				
				
//				response = httpclient.execute(new HttpGet(
//						"http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/getDeals/37.3348958/-121.884925/" + userId));

				response = httpclient.execute(new HttpGet(
						"http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/getDeals/"
								+ latitude + "/" + longitude + "/" + userId));

				
//				response = httpclient.execute(new HttpGet(
//						"http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/getDeals/"
//								+ latitude + "/" + longitude + "/" + userId));
				
				

				// response = httpclient.execute(new HttpGet(
				// "http://10.0.0.5:8080/RESTfulProject/REST/WebService/getDeals/"
				// + latitude + "/" + longitude + "/" + userId ));

				StatusLine statusLine = response.getStatusLine();

				Log.d("Deal http", "done");
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

					String respStr = EntityUtils.toString(response.getEntity(),
							"UTF-8");

					JSONObject json = new JSONObject(respStr);

					contacts = json.getJSONArray("data");

					for (int i = 0; i < contacts.length(); i++) {
						JSONObject deal = contacts.getJSONObject(i);

						dealId = deal.getInt("adid");

						adname = deal.getString("adname");
						addesc = deal.getString("addescription");

						sendNotification();
					}

					// Log.d("Deal http", adname);

				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				// TODO Handle problems..
			} catch (IOException e) {
				// TODO Handle problems..
				Log.e("LocateMe", "Could not get Geocoder data", e);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
		}

	}

	private Dialog getConnectingDialog() {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Connecting");
		dialog = builder.create();
		return dialog;
	}

	private Dialog getErrorConnectionDialog() {
		Dialog dialog = null;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Connection error");
		builder.setMessage("We are sorry but you need Internet Connection to Go on");
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		dialog = builder.create();
		return dialog;
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
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
