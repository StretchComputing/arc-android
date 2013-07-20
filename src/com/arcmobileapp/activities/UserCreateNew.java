package com.arcmobileapp.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.Security;
import com.arcmobileapp.web.CreateUserTask;

public class UserCreateNew extends BaseActivity {

	private TextView emailTextView;
	private TextView passwordTextView;
	private ProgressDialog loadingDialog;
	private AlertDialog successDialog;
	private AlertDialog pinDialog;
	private Cards enteredCard;
	private String myPIN;
	private boolean didCancelScan;



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
		
		Logger.d("CREATING NEW USER WITH EMAIL: " + sendEmail + " AND PASSWORD: " + sendPassword);
		
		CreateUserTask createUserTask = new CreateUserTask(sendEmail, sendPassword, firstName, lastName, false, getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				UserCreateNew.this.loadingDialog.hide();
				
				if (getFinalSuccess()){
					ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
					
					myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, getDevToken());
					myPrefs.putAndCommitString(Keys.CUSTOMER_ID, getDevCustomerId());
					myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, UserCreateNew.this.emailTextView.getText().toString());
					
					
					showSuccessDialog();
					

					
					
				}else{
					toastShort("Registration error, please try again.");

				}
				
				
			}
		};
		createUserTask.execute();
	     
		
	}
	
	
	
	private void showSuccessDialog() {

		successDialog = null;

		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		input.setVisibility(View.GONE);
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("You will now be prompted to enter a form of payment.");

		TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
	
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
			remainingBalance.setTextColor(getResources().getColor(R.color.white));
		}

		remainingBalance.setText("Registration Successful!");
		AlertDialog.Builder builder = new AlertDialog.Builder(UserCreateNew.this);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
	
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		successDialog = builder.create();
		successDialog.setCancelable(false);
		successDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = successDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						
						
						//clicked
						
						showCardIo();
						successDialog.dismiss();
					}
				});
			}
		});
		successDialog.show();
	
	}
	
	public void showCardIo(){
		
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		// required for authentication with card.io
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
		startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
		
		
	}
	
	
	private void showPinDialog() {
		pinDialog = null;
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		
		
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("Please create a PIN");
		input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
		remainingBalance.setVisibility(View.GONE);
		
		
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
		}
		
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(UserCreateNew.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		//builder.setIcon(R.drawable.logo);
		builder.setCancelable(false);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		pinDialog = builder.create();
		
		pinDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		pinDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = pinDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						
						if (input.getText().toString().length() < 4){
							toastShort("PIN must be at least 4 digits");
						}else{
							myPIN = input.getText().toString();
							pinDialog.dismiss();
							UserCreateNew.this.refreshList();
						}
					
						
					}
				});
			}
		});
		pinDialog.show();
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String resultDisplayStr = "no response";

		if (requestCode == Constants.SCAN_REQUEST_CODE) {
			if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
				CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
				
				if(!scanResult.isExpiryValid()) {
					resultDisplayStr = "Your credit card is not valid (expired)";
					showInfoDialog(resultDisplayStr);
					return;
				}
				
				if(scanResult.getCardType() == CardType.INSUFFICIENT_DIGITS || scanResult.getCardType() == CardType.UNKNOWN || scanResult.getCardType() == CardType.JCB) {
					resultDisplayStr = "Your credit card is not valid (type unknown)";
					showInfoDialog(resultDisplayStr);
					return;
				}
				
				resultDisplayStr = "Card Number (formatted):\n"
						+ scanResult.getFormattedCardNumber() + "\n";

				resultDisplayStr += "Card Type: "
						+ scanResult.getCardType().name + "\n";

				
				
				// Do something with the raw number, e.g.:
				// myService.setCardNumber( scanResult.cardNumber );

				if (scanResult.isExpiryValid()) {
					resultDisplayStr += "Expiration Date: "
							+ scanResult.expiryMonth + "/"
							+ scanResult.expiryYear + "\n";
				}

				if (scanResult.cvv != null) {
					// Never log or display a CVV
					resultDisplayStr += "CVV has " + scanResult.cvv.length()
							+ " digits.\n";
				}

				if (scanResult.zip != null) {
					resultDisplayStr += "Zip: " + scanResult.zip + "\n";
				}
				
				saveTemp(scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), String.valueOf(scanResult.expiryYear), scanResult.zip, scanResult.cvv, String.valueOf(scanResult.getCardType().ordinal()), scanResult.getCardType().name());
				showPinDialog();
				
			} else {
				
				didCancelScan = true;
				resultDisplayStr = "\nScan canceled.  You may instead enter payment from the 'Funds' section on the Menu, or as you are about to make a payment.\n";
				showInfoDialog(resultDisplayStr);
				
				
				
				
				return;
			}
		}

		//showSuccessMessage(resultDisplayStr);
		//toastLong(resultDisplayStr);
//		showInfoDialog(resultDisplayStr);
		// else handle other activity results
	}
	
	protected void saveTemp(String number, String month, String year, String zip, String cvv, String typeId, String typeLabel) {
		
		enteredCard = new Cards(number, month, year, zip, cvv, "****" + number.substring(number.length() - 4), typeLabel, null);

	}
	
	
	protected void saveCard() {
		
		
		//ArrayList<Cards> cards = DBController.getCards(getContentProvider());
		
		/*
		for(Cards card: cards) {
			if(card.getNumber().equalsIgnoreCase(newCard.getNumber())) {
				toastLong("You've already saved this credit card. You can edit or delete the card if you'd like to make a change");
				return;
			}
		}*/
		
		DBController.saveCreditCard(getContentProvider(), enteredCard);
		//showInfoDialog(DBController.getCardCount(getContentProvider()) + "Card added");
	}
	
	private void showInfoDialog(String display) {
		AlertDialog.Builder builder = new AlertDialog.Builder(UserCreateNew.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setMessage(display);
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// hideSuccessMessage();
				
				if (didCancelScan){
					didCancelScan = false;
					Intent goBackProfile = new Intent(getApplicationContext(), Home.class);
					goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(goBackProfile);
				}
				
				
				
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// hideSuccessMessage();
			}
		});
		builder.create().show();
	}
	
	
	public void refreshList(){
		
		//encrypt it
		enteredCard.setNumber(encryptCardNumber(enteredCard.getNumber()));
		//save it
		saveCard();
		
		
		
		toastShort("Thank you for registering!");
		Intent goBackProfile = new Intent(getApplicationContext(), Home.class);
		goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goBackProfile);
		
		
		//refresh list
	}
	
	public String encryptCardNumber(String cardNumber){	
		
		try{
			Security s = new Security();
			
	        //String encrypted = s.encrypt(myPIN, cardNumber);
	        String encrypted = s.encryptBlowfish(cardNumber, myPIN);

	        Logger.d("Encrypted: " + encrypted);
	        
	        return encrypted;
		}catch (Exception e){
			
	        Logger.d("EXCEPTION: " + e);

			return "";
		}
                
	}

}
