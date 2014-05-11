package com.example.locationanalytics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

//import net.sf.json.JSONObject;

public class PreferenceScreen extends Activity {

	// Your Facebook APP ID
	private static String APP_ID = "248970831974575"; // Replace your App ID
														// here

	// Instance of Facebook Class
	private Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	Button btnFbLogin;
	
	String fb_status="logged out";

	String fName;
	String lName;
	String userName;
	String password;
	String gender;
	String prefernceString;
	OnClickListener checkBoxListener;
	private RadioGroup radioGroupId;
	private RadioButton radioGenderButton;
	Intent toAnotherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference_screen);

		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		btnFbLogin = (Button) findViewById(R.id.btn_fblogin);

		btnFbLogin.setBackgroundResource(R.drawable.fbconnect);


		btnFbLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (fb_status.equalsIgnoreCase("logged out"))
				{
				loginToFacebook();
				
				}
				else
				{
					logoutFromFacebook();
					btnFbLogin.setBackgroundResource(R.drawable.fbconnect);

					
				}
			}
		});

		final CheckBox cb1 = (CheckBox) findViewById(R.id.checkBox1);
		final CheckBox cb2 = (CheckBox) findViewById(R.id.checkBox2);
		final CheckBox cb3 = (CheckBox) findViewById(R.id.checkBox3);
		final CheckBox cb4 = (CheckBox) findViewById(R.id.checkBox4);
		final CheckBox cb5 = (CheckBox) findViewById(R.id.checkBox5);
		final CheckBox cb6 = (CheckBox) findViewById(R.id.checkBox6);

		checkBoxListener = new OnClickListener() {

			public void onClick(View v) {

				StringBuilder sb = new StringBuilder();

				if (cb1.isChecked()) {
					// prefernceString = cb1.getText().toString() + " ";
					sb.append(cb1.getText());
					sb.append('&');
				}
				if (cb2.isChecked()) {
					// prefernceString += cb2.getText().toString() + " ";
					sb.append(cb2.getText());
					sb.append('&');

				}
				if (cb3.isChecked()) {
					// prefernceString += cb3.getText().toString() + " ";
					sb.append(cb3.getText());
					sb.append('&');

				}
				if (cb4.isChecked()) {
					// prefernceString += cb4.getText().toString() + " ";
					sb.append(cb4.getText());
					sb.append('&');

				}
				if (cb5.isChecked()) {
					// prefernceString += cb5.getText().toString();
					sb.append(cb5.getText());
					sb.append('&');

				}
				if (cb6.isChecked()) {
					// prefernceString += cb6.getText().toString();
					sb.append(cb6.getText());
					sb.append('&');

				}

				prefernceString = sb.toString();

			}

		};

		cb1.setOnClickListener(checkBoxListener);
		cb2.setOnClickListener(checkBoxListener);
		cb3.setOnClickListener(checkBoxListener);
		cb4.setOnClickListener(checkBoxListener);
		cb5.setOnClickListener(checkBoxListener);
		cb6.setOnClickListener(checkBoxListener);

		Button btnSubmit = (Button) findViewById(R.id.btnSubmit);

		btnSubmit.setOnClickListener(

		new View.OnClickListener()

		{
			HttpResponse resp;
			StringEntity entity;

			public void onClick(View aView) {

				Bundle extras = getIntent().getExtras();
				if (extras != null) {
					fName = getIntent().getStringExtra("fName");
					lName = getIntent().getStringExtra("lName");
					userName = getIntent().getStringExtra("userName");
					password = getIntent().getStringExtra("password");

					gender = getIntent().getStringExtra("gender");

					
					new RequestTask()
					.execute("http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/postRegistrationDetails");

//					new RequestTask()
//							.execute("http://10.0.0.5:8080/RESTfulProject/REST/WebService/postRegistrationDetails");

					toAnotherActivity = new Intent(aView.getContext(),
							HomeScreen.class);

				}

			}
		});

	}

	@SuppressWarnings("deprecation")
	public void loginToFacebook() {
		mPrefs = getPreferences(MODE_PRIVATE);

		String access_token = "";
		access_token = mPrefs.getString("access_token", null);

		if (access_token != null)

		{
			Log.d("ACccc", access_token);

		}
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {

			// Session session = Session.getActiveSession();
			// if (session == null) {
			// session = new Session(getApplicationContext());
			// }
			// Session.setActiveSession(session);

			facebook.authorize(this, new String[] { "email", "publish_stream",
					"user_checkins", "publish_checkins" },
					new DialogListener() {

						@Override
						public void onCancel() {
							// Function to handle cancel event
						}

						@Override
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();
							getProfileInformation();

						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

					});
		}
	}

	@SuppressWarnings("deprecation")
	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {


				Log.d("Profile", response);
				String json = response;


				try {
					JSONObject profile = new JSONObject(json);
					// getting name of the user
					final String name = profile.getString("name");
					// getting email of the user
					final String email = profile.getString("email");

					//getGraphAPI();
					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
//							Toast.makeText(getApplicationContext(),
//									"Name: " + name + "\nEmail: " + email,
//									Toast.LENGTH_LONG).show();
						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
		
		btnFbLogin.setBackgroundResource(R.drawable.fblogout);
		fb_status = "logged in";

	}
	
	
	public void getGraphAPI()
	{
		
		String access_token = facebook.getAccessToken();
		Session session = Session.getActiveSession();
		if (session == null) {
			session = new Session(getApplicationContext());
		}
		Session.setActiveSession(session);

		new Request(session, "/me/interests", null, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {

						Log.d("Response", response.toString());
						/* handle the result */
					}
				}).executeAsync();

		
	}

	@SuppressWarnings("deprecation")
	public void logoutFromFacebook() {
		fb_status = "logged out";

		mAsyncRunner.logout(this, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Logout from Facebook", response);
				if (Boolean.parseBoolean(response) == true) {
					// User successfully Logged out
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_screen, menu);
		return true;
	}

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			System.out.println("Inside AsyncTask");

			String name = null;
			
			String URL = "http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/postRegistrationDetails";

//			String URL = "http://10.0.0.5:8080/RESTfulProject/REST/WebService/postRegistrationDetails";
			ArrayList<NameValuePair> postParameters;

			postParameters = new ArrayList<NameValuePair>();
			System.out.println("Name: " + fName);
			System.out.println("Name: " + lName);
			System.out.println("Name: " + userName);
			System.out.println("gender: " + gender);
			// Log.d("Name:",name);
			postParameters.add(new BasicNameValuePair("firstname", fName));
			postParameters.add(new BasicNameValuePair("lastname", lName));
			postParameters.add(new BasicNameValuePair("username", userName));
			postParameters.add(new BasicNameValuePair("password", password));
			postParameters.add(new BasicNameValuePair("gender", gender));
			postParameters.add(new BasicNameValuePair("preferences",
					prefernceString));

			JSONObject json;
			try {
				HttpClient httpclient = new DefaultHttpClient();
//				HttpPost post = new HttpPost("http://10.0.0.5:8080/RESTfulProject/REST/WebService/postRegistrationDetails");

				HttpPost post = new HttpPost("http://cmpe295-androidwebservice.herokuapp.com/REST/WebService/postRegistrationDetails");

				post.setHeader("Content-type",
						"application/x-www-form-urlencoded");
				post.setEntity(new UrlEncodedFormEntity(postParameters));

				HttpResponse resp = httpclient.execute(post);

				startActivityForResult(toAnotherActivity, 0); // to main screen

				name = "Success";

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return name;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
		}
	}
}
