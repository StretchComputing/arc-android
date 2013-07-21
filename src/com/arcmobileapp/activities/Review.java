package com.arcmobileapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.CreateReview;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.SubmitReviewTask;
//import android.view.Menu;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;



public class Review extends BaseActivity {

	
	private EditText textAdditionalComments;
	private RatingBar starRating;
	private Check theBill;
	private ProgressDialog loadingDialog;

	@Override
	public void onBackPressed() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_review);

			theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);

			
			textAdditionalComments = (EditText) findViewById(R.id.text_additional_comments);
			textAdditionalComments.setHint(R.string.review_hint);
			
			starRating = (RatingBar) findViewById(R.id.star_rating);

			
			loadingDialog = new ProgressDialog(Review.this);
			loadingDialog.setTitle("Sending Review");
			loadingDialog.setMessage("Please Wait...");
			loadingDialog.setCancelable(false);
		} catch (Exception e) {
			(new CreateClientLogTask("Review.onCreate", "Exception Caught", "error", e)).execute();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
    public void onSkipClicked(View view) {
		
	
    	try {
			Intent goBackHome = new Intent(getApplicationContext(), Home.class);
			goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(goBackHome);
		} catch (Exception e) {
			(new CreateClientLogTask("Review.onSkipClicked", "Exception Caught", "error", e)).execute();

		}
		
		
	}
    
    
    public void onSubmitClicked(View view) {
		
	
		try {
			if (textAdditionalComments.getText().toString().length() == 0){
				textAdditionalComments.setText("");
			}
			
			String customerId = getId();
			String token = getToken();
			double numStars = (double) starRating.getNumStars();
			
			
			loadingDialog.show();
			CreateReview newReview = new CreateReview(String.valueOf(theBill.getId()), String.valueOf(theBill.getPaymentId()),
					numStars, customerId, textAdditionalComments.getText().toString());
			
			
				
			
			SubmitReviewTask task = new SubmitReviewTask(token, newReview, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						

						loadingDialog.hide();
						if (getFinalSuccess()) {

							toastShort("Thank you for your review!");

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
		} catch (Exception e) {
			(new CreateClientLogTask("Review.onSubmitClicked", "Exception Caught", "error", e)).execute();

		}
			
			
		
	}






}
