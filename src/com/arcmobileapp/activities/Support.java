package com.arcmobileapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;

public class Support extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	
	public void emailNow(View view) {

		
		try{
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "support@arcmobileapp.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Feedback");
			startActivity(emailIntent); 
		}catch (Exception e){
			toastShort("Error opening email client, please try again");
		}
	}
	
	public void callNow(View view) {

		Intent intent = new Intent(Intent.ACTION_CALL);

		intent.setData(Uri.parse("tel:7083209272"));
		startActivity(intent);
		
	}
}
