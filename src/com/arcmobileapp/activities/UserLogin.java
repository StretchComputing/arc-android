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
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.GetTokenTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class UserLogin extends BaseActivity {

	private TextView emailTextView;
	private TextView passwordTextView;
	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("User Login - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_user_login);
			
			emailTextView = (TextView) findViewById(R.id.register_email_text);
			passwordTextView = (TextView) findViewById(R.id.register_password_text);

			loadingDialog = new ProgressDialog(UserLogin.this);
			loadingDialog.setTitle("Logging In");
			loadingDialog.setMessage("Please Wait...");
			loadingDialog.setCancelable(false);
		} catch (Exception e) {
			(new CreateClientLogTask("UserLogin.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	public void onLogInClicked(View view) {


		try {
			if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 ){
				//Send the login
				AppActions.add("User Login - Login Clicked - Email: " + emailTextView.getText().toString());

				login();
			}else{
				toastShort("Please enter your email address and password.");
			}
		} catch (Exception e) {
			(new CreateClientLogTask("UserLogin.onLogInClicked", "Exception Caught", "error", e)).execute();

		}
	}


	private void login(){
		
		try {
			loadingDialog.show();
			String sendEmail = (String) emailTextView.getText().toString();
			String sendPassword = (String) passwordTextView.getText().toString();

			GetTokenTask getTokenTask = new GetTokenTask(sendEmail, sendPassword, false, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						UserLogin.this.loadingDialog.hide();
						if(getSuccess()) {
							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());


							if(getDevToken()!=null) {

								AppActions.add("User Login - Login Succeeded");

								myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, getDevToken());
								myPrefs.putAndCommitString(Keys.CUSTOMER_ID, getDevCustomerId());
								myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, UserLogin.this.emailTextView.getText().toString());

								toastShort("Login Successful!");
								
								Intent goBackProfile = new Intent(getApplicationContext(), Home.class);
								goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(goBackProfile);

							}else{
								toastShort("Login error, please try again.");

							}
											
						
						}else{
							
							int errorCode = getErrorCode();
							
							AppActions.add("User Login - Login Failed - ErrorCode: " + errorCode);

							if (errorCode == ErrorCodes.INCORRECT_LOGIN_INFO){
								toastShort("Invalid username/password, please try again.");

							}else{
								toastShort("Error logging in.  Dutch may be experiencing network issues, please try again.");

							}
						}
					} catch (Exception e) {
						(new CreateClientLogTask("UserLogin.login.onPostExecute", "Exception Caught", "error", e)).execute();

					}
				}
			};
			getTokenTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("UserLogin.login", "Exception Caught", "error", e)).execute();

		}

		
	}
}
