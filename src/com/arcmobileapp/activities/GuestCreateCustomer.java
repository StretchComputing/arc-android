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
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.UpdateCustomerTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class GuestCreateCustomer extends BaseActivity {

	private Check theBill;
	private TextView emailTextView;
	private TextView passwordTextView;
	private ProgressDialog loadingDialog;

	@Override
	public void onBackPressed() {
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			
			AppActions.add("Guest Create Customer - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_guest_create_customer);
			
			theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);

			emailTextView = (TextView) findViewById (R.id.guest_email_textv);
			passwordTextView = (TextView) findViewById (R.id.guest_password_textv);
			
			loadingDialog = new ProgressDialog(GuestCreateCustomer.this);
			loadingDialog.setTitle("Creating Account");
			loadingDialog.setMessage("Please Wait...");
			loadingDialog.setCancelable(false);
			
		}catch(Exception e){
			(new CreateClientLogTask("GuestCreateCustomer.onCreate", "Exception Caught", "error", e)).execute();

		}
	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onCreateClicked(View view) {

		try {
			if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 ){
				//Send the login
				AppActions.add("Guest Create Customer - Create Clicked - Email: " + emailTextView.getText().toString());

				register();
			}else{
				toastShort("Please enter an email address and password.");
			}
		} catch (Exception e) {
			(new CreateClientLogTask("GuestCreateCustomer.onCreateClicked", "Exception Caught", "error", e)).execute();
		}
		
		
	}
	
	
	public void onNoThanksClicked(View view) {

		
		try {
			AppActions.add("Guest Create Customer - No Thanks Clicked");

			Intent goReview = new Intent(getApplicationContext(), Review.class);
			goReview.putExtra(Constants.INVOICE, theBill);
			startActivity(goReview);
		} catch (Exception e) {
			(new CreateClientLogTask("GuestCreateCustomer.onNoThanksClicked", "Exception Caught", "error", e)).execute();
		}
		
	}

	private void register(){
		
		try {
			loadingDialog.show();
			UpdateCustomerTask createUserTask = new UpdateCustomerTask(emailTextView.getText().toString(), passwordTextView.getText().toString(), getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						GuestCreateCustomer.this.loadingDialog.hide();
						
						int errorCode = getErrorCode();

						
						if (getFinalSuccess()){
							
							AppActions.add("Guest Create Customer - Register Successful");

							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
							
							String guestId = myPrefs.getString(Keys.GUEST_ID);

							myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, getNewCustomerToken());
							myPrefs.putAndCommitString(Keys.CUSTOMER_ID, guestId);
							myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, GuestCreateCustomer.this.emailTextView.getText().toString());
							
							
							myPrefs.putAndCommitString(Keys.GUEST_TOKEN, getNewCustomerToken());
							myPrefs.putAndCommitString(Keys.GUEST_ID, guestId);
							
							
							

							toastShort("Account created successfully!");
							
							Intent goReview = new Intent(getApplicationContext(), Review.class);
							goReview.putExtra(Constants.INVOICE, theBill);
							startActivity(goReview);
							
							
						}else{
							AppActions.add("Guest Create Customer - Register Failed - Error Code:" + errorCode);

							if (errorCode != 0){
								
								String errorMsg = "";
								
					            if(errorCode == 103) {
					                errorMsg = "The email address you entered is already being used.  If you already have an account, please sign in by going to Profile in the left menu";
					            }else if (errorCode == ErrorCodes.NETWORK_ERROR){
					                
					                errorMsg = "Arc is having problems connecting to the internet.  Please check your connection and try again.  Thank you!";
					                
					            }else {
					                errorMsg = "We encountered an error during the registration process, please try again.";
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("We encountered an error during the registration process, please try again.");

							}


						}
					} catch (Exception e) {
						(new CreateClientLogTask("GuestCreateCustomer.register.onPostExecute", "Exception Caught", "error", e)).execute();
					}
					
					
				}
			};
			createUserTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("GuestCreateCustomer.register", "Exception Caught", "error", e)).execute();
		}
		
		
	}
}
