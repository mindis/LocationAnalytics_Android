package com.example.locationanalytics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class HomeScreen extends Activity {

	// // Your Facebook APP ID
	// private static String APP_ID = "248970831974575"; // Replace your App ID
	// here
	//
	// // Instance of Facebook Class
	// private Facebook facebook;
	// private AsyncFacebookRunner mAsyncRunner;
	// String FILENAME = "AndroidSSO_data";
	// private SharedPreferences mPrefs;

	Button btnGo;
	Button btnRegister;
	Button btnFbLogin;
	EditText userName;
	EditText password;

	String strUsername;
	String strPassword;

	Intent toAnotherActivity;
	Intent toAnotherActivity1;
	int userid;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		// facebook = new Facebook(APP_ID);
		// mAsyncRunner = new AsyncFacebookRunner(facebook);
		//
		// btnFbLogin = (Button) findViewById(R.id.btn_fblogin);
		//
		// btnFbLogin.setBackgroundResource(R.drawable.facebook_login_button);
		//
		//
		//
		// btnFbLogin.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// loginToFacebook();
		// }
		// });

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String loginstatus = getIntent().getStringExtra("loginstatus");
			if (loginstatus.equalsIgnoreCase("Fail")) {

				Toast.makeText(this, "Login Failed. Try Again.",
						Toast.LENGTH_LONG).show();

			}

		}
		btnGo = (Button) findViewById(R.id.btnGo);

		btnGo.setOnClickListener(

		new View.OnClickListener()

		{

			public void onClick(View aView) {

				new RequestTask()
						.execute("http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/Login");

				
//				new RequestTask()
//				.execute("http://10.0.0.5:8080/RESTfulProject/REST/WebService/Login");

		
				
				toAnotherActivity = new Intent(aView.getContext(),
						MainScreen.class);

				toAnotherActivity1 = new Intent(aView.getContext(),
						HomeScreen.class);
			}

		});

		btnRegister = (Button) findViewById(R.id.btnRegister);

		btnRegister.setOnClickListener(

		new View.OnClickListener()

		{

			public void onClick(View aView) {
				Intent toAnotherActivity = new Intent(aView.getContext(),
						RegisterScreen.class);
				startActivityForResult(toAnotherActivity, 0);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = "Success";
			try {

				userName = (EditText) findViewById(R.id.txtUserName);
				password = (EditText) findViewById(R.id.txtPassword);
				strUsername = userName.getText().toString();
				strPassword = password.getText().toString();

				
				response = httpclient.execute(new HttpGet(
						"http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/Login/"
								+ strUsername + "/" + strPassword));
				
//				response = httpclient.execute(new HttpGet(
//						"http://10.0.0.5:8080/RESTfulProject/REST/WebService/Login/"
//								+ strUsername + "/" + strPassword));
//


				StatusLine statusLine = response.getStatusLine();

				Log.d("Deal http", "done");
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

					String respStr = EntityUtils.toString(response.getEntity(),
							"UTF-8");

					if (respStr.equalsIgnoreCase("Failure")) // failure
					{

						toAnotherActivity1.putExtra("loginstatus", "Fail");

						startActivityForResult(toAnotherActivity1, 0); // to
																		// login
																		// screen

						return respStr;

					} else // successfull --- comes with userid
					{

						userid = Integer.parseInt(respStr);
						toAnotherActivity.putExtra("userid", userid);
						startActivityForResult(toAnotherActivity, 0); // to main
																		// screen

						return respStr;

					}
					// if (respStr.equalsIgnoreCase("Success")) {
					//
					//
					// startActivityForResult(toAnotherActivity, 0);
					// return respStr ;
					//
					//
					// } else {
					//
					// startActivityForResult(toAnotherActivity1, 0);
					// return respStr ;
					//
					//
					// //startActivityForResult(toAnotherActivity, 0);
					//
					// }

					// JSONObject json = new JSONObject(respStr);
					//
					// String adname = json.getString("adname");
					//
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
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				return responseString;
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
