package com.example.locationanalytics;

import java.io.IOException;

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

import com.example.locationanalytics.DealScreen.RequestTask;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class DealScreen extends Activity {

	public TextView lblAdName;
	public TextView lblAdDescription;
	public TextView lblAdDetail;

	int adId;

	Button btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d("hii", "called");
		super.onCreate(savedInstanceState);
		Log.d("hii", "called");

		setContentView(R.layout.activity_deal_screen);
		Log.d("hii", "called");


		lblAdName = (TextView) this.findViewById(R.id.textView1);
	//	lblAdDescription = (TextView) this.findViewById(R.id.textView2);
	//	lblAdDetail = (TextView) this.findViewById(R.id.textView3);


		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			 adId = extras.getInt("adId");
			

		
			btnClose = (Button) findViewById(R.id.btnClose);

			btnClose.setOnClickListener(

			new View.OnClickListener()

			{

				public void onClick(View aView) {
					Intent toAnotherActivity = new Intent(aView.getContext(),
							MainScreen.class);
					startActivityForResult(toAnotherActivity, 0);
				}
			});
			
//			new RequestTask()
//			.execute("http://10.0.0.5:8080/RESTfulProject/REST/WebService/GetDealFromId");


			new RequestTask()
			.execute("http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/GetDealFromId");

			
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("hii", "called");

	}


	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.go_to_deal, menu);
		return true;
	}

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				
//				response = httpclient.execute(new HttpGet(
//						"http://10.0.0.5:8080/RESTfulProject/REST/WebService/GetDealFromId/"+adId));
			
				response = httpclient.execute(new HttpGet(
						"http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/GetDealFromId/"+adId));

				StatusLine statusLine = response.getStatusLine();
				
				Log.d("Deal http", "done");
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

					String respStr = EntityUtils.toString(response.getEntity(),
							"UTF-8");

					JSONObject json = new JSONObject(respStr);

					String adname = json.getString("adname");
					String adDesc = json.getString("addescription");

				//	Log.d("Deal http", adname);

					//lblAdName.setText(adname);
				//	lblAdDescription.setText(adDesc);
				//	lblAdDetail.setText("");
					return responseString;


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
}
