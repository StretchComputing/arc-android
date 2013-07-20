package com.arcmobileapp.activities;

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
import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentFlags;
import com.arcmobileapp.utils.Security;
import com.arcmobileapp.web.MakePaymentTask;

public class ConfirmPayment extends BaseActivity {

	private Check theBill;
    private Cards selectedCard;
    
    private TextView myTotalPayment;
    private TextView myPaymentUsed;
	private ProgressDialog loadingDialog;
	private boolean justAddedCard;
    private EditText myPinText;
    private TextView textEnterPin;
    private String decryptedCC;
    private AlertDialog newUserDialog;
    private AlertDialog pinDialog;
    private int myPaymentId;
	private String myPIN;

   
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_payment);
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
		selectedCard =  (Cards) getIntent().getSerializableExtra(Constants.SELECTED_CARD);
		justAddedCard = getIntent().getBooleanExtra(Constants.JUST_ADD_CARD, false);
		
		myTotalPayment = (TextView) findViewById(R.id.my_total_payment);
		myPaymentUsed = (TextView) findViewById(R.id.my_payment_used);
		textEnterPin = (TextView) findViewById(R.id.text_enter_pin);

		myPinText = (EditText) findViewById(R.id.confirm_pin_text);

		
		loadingDialog = new ProgressDialog(ConfirmPayment.this);
		loadingDialog.setTitle("Making Payment");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		
		if (justAddedCard){
			textEnterPin.setVisibility(View.GONE);
			myPinText.setVisibility(View.GONE);
		}

		setLabels();


	}
	
	
	private void setLabels(){
		
		myTotalPayment.setText(String.format("$%.2f", theBill.getMyBasePayment() + theBill.getMyTip()));
		myPaymentUsed.setText(selectedCard.getCardId());
	}
	
	public void onMakePaymentClicked(View view) {
		
		String cardNumber = "";
		
		if (justAddedCard){
			
			decryptedCC = selectedCard.getNumber();
		}else{
			try{
				decryptedCC = decryptCreditCardNumber(selectedCard.getNumber());

			}catch(Exception e){
				
			}
		}
		
		
		if (decryptedCC.length() > 0){
			
			makePayment();
		}else{
			toastShort("Invalid PIN, please try again");
		}
		
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	private String decryptCreditCardNumber(String encryptedNumber){
		
		
		try{
			Security s = new Security();
	        //String decrypted = s.decrypt(myPinText.getText().toString(), encryptedNumber);
	        String decrypted = s.decryptBlowfish(encryptedNumber, myPinText.getText().toString());
			
	        Logger.d("DECRYPTED: " + decrypted);
	        if (decrypted == null){
	        	return "";
	        }
	        return decrypted;
	        
		}catch (Exception e){
			return "";
		}
	}
	
	
	private void makePayment(){
		
		
		loadingDialog.show();
		String token = getToken();
		String customerId = getId();

		String account = decryptedCC.replace(" ", "");
		String month = selectedCard.getExpirationMonth();
		if (month.length() == 1) {
			month = "0" + month;
		}
		String year = selectedCard.getExpirationYear().substring(2, 4);
		String expiration = month + "-" + year;
		String pin = selectedCard.getCVV();
		String type = PaymentFlags.PaymentType.CREDIT.toString();
		String cardType = PaymentFlags.CardType.V.toString();
		String splitType = PaymentFlags.SplitType.DOLLAR.toString();
		

	
		CreatePayment newPayment = new CreatePayment(theBill.getMerchantId(), customerId, String.valueOf(theBill.getId()), theBill.getBaseAmount() + theBill.getTaxAmount(), theBill.getMyBasePayment(), theBill.getMyTip(), account, type, cardType, expiration, pin, null, splitType, null, null, theBill.getMyItems());

		MakePaymentTask task = new MakePaymentTask(token, newPayment, getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				

				loadingDialog.hide();
				if (getFinalSuccess()) {

					toastShort("Your payment has been processed successfully!");

					
					ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
					
					String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);

					if(customerToken == null && customerToken == null){
						//Offer Registration Screen
						
						theBill.setPaymentId(getPaymentId());
						Intent goReview = new Intent(getApplicationContext(), GuestCreateCustomer.class);
						goReview.putExtra(Constants.INVOICE, theBill);
						startActivity(goReview);
						
					}else{
						
						if (justAddedCard){
							//Offer saving the card;
							
							myPaymentId = getPaymentId();
							showPinDialog();
						}else{
							
							
							theBill.setPaymentId(getPaymentId());
							Intent goReview = new Intent(getApplicationContext(), Review.class);
							goReview.putExtra(Constants.INVOICE, theBill);
							startActivity(goReview);
							
						}
						
						
					
					}
					
					
					
					
					
				} else {
					toastShort("Payment Failed, please try again.");

				}
			}
		};
		
		task.execute();
		
	}
	
	
	

	
	private void showPinDialog() {
		pinDialog = null;
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		
		
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("You must create a PIN so we can securely encrypt your card information.");
		input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
		remainingBalance.setText("Save your payment info?");
		//remainingBalance.setVisibility(View.GONE);
		
		
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
			remainingBalance.setTextColor(getResources().getColor(R.color.white));

		}
		
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPayment.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		//builder.setIcon(R.drawable.logo);
		builder.setCancelable(false);
		builder.setPositiveButton("Save Card", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		
		builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {

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
							ConfirmPayment.this.refreshList();
						}
					
						
					}
				});
				
				
				Button c = pinDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
				c.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						
						theBill.setPaymentId(myPaymentId);
						Intent goReview = new Intent(getApplicationContext(), Review.class);
						goReview.putExtra(Constants.INVOICE, theBill);
						startActivity(goReview);
						
					
						
					}
				});
				
				
				
				
				
			}
		});
		pinDialog.show();
		
	}
	
	
    public void refreshList(){
		
		//encrypt it
		selectedCard.setNumber(encryptCardNumber(selectedCard.getNumber()));
		//save it
		saveCard();
		
		//refresh list
		theBill.setPaymentId(myPaymentId);
		Intent goReview = new Intent(getApplicationContext(), Review.class);
		goReview.putExtra(Constants.INVOICE, theBill);
		startActivity(goReview);
		
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
    
     protected void saveCard() {
		
		DBController.saveCreditCard(getContentProvider(), selectedCard);
	}
    
}
