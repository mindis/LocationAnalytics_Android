package com.example.locationanalytics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RegisterScreen extends Activity {
	Button btnNext;
	EditText fName;
	EditText lName;
	EditText userName;
	EditText password;
	RadioGroup radioGenderGroup;

	RadioButton radioButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_screen);

		btnNext = (Button) findViewById(R.id.btnNext);

		btnNext.setOnClickListener(

		new View.OnClickListener()

		{

			public void onClick(View aView) {

				fName = (EditText) findViewById(R.id.txtFname);
				lName = (EditText) findViewById(R.id.txtLname);
				userName = (EditText) findViewById(R.id.txtUserName);
				password = (EditText) findViewById(R.id.txtPassword);
				radioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);

				String strFname = fName.getText().toString();
				String strLname = lName.getText().toString();
				String strUsername = userName.getText().toString();
				String strPassword = password.getText().toString();

				radioButton = (RadioButton) findViewById(R.id.rbMale);
				String strGender;

				if (radioButton.isChecked()) {
					strGender = "Male";

				} else {
					strGender = "Female";

				}

				Intent prefernceScreenActivity = new Intent(aView.getContext(),
						PreferenceScreen.class);

				prefernceScreenActivity.putExtra("fName", strFname);
				prefernceScreenActivity.putExtra("lName", strLname);
				prefernceScreenActivity.putExtra("userName", strUsername);
				prefernceScreenActivity.putExtra("password", strPassword);
				prefernceScreenActivity.putExtra("gender", strGender);

				startActivityForResult(prefernceScreenActivity, 0);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_screen, menu);
		return true;
	}

}
