package com.arcmobileapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.CreateReview;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.web.SubmitReviewTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
//import android.view.Menu;



public class Review extends BaseActivity {

	
	private EditText textAdditionalComments;
	private RatingBar starRating;
	private Check theBill;
	private ProgressDialog loadingDialog;
	
	private TextView topTextView;
	private TextView clickTextView;

	private Button submitButton;
	private boolean isSubmittingReview = false;
	@Override
	public void onBackPressed() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("Review - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_review);

			theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
			
			textAdditionalComments = (EditText) findViewById(R.id.text_additional_comments);
			textAdditionalComments.setHint(R.string.review_hint);
			textAdditionalComments.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			starRating = (RatingBar) findViewById(R.id.star_rating);

			topTextView = (TextView) findViewById(R.id.text_enter_pin);
			topTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			clickTextView = (TextView) findViewById(R.id.current_merchant);
			clickTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());

			
			submitButton = (Button) findViewById(R.id.button_email);
			submitButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());

		
		} catch (Exception e) {
			(new CreateClientLogTask("Review.onCreate", "Exception Caught", "error", e)).execute();

		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadingDialog = new ProgressDialog(Review.this);
		loadingDialog.setTitle("Sending Review");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.review_menu, menu);
		return true;
	}
	
	
    public void onSkipClicked(View view) {
		
	
    	try {
			loadingDialog.dismiss();

			Intent goBackHome = new Intent(getApplicationContext(), Home.class);
			goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(goBackHome);
		} catch (Exception e) {
			(new CreateClientLogTask("Review.onSkipClicked", "Exception Caught", "error", e)).execute();

		}
		
		
	}
    
    
    public void onSubmitClicked(View view) {
		
	
		try {
			
			
			if (!isSubmittingReview){
				isSubmittingReview = true;
				
				
				if (textAdditionalComments.getText().toString().length() == 0){
					textAdditionalComments.setText("");
				}
				
				String customerId = getId();
				String token = getToken();
				double numStars = (double) starRating.getNumStars();
				
				
				AppActions.add("Review - Submitting Review - Num Stars:" + numStars + ", Comments:" + textAdditionalComments.getText().toString());

				
				loadingDialog.show();
				CreateReview newReview = new CreateReview(String.valueOf(theBill.getId()), String.valueOf(theBill.getPaymentId()),
						numStars, customerId, textAdditionalComments.getText().toString());
				
				
					
				
				SubmitReviewTask task = new SubmitReviewTask(token, newReview, getApplicationContext()) {
					@Override
					protected void onPostExecute(Void result) {
						try {
							super.onPostExecute(result);
							
							isSubmittingReview = false;

							loadingDialog.hide();
							if (getFinalSuccess()) {

								toastShort("Thank you for your review!");
								loadingDialog.dismiss();

								Intent goBackHome = new Intent(getApplicationContext(), Home.class);
								goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(goBackHome);
								
								
							} else {
								toastShort("Review Failed, please try again.");

							}
						} catch (Exception e) {
							(new CreateClientLogTask("Review.onSubmitClicked.onPostExecute", "Exception Caught", "error", e)).execute();

						}
					}
				};
				
				task.execute();
				
				
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("Review.onSubmitClicked", "Exception Caught", "error", e)).execute();

		}
			
			
		
	}


    @Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.skipReview:
			toastShort("Thank you for your purchase!");
			loadingDialog.dismiss();

			Intent goBackHome = new Intent(getApplicationContext(), Home.class);
			goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(goBackHome);
			break;
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	




}
