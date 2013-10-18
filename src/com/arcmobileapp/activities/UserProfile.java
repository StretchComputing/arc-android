package com.arcmobileapp.activities;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.dutchmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.GetTokenTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class UserProfile extends BaseActivity {

	private RelativeLayout loggedInView;
	private RelativeLayout loggedOutView;
	private TextView emailTextView;
	private TextView passwordTextView;
	private Button editServerButton;
	private Button signOutButton;
	private Button loginButton;
	private Button createButton;
	private Button paymentHistoryButton;

	private TextView helpItemText;
	private boolean isLeaving = false;
	private boolean isSignout = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			AppActions.add("UserProfile - OnCreate");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_user_profile);
			
			loggedInView = (RelativeLayout) findViewById(R.id.logged_in_view);
			loggedOutView = (RelativeLayout) findViewById(R.id.logged_out_view);
			emailTextView = (TextView) findViewById(R.id.email_text);
			passwordTextView = (TextView) findViewById(R.id.password_text);
			emailTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			passwordTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			helpItemText = (TextView) findViewById(R.id.help_item_text);
			helpItemText.setTypeface(ArcMobileApp.getLatoLightTypeface());

			editServerButton = (Button) findViewById(R.id.edit_server_button);
			signOutButton = (Button) findViewById(R.id.resendButton);
			loginButton = (Button) findViewById(R.id.button2);
			createButton = (Button) findViewById(R.id.button3);
			paymentHistoryButton = (Button)findViewById(R.id.Button01);
			
			editServerButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			signOutButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			loginButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			createButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			
			
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onCreate", "Exception Caught", "error", e)).execute();

		}

		
		

	}

	@Override
	public void onResume(){
		try {
			super.onResume();
			
			isLeaving = false;
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			String customerEmail = myPrefs.getString(Keys.CUSTOMER_EMAIL);

			String passwordText = "**********";


			//Show "logged in" or "logged out" view
			if (customerToken != null && customerToken.length() > 0){
				
				AppActions.add("UserProfile - OnResume - As Customer");

				loggedInView.setVisibility(View.VISIBLE);
				loggedOutView.setVisibility(View.INVISIBLE);
				
				emailTextView.setText(customerEmail);
				
				passwordTextView.setText(passwordText);
				
			}else{
				
				AppActions.add("UserProfile - OnResume - As Guest");

				loggedInView.setVisibility(View.INVISIBLE);
				loggedOutView.setVisibility(View.VISIBLE);
			}
			
			//Show/hide edit server button
			if (customerToken != null && customerToken.length() > 0 && myPrefs.getBoolean(Keys.IS_ADMIN)){
				editServerButton.setVisibility(View.VISIBLE);
			}else{
				editServerButton.setVisibility(View.INVISIBLE);
			}
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onResume", "Exception Caught", "error", e)).execute();

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
			
			AppActions.add("UserProfile - Login Clicked");

			
			
			if (!isLeaving){
				isLeaving = true;
				Intent social = (new Intent(getApplicationContext(), UserLogin.class));
				startActivity(social);
			}
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onLoginClicked", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	public void onCreateNewClicked(View view) {

		try {
			
			AppActions.add("UserProfile - Create Clicked");

		
			
			if (!isLeaving){
				isLeaving = true;
				Intent social = (new Intent(getApplicationContext(), UserCreateNew.class));
				startActivity(social);
			}
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onCreateNewClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void onLogoutClicked(View view) {

		try {
			
			if (!isSignout){
				isSignout = true;
				AppActions.add("UserProfile - Logout Clicked");

				ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

				myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, "");
				myPrefs.putAndCommitString(Keys.CUSTOMER_ID, "");
				myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, "");
				myPrefs.putAndCommitString(Keys.DUTCH_URL, "");

				myPrefs.putAndCommitBoolean(Keys.IS_ADMIN, false);

				getGuestToken();
			}
		
			
			//Intent social = (new Intent(getApplicationContext(), UserCreate.class));
			//startActivity(social);
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onLogoutClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	public void onViewPaymentHistoryClicked(View view) {

		try {
	
			Intent history = (new Intent(getApplicationContext(), PaymentHistory.class));
			startActivity(history);
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onViewPaymentHistoryClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void onEditServerClicked(View view) {
		
		AppActions.add("UserProfile - Edit Server Clicked");

		if (!isLeaving){
			isLeaving = true;
			Intent social = (new Intent(getApplicationContext(), EditServer.class));
			startActivity(social);
		}
		
	}

	
private void getGuestToken(){
		
		try {
			String uuid = UUID.randomUUID().toString();
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			myPrefs.putAndCommitString(Keys.MY_UUID, uuid);
			
			GetTokenTask getTokenTask = new GetTokenTask(uuid, uuid, true, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						isSignout = false;
						int errorCode = getErrorCode();

						
						
						if(getSuccess()) {
							
							AppActions.add("User Profile - Get Token Succeeded");

							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

							if(getDevToken()!=null) {

								myPrefs.putAndCommitString(Keys.GUEST_TOKEN, getDevToken());
								myPrefs.putAndCommitString(Keys.GUEST_ID, getDevCustomerId());
							}		
							
							Intent goHome = new Intent(getApplicationContext(), Home.class);
							goHome.putExtra(Constants.LOGGED_OUT, true);
							goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(goHome);
						
						}else{
							
							AppActions.add("User Profile - Get Token Failed - Error Code:" + errorCode);

							if (errorCode != 0){
								toastShort("Unable to complete logout, please try again.");

							}
						}
					} catch (Exception e) {
						(new CreateClientLogTask("InitActivity.getGuestToken.onPostExecute", "Exception Caught", "error", e)).execute();

					}
				}
			};
			getTokenTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.getGuestToken", "Exception Caught", "error", e)).execute();

		}
		
		
	}

}
