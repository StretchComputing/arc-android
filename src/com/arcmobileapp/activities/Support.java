package com.arcmobileapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.dutchmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class Support extends BaseActivity {

	
	private TextView titleTextView;
	private TextView questionTextView;
	
	private TextView emailTextView;
	private TextView phoneTextView;
	
	private Button emailButton;
	private Button phoneButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			

			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_support);
			
			
			titleTextView = (TextView) findViewById(R.id.item_you_pay);
			titleTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			questionTextView = (TextView) findViewById(R.id.current_merchant);
			questionTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			emailTextView = (TextView) findViewById(R.id.textView3);
			emailTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			phoneTextView = (TextView) findViewById(R.id.textView4);
			phoneTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			emailButton = (Button) findViewById(R.id.button_email);
			emailButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			phoneButton = (Button) findViewById(R.id.button_call);
			phoneButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			
			//If there is a guest token or customer token, go to HOME
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			String customerEmail = myPrefs.getString(Keys.CUSTOMER_EMAIL);

			if (customerToken != null && customerToken.length() > 0){
				AppActions.add("Support - OnCreate - Viewed As Customer: " + customerEmail);

			}else{
				AppActions.add("Support - OnCreate - Viewed As Guest ");

			}
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("Support.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	
	public void emailNow(View view) {

		
		try{
			
			AppActions.add("Support - Send Email");

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "support@arcmobileapp.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Feedback");
			startActivity(emailIntent); 
		}catch (Exception e){

			toastShort("Error opening email client, please try again");
			(new CreateClientLogTask("Support.emailNow", "Exception Caught", "error", e)).execute();

		}
	}
	
	public void callNow(View view) {

		try {
			
			AppActions.add("Support - Phone Call");

			Intent intent = new Intent(Intent.ACTION_CALL);

			intent.setData(Uri.parse("tel:7083209272"));
			startActivity(intent);
		} catch (Exception e) {
			
			toastShort("Error opening phone client, please try again");

			(new CreateClientLogTask("Support.callNow", "Exception Caught", "error", e)).execute();

		}
		
	}
}
