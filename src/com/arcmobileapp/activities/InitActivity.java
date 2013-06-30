package com.arcmobileapp.activities;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.arcmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.GetTokenTask;

public class InitActivity extends Activity {
	
	private Boolean doesHaveToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_init);

		//Get the token
		
		getGuestToken();
	}
	
	private void getGuestToken(){
		
		String uuid = UUID.randomUUID().toString();
		
		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
		myPrefs.putAndCommitString(Keys.MY_UUID, uuid);
		
		GetTokenTask getTokenTask = new GetTokenTask(uuid, uuid, true, getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if(getSuccess()) {
					ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

					
					if(getDevToken()!=null) {

						myPrefs.putAndCommitString(Keys.DEV_TOKEN, getDevToken());
						myPrefs.putAndCommitString(Keys.DEV_CUSTOMER_ID, getDevCustomerId());
					}						
					if(getProdToken()!=null) {
						myPrefs.putAndCommitString(Keys.PROD_TOKEN, getProdToken());
						myPrefs.putAndCommitString(Keys.PROD_CUSTOMER_ID, getProdCustomerId());
					}
					
					doesHaveToken = true;
				}
			}
		};
		getTokenTask.execute();
		
		
	}
	
	public void onStartClicked(View view) {

		//Go Home
		
		if (doesHaveToken){
			startActivity(new Intent(getApplicationContext(), Home.class));
			overridePendingTransition(0, 0);
			finish();
		}else{
			toast("Registering you as a guest, please wait a second then try again.", Toast.LENGTH_SHORT);
		}
		

	}
	
	private void toast(String message, int duration) {
		Toast.makeText(getApplicationContext(), message, duration).show();
	}

}
