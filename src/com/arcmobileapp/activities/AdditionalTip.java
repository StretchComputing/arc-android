package com.arcmobileapp.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
//import android.view.Menu;
/*
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
*/
import com.arcmobileapp.utils.Security;



public class AdditionalTip extends BaseActivity {

	
	private Check theBill;
    private Cards selectedCard;

	private TextView textTotalPayment;
	
	private EditText myTipText;
	
	private RadioButton radioEightteen;
	private RadioButton radioTwenty;
	private RadioButton radioTwentyTwo;
    private RadioGroup radiogroup1;
    private boolean didChooseRadio;
    private ArrayList<Cards> cards;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_additional_tip);
		
		
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
		
		textTotalPayment = (TextView) findViewById(R.id.text_total_payment);
		myTipText = (EditText) findViewById(R.id.my_tip_text);

		radioEightteen = (RadioButton) findViewById(R.id.radio_eightteen);
		radioTwenty = (RadioButton) findViewById(R.id.radio_twenty);
		radioTwentyTwo = (RadioButton) findViewById(R.id.radio_twenty_two);


        radiogroup1 = (RadioGroup) findViewById(R.id.tip_radio_group);
		
		
		
		textTotalPayment.setText(String.format("$%.2f", theBill.getMyBasePayment()));
		
		if (theBill.getServiceCharge() == 0.0){
			
			Logger.d("Selecting TWENTY");
			radioTwenty.setChecked(true);
			
			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .20));
		}
		
		myTipText.addTextChangedListener(new TextWatcher()
		{
		 

		    @Override
		    public void onTextChanged( CharSequence s, int start, int before, int count)
		    {
		    	
		    	if (AdditionalTip.this.didChooseRadio){
		    		AdditionalTip.this.didChooseRadio = false;
		    	}else{
		    		AdditionalTip.this.radioEightteen.setChecked(false);
			    	AdditionalTip.this.radioTwenty.setChecked(false);
			    	AdditionalTip.this.radioTwentyTwo.setChecked(false);
		    	}
		    	

		    }

		    @Override
		    public void beforeTextChanged( CharSequence s, int start, int count, int after)
		    {}

		    @Override
		    public void afterTextChanged( final Editable s)
		    {
		    	


		    }
		});


	}

	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();

    	didChooseRadio = true;
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_eightteen:
	            if (checked)
	    			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .18));
	            break;
	        case R.id.radio_twenty:
	            if (checked)
	    			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .20));
	            break;
	        case R.id.radio_twenty_two:
	            if (checked)
	    			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .22));
	            break;
	    }
	}

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onContinueButtonClicked(View view) {
	    // Is the button now checked?
	   
		//set MyTip()
		
	
		theBill.setMyTip(Double.parseDouble(myTipText.getText().toString()));
		
		//Get payment info
		 cards = DBController.getCards(getContentProvider());

		if (cards.size() > 0){

			if (cards.size() == 1){
				//Go straight to Payment screen with this card
				selectedCard = cards.get(0);
				goConfirmPayment();
				
			}else{
				showAlertDialog();

			}
			
		}else{
			   showCardIo();

		}
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
				
				toastShort("Scan Successful: " + scanResult.getFormattedCardNumber());
				
				Cards newCard = new Cards( scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), String.valueOf(scanResult.expiryYear), scanResult.zip, scanResult.cvv, "****" + scanResult.getFormattedCardNumber().substring(scanResult.getFormattedCardNumber().length() - 4), scanResult.getCardType().name(), null);
				selectedCard = newCard;
				goConfirmPayment();
				
			} else {
				resultDisplayStr = "\nScan was canceled.\n";
				showInfoDialog(resultDisplayStr);
				return;
			}
		}

		//showSuccessMessage(resultDisplayStr);
	
	}
	
	
	
	
	private void showInfoDialog(String display) {
		AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalTip.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setMessage(display);
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//hideSuccessMessage();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//hideSuccessMessage();
			}
		});
		builder.create().show();
	}
	
	
	private void showAlertDialog(){
		
		  
		  List<String> listItems = new ArrayList<String>();

		  
		  for (int i = 0; i < cards.size(); i++){
			  Cards currentCard = cards.get(i);
			  
			  listItems.add(currentCard.getCardLabel() + currentCard.getCardId());
		  }

		  final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Select Payment:");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                // Do something with the selection
	                AdditionalTip.this.selectedCard = AdditionalTip.this.cards.get(item);
	                AdditionalTip.this.goConfirmPayment();
	                	
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();

	}
	
	
	private void goConfirmPayment(){
		
		Intent confirmPayment = new Intent(getApplicationContext(), ConfirmPayment.class);
		confirmPayment.putExtra(Constants.SELECTED_CARD, selectedCard);
		confirmPayment.putExtra(Constants.INVOICE, theBill);

		startActivity(confirmPayment);
	}
	

}
