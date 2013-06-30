package com.arcmobileapp.activities;

import io.card.payment.CardIOActivity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.arcmobileapp.utils.Constants;
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

    private EditText myPinText;
   
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_payment);
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
		selectedCard =  (Cards) getIntent().getSerializableExtra(Constants.SELECTED_CARD);

		
		myTotalPayment = (TextView) findViewById(R.id.my_total_payment);
		myPaymentUsed = (TextView) findViewById(R.id.my_payment_used);
		myPinText = (EditText) findViewById(R.id.confirm_pin_text);

		
		loadingDialog = new ProgressDialog(ConfirmPayment.this);
		loadingDialog.setTitle("Making Payment");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		

		setLabels();


	}
	
	
	private void setLabels(){
		
		myTotalPayment.setText(String.format("$%.2f", theBill.getMyBasePayment() + theBill.getMyTip()));
		myPaymentUsed.setText(selectedCard.getCardId());
	}
	
	public void onMakePaymentClicked(View view) {
		
		String cardNumber = "";
		try{
			cardNumber = decryptCreditCardNumber(selectedCard.getNumber());

		}catch(Exception e){
			
		}
		
		if (cardNumber.length() > 0){
			
			makePayment();
		}else{
			toastShort("Invali PIN, please try again");
		}
		
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	private String decryptCreditCardNumber(String encryptedNumber){
		
		
		Security s = new Security();
        String decrypted = s.decrypt(myPinText.getText().toString(), encryptedNumber);
        
		

        return decrypted;
	}
	
	
	private void makePayment(){
		
		
		loadingDialog.show();
		String token = getString(Keys.DEV_TOKEN);
		String customerId = getString(Keys.DEV_CUSTOMER_ID);

		String account = selectedCard.getNumber().replace(" ", "");
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
		

	
		CreatePayment newPayment = new CreatePayment(theBill.getMerchantId(), customerId, String.valueOf(theBill.getId()), theBill.getBaseAmount() + theBill.getTaxAmount(), theBill.getMyBasePayment(), theBill.getMyTip(), account, type, cardType, expiration, pin, null, splitType, null, null, null);

		MakePaymentTask task = new MakePaymentTask(token, newPayment, getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				

				loadingDialog.hide();
				if (getFinalSuccess()) {

	
					theBill.setPaymentId(getPaymentId());
					Intent goReview = new Intent(getApplicationContext(), Review.class);
					goReview.putExtra(Constants.INVOICE, theBill);
					startActivity(goReview);
					
					
					toastShort("Your payment has been processed successfully!");
				} else {
					toastShort("Payment Failed, please try again.");

				}
			}
		};
		
		task.execute();
		
	}

}
