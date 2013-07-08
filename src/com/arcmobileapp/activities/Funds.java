package com.arcmobileapp.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Enums.ModernPicTypes;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.Security;
import com.arcmobileapp.utils.Utils;

public class Funds extends BaseActivity {

	private LinearLayout theView;
	private LinearLayout storedCardsView;
	private TextView addCardSuccess;
	private TextView addCardSuccessMsg;
	private TextView addCardSuccessLock;
	private String myPIN;
	private AlertDialog pinDialog;
	private Cards enteredCard;

	public Funds() {
		super();
	}

	public Funds(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.funds);
		theView = (LinearLayout) findViewById(R.id.funds_layout);
		theView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.login_fade_in));
		storedCardsView = (LinearLayout) findViewById(R.id.stored_cards_layout);
		addCardSuccess = (TextView) findViewById(R.id.add_card_success);
		addCardSuccess.setText(Utils.convertModernPicType(ModernPicTypes.MoneyBag));
		addCardSuccess.setTextSize(220);
		addCardSuccess.setTypeface(ArcMobileApp.getModernPicsTypeface());
		addCardSuccessMsg = (TextView) findViewById(R.id.add_card_success_msg);
		addCardSuccessLock = (TextView) findViewById(R.id.add_card_success_lock);
		addCardSuccessLock.setText(Utils.convertModernPicType(ModernPicTypes.Unlock) + " " + Utils.convertModernPicType(ModernPicTypes.Lock));
		addCardSuccessLock.setTextSize(100);
		addCardSuccessLock.setTypeface(ArcMobileApp.getModernPicsTypeface());
		addCardSuccessLock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lockCard();
			}
		});
		hideSuccessMessage();
		initStoredCards();
	}
	
	private void initStoredCards() {
		Logger.d("PRINT CARD INFO");
		storedCardsView.removeAllViews();  //clear any views in this object
		ArrayList<Cards> cards = DBController.getCards(getContentProvider());
		
		for(Cards card:cards) {
			LinearLayout addMe = createCardLayout(card);
			storedCardsView.addView(addMe);
			storedCardsView.addView(createSpace());
			//Logger.d(card.getNumber() + " | "  + card.getExpirationMonth()  + " | " + card.getExpirationYear() + " | " + card.getCardId()  + " | " + card.getCardLabel());
		}
		
	}
	
	public LinearLayout createSpace() {
		LinearLayout space = new LinearLayout(getApplicationContext());
		space.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 20));
		return space;
	}
	
	public LinearLayout createCardLayout(final Cards card) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		LinearLayout rLayout = (LinearLayout) inflater.inflate(R.layout.card_item, null);
		
		
		
		
		TextView tvCardType = (TextView) rLayout.findViewById(R.id.cardType);
		tvCardType.setText(card.getCardLabel());
		
		TextView tvCardNumber = (TextView) rLayout.findViewById(R.id.cardNumber);
		tvCardNumber.setText(card.getCardId());
		
		String expiration = card.getExpirationMonth() + "/" + card.getExpirationYear();
		TextView tvExpiration = (TextView) rLayout.findViewById(R.id.expiration);
		tvExpiration.setText(expiration);
		
		rLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				cardClicked(card);
				
			}
		});
		
		return rLayout;
	}
	
	private void cardClicked(final Cards card) {
		//toastLong("clicked " + card.getNumber());
		AlertDialog.Builder builder = new AlertDialog.Builder(Funds.this);
		builder.setTitle("Delete card?");
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						 DBController.deleteCard(getContentProvider(), card.getId());
						 initStoredCards();  //refresh that view
					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}



	private void lockCard() {
		String display = "\nLock Card (PIN)\n";
		showInfoDialog(display);
	}
	
	private void hideSuccessMessage() {
		addCardSuccess.setVisibility(View.GONE);
		addCardSuccessMsg.setVisibility(View.GONE);
		addCardSuccessLock.setVisibility(View.GONE);
	}
	
	private void showSuccessMessage(String message) {
		addCardSuccess.setVisibility(View.VISIBLE);
		addCardSuccessMsg.setText(message);
		addCardSuccessMsg.setVisibility(View.VISIBLE);
		addCardSuccessLock.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	protected void clickCarousel(int pos){
		toastShort("Clicked " + pos);
	}
	
	public void onAddCardClick(View v) {
		hideSuccessMessage();
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
				
				saveTemp(scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), String.valueOf(scanResult.expiryYear), scanResult.zip, scanResult.cvv, String.valueOf(scanResult.getCardType().ordinal()), scanResult.getCardType().name());
				showPinDialog();
				
			} else {
				resultDisplayStr = "\nScan was canceled.\n";
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
		AlertDialog.Builder builder = new AlertDialog.Builder(Funds.this);
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
		
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(Funds.this);
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
							Funds.this.refreshList();
						}
					
						
					}
				});
			}
		});
		pinDialog.show();
		
	}
	
	public void refreshList(){
		
		//encrypt it
		enteredCard.setNumber(encryptCardNumber(enteredCard.getNumber()));
		//save it
		saveCard();
		
		//refresh list
		initStoredCards();
	}
	
}
