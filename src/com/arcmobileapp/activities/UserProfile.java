package com.arcmobileapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;

public class UserProfile extends BaseActivity {

	private RelativeLayout loggedInView;
	private RelativeLayout loggedOutView;
	private TextView emailTextView;
	private TextView passwordTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		loggedInView = (RelativeLayout) findViewById(R.id.logged_in_view);
		loggedOutView = (RelativeLayout) findViewById(R.id.logged_out_view);
		emailTextView = (TextView) findViewById(R.id.email_text);
		passwordTextView = (TextView) findViewById(R.id.password_text);

		
		

	}

	@Override
	public void onResume(){
		super.onResume();
		
		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

		String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
		String customerEmail = myPrefs.getString(Keys.CUSTOMER_EMAIL);

		String passwordText = "**********";

	

		if (customerToken != null && customerToken.length() > 0){
			loggedInView.setVisibility(View.VISIBLE);
			loggedOutView.setVisibility(View.INVISIBLE);
			
			emailTextView.setText(customerEmail);
			
			passwordTextView.setText(passwordText);
			
		}else{
			loggedInView.setVisibility(View.INVISIBLE);
			loggedOutView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	
	public void onLogInClicked(View view) {

		Intent social = (new Intent(getApplicationContext(), UserLogin.class));
		startActivity(social);
		
	}
	
	public void onCreateNewClicked(View view) {

		Intent social = (new Intent(getApplicationContext(), UserCreateNew.class));
		startActivity(social);
	}
	
	
	public void onLogoutClicked(View view) {

		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

		myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, "");
		myPrefs.putAndCommitString(Keys.CUSTOMER_ID, "");
		myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, "");
		
		Intent goHome = new Intent(getApplicationContext(), Home.class);
		goHome.putExtra(Constants.LOGGED_OUT, true);
		goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goHome);
		
		//Intent social = (new Intent(getApplicationContext(), UserCreate.class));
		//startActivity(social);
	}
}
