package com.arcmobileapp.activities;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.GetTokenTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class InitActivity extends Activity {
	
	private Boolean doesHaveToken;
	private Boolean tokenDidFail = false;
	
	private TextView titleText;
	private TextView subTitleText;
	private TextView stepOne;
	private TextView stepTwo;
	private TextView stepthree;
	private TextView termsText;
	
	private Button startButton;
	private Button termsButton;
	private Button privacyButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			setContentView(R.layout.activity_init);
			
			titleText = (TextView)findViewById(R.id.current_merchant);
			titleText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			subTitleText = (TextView)findViewById(R.id.item_you_pay);
			subTitleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			stepOne = (TextView)findViewById(R.id.textView3);
			stepOne.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			stepTwo = (TextView)findViewById(R.id.amountText);
			stepTwo.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			stepthree = (TextView)findViewById(R.id.nameText);
			stepthree.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			termsText = (TextView)findViewById(R.id.text_enter_pin);
			termsText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			startButton = (Button)findViewById(R.id.button_call);
			startButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			termsButton = (Button)findViewById(R.id.button_email);
			termsButton.setTypeface(ArcMobileApp.getLatoLightTypeface());
			privacyButton = (Button)findViewById(R.id.Button01);
			privacyButton.setTypeface(ArcMobileApp.getLatoLightTypeface());

			//Get the token
			
			getGuestToken();
		} catch (Exception e) {
			(new CreateClientLogTask("InitActivity.onCreate", "Exception Caught", "error", e)).execute();

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
						
						int errorCode = getErrorCode();

						
						
						if(getSuccess()) {
							
							AppActions.add("Init Activity - Get Token Succeeded");

							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

							if(getDevToken()!=null) {

								myPrefs.putAndCommitString(Keys.GUEST_TOKEN, getDevToken());
								myPrefs.putAndCommitString(Keys.GUEST_ID, getDevCustomerId());
							}						
						
							tokenDidFail = false;
							doesHaveToken = true;
						}else{
							
							tokenDidFail = true;
							AppActions.add("Init Activity - Get Token Failed - Error Code:" + errorCode);

							if (errorCode != 0){
								toast("Unable to retrieve guest token, please try again.", 6);

							}
						}
					} catch (Exception e) {
						(new CreateClientLogTask("InitActivity.getGuestToken.onPostExecute", "Exception Caught", "error", e)).execute();

					}
				}
			};
			getTokenTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("InitActivity.getGuestToken", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
	public void onStartClicked(View view) {

		try {
			//Go Home
			
			if (tokenDidFail){
				getGuestToken();
				toast("Registering you as a guest, please wait a second then try again.", 6);

			}else{
				if (doesHaveToken){
					
					AppActions.add("Init Activity - Clicked Start - Have Guest Token");

							
					ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

					myPrefs.putAndCommitBoolean(Keys.AGREED_TERMS, true);

					startActivity(new Intent(getApplicationContext(), Home.class));
					overridePendingTransition(0, 0);
					finish();
				}else{
					
					AppActions.add("Init Activity - Clicked Start - No Guest Token Yet");

					toast("Registering you as a guest, please wait a second then try again.", 6);
				}
				
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("InitActivity.onStartClicked", "Exception Caught", "error", e)).execute();

		}
		

	}
	
	private void toast(String message, int duration) {
		Toast.makeText(getApplicationContext(), message, duration).show();
	}
	
	public void onTermsClicked(View view){
		
		try {
			AppActions.add("Init Activity - Terms Clicked");

			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://arc.dagher.mobi/html/docs/terms.html"));
			startActivity(browserIntent);
		} catch (Exception e) {
			(new CreateClientLogTask("InitActivity.onTermsClicked", "Exception Caught", "error", e)).execute();

		}
		
		
	}


	public void onPrivacyClicked(View view){
		
		try {
			AppActions.add("Init Activity - Privacy Clicked");

			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://arc.dagher.mobi/html/docs/privacy.html"));
			startActivity(browserIntent);
		} catch (Exception e) {
			(new CreateClientLogTask("InitActivity.onPrivacyClicked", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
}
