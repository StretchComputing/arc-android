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
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
//import android.view.Menu;
/*
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
*/



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
    private boolean justAddedCard;
    private TextView totalLabel;
    private TextView tipLabel;
    private Button continueButton;
    private TextView dollarSign;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("Additional Tip - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_additional_tip);
			
			
			
			theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
			
			textTotalPayment = (TextView) findViewById(R.id.text_total_payment);
			textTotalPayment.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			myTipText = (EditText) findViewById(R.id.my_tip_text);
			myTipText.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			
			dollarSign = (TextView) findViewById(R.id.amountText);
			dollarSign.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			
			radioEightteen = (RadioButton) findViewById(R.id.radio_eightteen);
			radioEightteen.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			radioTwenty = (RadioButton) findViewById(R.id.radio_twenty);
			radioTwenty.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			radioTwentyTwo = (RadioButton) findViewById(R.id.radio_twenty_two);
			radioTwentyTwo.setTypeface(ArcMobileApp.getLatoBoldTypeface());


			radiogroup1 = (RadioGroup) findViewById(R.id.tip_radio_group);
			
			totalLabel = (TextView)findViewById(R.id.text_enter_pin);
			totalLabel.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			tipLabel = (TextView)findViewById(R.id.current_merchant);
			tipLabel.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			continueButton = (Button)findViewById(R.id.button_email);
			continueButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			textTotalPayment.setText(String.format("$%.2f", theBill.getMyBasePayment()));
			
			if (theBill.getServiceCharge() == 0.0){
				
				Logger.d("Selecting TWENTY");
				radioTwenty.setChecked(true);
				
				myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .20));
			}
			myTipText.setFilters(new InputFilter[] { new CurrencyFilter() });

			myTipText.addTextChangedListener(new TextWatcher()
			{
			 

			    @Override
			    public void onTextChanged( CharSequence s, int start, int before, int count)
			    {
			    	
			    	try {
						if (AdditionalTip.this.didChooseRadio){
							AdditionalTip.this.didChooseRadio = false;
						}else{
							AdditionalTip.this.radioEightteen.setChecked(false);
							AdditionalTip.this.radioTwenty.setChecked(false);
							AdditionalTip.this.radioTwentyTwo.setChecked(false);
						}
					} catch (Exception e) {
						(new CreateClientLogTask("AdditionalTip.onCreate.onTtextChanged", "Exception Caught", "error", e)).execute();

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
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.onCreate", "Exception Caught", "error", e)).execute();

		}


	}

	public void onRadioButtonClicked(View view) {
	    try {
			// Is the button now checked?
			boolean checked = ((RadioButton) view).isChecked();

			didChooseRadio = true;
			// Check which radio button was clicked
			switch(view.getId()) {
			    case R.id.radio_eightteen:
			        if (checked)
						AppActions.add("Additional Tip - 18% selected");

						myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .18));
			        break;
			    case R.id.radio_twenty:
			        if (checked)
						AppActions.add("Additional Tip - 20% selected");

						myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .20));
			        break;
			    case R.id.radio_twenty_two:
			        if (checked)
						AppActions.add("Additional Tip - 22% selected");

						myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .22));
			        break;
			}
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.onRadioButtonClicked", "Exception Caught", "error", e)).execute();

		}
	}

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onContinueButtonClicked(View view) {
	    try {
			// Is the button now checked?
   
			//set MyTip()
			

			if (myTipText.getText().toString() != null){
				
				
				if (myTipText.getText().toString().equals(".")){
					toastShort("Please enter a tip first");
					return;
				}
				
				Double myTip = 0.0;
				
				if (myTipText.getText().toString().length() > 0){
					myTip = Double.parseDouble(myTipText.getText().toString());
				}
				
				AppActions.add("Additional Tip - Continue selected - Additional Tip: " + myTip + ", Total Payment: " + theBill.getMyBasePayment());

				theBill.setMyTip(myTip);
				
				//Get payment info
				 cards = DBController.getCards(getContentProvider());

				if (cards.size() > 0){

					if (cards.size() == 1){
						//Go straight to Payment screen with this card
						AppActions.add("Additional Tip - Continue selected - Number of stored cards:1");
						selectedCard = cards.get(0);
						goConfirmPayment();
						
					}else{
						showAlertDialog();

					}
					
				}else{
					AppActions.add("Additional Tip - Continue selected - Number of stored cards:" + cards.size());

					   showCardIo();

				}
				
				
			}else{
				toastShort("Invalid Tip Amount");
			}
			
		} catch (NumberFormatException e) {
			(new CreateClientLogTask("AdditionalTip.onContinueButtonClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	public void showCardIo(){
		
		try {
			
			AppActions.add("Additional Tip - Adding Card");

			Intent scanIntent = new Intent(this, CardIOActivity.class);
			// required for authentication with card.io
			scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
			startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.showCardIO", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			AppActions.add("Additional Tip - CardIO Scan Complete");

			String resultDisplayStr = "no response";

			if (requestCode == Constants.SCAN_REQUEST_CODE) {
				if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
					
					if(!scanResult.isExpiryValid()) {
						AppActions.add("Additional Tip - CardIO Scan Expiry Not Valid");

						resultDisplayStr = "Your credit card is not valid (expired)";
						showInfoDialog(resultDisplayStr);
						return;
					}
					
					if(scanResult.getCardType() == CardType.INSUFFICIENT_DIGITS || scanResult.getCardType() == CardType.UNKNOWN || scanResult.getCardType() == CardType.JCB) {
						AppActions.add("Additional Tip - CardIO Scan Credit Card Not Valid");

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
					
					toastShort("Scan Successful");
					
					Cards newCard = new Cards( scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), String.valueOf(scanResult.expiryYear), scanResult.zip, scanResult.cvv, "****" + scanResult.getFormattedCardNumber().substring(scanResult.getFormattedCardNumber().length() - 4), scanResult.getCardType().name(), null);
					selectedCard = newCard;
					justAddedCard = true;
					goConfirmPayment();
					
				} else {
					resultDisplayStr = "\nScan was canceled.\n";
					showInfoDialog(resultDisplayStr);
					return;
				}
			}

			//showSuccessMessage(resultDisplayStr);
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.onActivityResult", "Exception Caught", "error", e)).execute();

		}
	
	}
	
	
	
	
	private void showInfoDialog(String display) {
		try {
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
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private void showAlertDialog(){
		
		  
		  try {
			List<String> listItems = new ArrayList<String>();

			  
			  for (int i = 0; i < cards.size(); i++){
				  Cards currentCard = cards.get(i);
				  
				  
				  if (currentCard.getCardName() != null && currentCard.getCardName().length() > 0){
			
						 listItems.add(currentCard.getCardName() +  " (" + currentCard.getCardLabel() + ") " +currentCard.getCardId());

					}else{
						  listItems.add(currentCard.getCardLabel() + currentCard.getCardId());

					}
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
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.showAlertDialog", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	private void goConfirmPayment(){
		
		try {
			Intent confirmPayment = new Intent(getApplicationContext(), ConfirmPayment.class);
			confirmPayment.putExtra(Constants.SELECTED_CARD, selectedCard);
			confirmPayment.putExtra(Constants.INVOICE, theBill);
			confirmPayment.putExtra(Constants.JUST_ADD_CARD, justAddedCard);

			startActivity(confirmPayment);
		} catch (Exception e) {
			(new CreateClientLogTask("AdditionalTip.goConfirmPayment", "Exception Caught", "error", e)).execute();

		}
	}
	

}
