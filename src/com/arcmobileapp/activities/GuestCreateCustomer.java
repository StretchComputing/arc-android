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
import com.arcmobileapp.web.UpdateCustomerTask;

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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest_create_customer);
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);

		emailTextView = (TextView) findViewById (R.id.guest_email_textv);
		passwordTextView = (TextView) findViewById (R.id.guest_password_textv);
		
		loadingDialog = new ProgressDialog(GuestCreateCustomer.this);
		loadingDialog.setTitle("Creating Account");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onCreateClicked(View view) {

		if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 ){
			//Send the login
			register();
		}else{
			toastShort("Please enter an email address and password.");
		}
		
		
	}
	
	
	public void onNoThanksClicked(View view) {

		
		Intent goReview = new Intent(getApplicationContext(), Review.class);
		goReview.putExtra(Constants.INVOICE, theBill);
		startActivity(goReview);
		
	}

	private void register(){
		
		loadingDialog.show();
		UpdateCustomerTask createUserTask = new UpdateCustomerTask(emailTextView.getText().toString(), passwordTextView.getText().toString(), getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				GuestCreateCustomer.this.loadingDialog.hide();
				
				if (getFinalSuccess()){
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
					toastShort("Registration error, please try again.");

				}
				
				
			}
		};
		createUserTask.execute();
		
		
	}
}
