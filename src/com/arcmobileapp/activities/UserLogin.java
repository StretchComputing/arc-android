package com.arcmobileapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.GetTokenTask;

public class UserLogin extends BaseActivity {

	private TextView emailTextView;
	private TextView passwordTextView;
	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		
		emailTextView = (TextView) findViewById(R.id.register_email_text);
		passwordTextView = (TextView) findViewById(R.id.register_password_text);

		loadingDialog = new ProgressDialog(UserLogin.this);
		loadingDialog.setTitle("Logging In");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	public void onLogInClicked(View view) {

		if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 ){
			//Send the login
			login();
		}else{
			toastShort("Please enter your email address and password.");
		}
	}


	private void login(){
		
		loadingDialog.show();
		String sendEmail = (String) emailTextView.getText().toString();
		String sendPassword = (String) passwordTextView.getText().toString();

		GetTokenTask getTokenTask = new GetTokenTask(sendEmail, sendPassword, false, getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				UserLogin.this.loadingDialog.hide();
				if(getSuccess()) {
					ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());


					if(getDevToken()!=null) {

						myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, getDevToken());
						myPrefs.putAndCommitString(Keys.CUSTOMER_ID, getDevCustomerId());
						myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, UserLogin.this.emailTextView.getText().toString());

						toastShort("Login Successful!");
						
						Intent goBackProfile = new Intent(getApplicationContext(), UserProfile.class);
						goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(goBackProfile);

					}else{
						toastShort("Login error, please try again.");

					}
									
				
				}else{
					toastShort("Invalid username/password, please try again.");
				}
			}
		};
		getTokenTask.execute();

		
	}
}
