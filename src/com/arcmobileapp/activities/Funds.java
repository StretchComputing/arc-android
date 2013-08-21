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
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.Security;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class Funds extends BaseActivity {

	private LinearLayout theView;
	private LinearLayout storedCardsView;
	private TextView addCardSuccess;
	private TextView addCardSuccessMsg;
	private TextView addCardSuccessLock;
	private String myPIN;
	private AlertDialog pinDialog;
	private Cards enteredCard;
	private Cards cardToName;

	private TextView titleText;
	private Button addButton;
	
	public Funds() {
		super();
	}

	public Funds(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("Funds - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.funds);
			theView = (LinearLayout) findViewById(R.id.funds_layout);
			theView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.login_fade_in));
			storedCardsView = (LinearLayout) findViewById(R.id.stored_cards_layout);
			
			titleText = (TextView)findViewById(R.id.home_title);
			titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			addButton = (Button)findViewById(R.id.add_card_button);
			addButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			initStoredCards();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Funds.onCreate", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void initStoredCards() {
		try {
			Logger.d("PRINT CARD INFO");
			storedCardsView.removeAllViews();  //clear any views in this object
			ArrayList<Cards> cards = DBController.getCards(getContentProvider());
		
			AppActions.add("Funds - initStoredCards - Number of Cards:" + cards.size());

			for(Cards card:cards) {
				RelativeLayout addMe = createCardLayout(card);
				storedCardsView.addView(addMe);
				storedCardsView.addView(createSpace());
				Logger.d(card.getCardName() + " | " + card.getNumber() + " | "  + card.getExpirationMonth()  + " | " + card.getExpirationYear() + " | " + card.getCardId()  + " | " + card.getCardLabel());
						
			}
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.initStoredCards", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	public LinearLayout createSpace() {
		LinearLayout space = new LinearLayout(getApplicationContext());
		space.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 20));
		return space;
	}
	
	public RelativeLayout createCardLayout(final Cards card) {
		try {
			LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			RelativeLayout rLayout = (RelativeLayout) inflater.inflate(R.layout.card_item, null);
			
			
			
			
			TextView tvCardType = (TextView) rLayout.findViewById(R.id.cardType);
			tvCardType.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			if (card.getCardName() != null && card.getCardName().length() > 0){
				tvCardType.setText(card.getCardName() + " (" + card.getCardLabel() + ")");
			}else{
				tvCardType.setText(card.getCardLabel());

			}
			
			TextView tvCardNumber = (TextView) rLayout.findViewById(R.id.cardNumber);
			tvCardNumber.setText(card.getCardId());
			tvCardNumber.setTypeface(ArcMobileApp.getLatoLightTypeface());

			String expiration = card.getExpirationMonth() + "/" + card.getExpirationYear();
			TextView tvExpiration = (TextView) rLayout.findViewById(R.id.expiration);
			tvExpiration.setText(expiration);
			tvExpiration.setTypeface(ArcMobileApp.getLatoLightTypeface());

			rLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					cardClicked(card);
					
				}
			});
			
			return rLayout;
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.createCardLayout", "Exception Caught", "error", e)).execute();
			return null;

		}
	}
	
	private void cardClicked(final Cards card) {
		try {
			
			AppActions.add("Funds - Card Clicked");

			
			//toastLong("clicked " + card.getNumber());
			AlertDialog.Builder builder = new AlertDialog.Builder(Funds.this);
			builder.setTitle("Edit Card?");
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("Name Card",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							cardToName = card;
							showNameDialog();
						}
					}).setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							

						}
					}).setNeutralButton("Delete Card",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							AppActions.add("Funds - Card Deleted");

							 try {
								DBController.deleteCard(getContentProvider(), card.getId());
								 initStoredCards();  //refresh that view
							} catch (Exception e) {
								(new CreateClientLogTask("Funds.cardClicked.onClick", "Exception Caught", "error", e)).execute();

							}
							 
						}
					});
			builder.create().show();
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.cardClicked", "Exception Caught", "error", e)).execute();

		}
	}




	
	private void hideSuccessMessage() {
		
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
		try {
			
			AppActions.add("Funds - Add Card Clicked");

			hideSuccessMessage();
			Intent scanIntent = new Intent(this, CardIOActivity.class);
			// required for authentication with card.io
			scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
			startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.onAddCardClick", "Exception Caught", "error", e)).execute();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			
			AppActions.add("Funds - CardIO Scan Complete");

			
			super.onActivityResult(requestCode, resultCode, data);

			String resultDisplayStr = "no response";

			if (requestCode == Constants.SCAN_REQUEST_CODE) {
				if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
					
					if(!scanResult.isExpiryValid()) {
						AppActions.add("Funds - CardIO Scan Expiry Not Valid");

						resultDisplayStr = "Your credit card is not valid (expired)";
						showInfoDialog(resultDisplayStr);
						return;
					}
					
					if(scanResult.getCardType() == CardType.INSUFFICIENT_DIGITS || scanResult.getCardType() == CardType.UNKNOWN || scanResult.getCardType() == CardType.JCB) {
						
						AppActions.add("Funds - CardIO Scan Credit Card Not Valid");

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
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.onActivityResults", "Exception Caught", "error", e)).execute();

		}
	}
	
	protected void saveTemp(String number, String month, String year, String zip, String cvv, String typeId, String typeLabel) {
		
		try {
			enteredCard = new Cards(number, month, year, zip, cvv, "****" + number.substring(number.length() - 4), typeLabel, null);
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.saveTemp", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	protected void saveCard() {
		
		
		
		
		try {
			
			AppActions.add("Funds - New Card Added");

			DBController.saveCreditCard(getContentProvider(), enteredCard);
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.saveCard", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void showInfoDialog(String display) {
		try {
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
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public String encryptCardNumber(String cardNumber){	
	
		try{
			Security s = new Security();
			
	        //String encrypted = s.encrypt(myPIN, cardNumber);
	        String encrypted = s.encryptBlowfish(cardNumber, myPIN);	        
	        return encrypted;
		}catch (Exception e){
			
			(new CreateClientLogTask("Funds.encryptCardNumber", "Exception Caught", "error", e)).execute();

			return "";
		}
                
	}
	
	
	private void showPinDialog() {
		try {
			
			AppActions.add("Funds -Show PIN Dialog");

			pinDialog = null;
			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.pin_dialog, null);
			final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);			
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("Your PIN will be used to securely encrypt your card.");
			input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

			input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
			//remainingBalance.setVisibility(View.GONE);
			remainingBalance.setText("Please create a PIN");
			
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
				remainingBalance.setTextColor(getResources().getColor(R.color.white));

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
							
							try {
								if (input.getText().toString().length() < 4){
									toastShort("PIN must be at least 4 digits");
								}else{
									myPIN = input.getText().toString();
									pinDialog.dismiss();
									Funds.this.refreshList();
								}
							} catch (Exception e) {
								(new CreateClientLogTask("Funds.showPinDialog.onClick", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Funds.showPinDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	public void refreshList(){
		
		try {
			//encrypt it
			enteredCard.setNumber(encryptCardNumber(enteredCard.getNumber()));
			//save it
			saveCard();
			
			//refresh list
			initStoredCards();
		} catch (Exception e) {
			(new CreateClientLogTask("Funds.refreshList", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private void showNameDialog() {
		try {
			
			AppActions.add("Funds -Show PIN Dialog");

			pinDialog = null;
			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
			final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);			
			
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("Enter a name for this card.");
			input.setGravity(Gravity.CENTER | Gravity.BOTTOM);
			input.setInputType(InputType.TYPE_CLASS_TEXT);

			//input.setFilters(new InputFilter[] { new CurrencyFilter(), new InputFilter.LengthFilter(6) });
			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
			remainingBalance.setVisibility(View.GONE);
			//remainingBalance.setText("Please create a PIN");
			
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
				remainingBalance.setTextColor(getResources().getColor(R.color.white));

			}
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(Funds.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setView(makePaymentView);
			//builder.setIcon(R.drawable.logo);
			builder.setCancelable(true);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

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
							
							try {
								if (input.getText().toString().length() < 2){
									toastShort("Name must be at least 2 digits");
								}else{
									String cardName = input.getText().toString();
									
									DBController.updateCardName(getContentProvider(), cardToName.getId(), cardName);
									pinDialog.dismiss();
									initStoredCards();
								}
							} catch (Exception e) {
								(new CreateClientLogTask("Funds.showNameDialog.onClick", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
					
					
					
					Button c = pinDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
					c.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							pinDialog.dismiss();
						
							
						}
					});
					
					
					
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Funds.showNameDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
	
}
