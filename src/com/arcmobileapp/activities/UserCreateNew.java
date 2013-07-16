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
import com.arcmobileapp.web.CreateUserTask;
import com.arcmobileapp.web.GetTokenTask;

public class UserCreateNew extends BaseActivity {

	private TextView emailTextView;
	private TextView passwordTextView;
	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_create_new);
		
		emailTextView = (TextView) findViewById(R.id.new_email_text);
		passwordTextView = (TextView) findViewById(R.id.new_password_text);
		
		loadingDialog = new ProgressDialog(UserCreateNew.this);
		loadingDialog.setTitle("Registering");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onRegisterClicked(View view) {

		if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 ){
			//Send the login
			login();
		}else{
			toastShort("Please enter an email address and password.");
		}
	}


	private void login(){
		
		
		loadingDialog.show();
		String sendEmail = (String) emailTextView.getText().toString();
		String sendPassword = (String) passwordTextView.getText().toString();

		String firstName = "test";
		String lastName = "test";
		
		CreateUserTask createUserTask = new CreateUserTask(sendEmail, sendPassword, firstName, lastName, false, getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				UserCreateNew.this.loadingDialog.hide();
				if(getSuccess()) {
					ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());


					if(getDevToken()!=null) {

						toastShort("Registration Successful!");
						
						Intent goBackProfile = new Intent(getApplicationContext(), UserProfile.class);
						goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(goBackProfile);
						

					}else{
						toastShort("Registration error, please try again.");

					}
									
				
				}else{
					toastShort("Registration error, please try again.");
				}
			}
		};
		createUserTask.execute();
	     
		
	}
	
	

}
