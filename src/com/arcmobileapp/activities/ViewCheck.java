package com.arcmobileapp.activities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.domain.LineItem;
import com.arcmobileapp.domain.Payments;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentFlags;
import com.arcmobileapp.web.MakePaymentTask;

public class ViewCheck extends BaseActivity {

	private TextView myTotalTextView;
	
	private RelativeLayout layoutBottom;
	
	private TextView textSubtotalName;
	private TextView textTaxName;
	private TextView textServiceChargeName;
	private TextView textDiscountName;
	private TextView textAmountDueName;
	private TextView textSubtotalValue;
	private TextView textTaxValue;
	private TextView textServiceChargeValue;
	private TextView textDiscountValue;
	private TextView textAmountDueValue;
	private TextView textAlreadyPaidName;
	private TextView textAlreadyPaidValue;
	private ListView list;
	private Double myPayment;
	private Button splitDollarButton;
	private Button splitPercentButton;
	private Double taxPercent;
	
	private int currentSelectedIndex;


	private ArrayAdapter<LineItem> adapter;

	private String merchantId;

	String paymentInfo;
	private AlertDialog payDialog;
	private Double totalBill;
	private Double myBill;
	private Double amountPaid;
	private String invoiceId;
	private Check theBill;
	DecimalFormat money = new DecimalFormat("$#.00");
	NumberFormat quantity = new DecimalFormat("#");
	ListView myListView;
	
	private Button payBillButton;

	public ViewCheck() {
		super();
	}

	public ViewCheck(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		totalBill = 0d;
		amountPaid = 0d;
		setContentView(R.layout.view_check);
	
		
		myTotalTextView = (TextView) findViewById(R.id.myTotalTextView);
		
		
		textSubtotalName = (TextView) findViewById(R.id.text_subtotal_name);
		textTaxName = (TextView) findViewById(R.id.text_tax_name);
		textServiceChargeName = (TextView) findViewById(R.id.text_service_charge_name);
		textDiscountName = (TextView) findViewById(R.id.text_discount_name);
		textAmountDueName = (TextView) findViewById(R.id.text_amount_due_name);
		textSubtotalValue = (TextView) findViewById(R.id.text_subtotal_value);
		textTaxValue = (TextView) findViewById(R.id.text_tax_value);
		textServiceChargeValue = (TextView) findViewById(R.id.text_service_charge_value);
		textDiscountValue = (TextView) findViewById(R.id.text_discount_value);
		textAmountDueValue = (TextView) findViewById(R.id.text_amount_due_value);
		textAlreadyPaidName = (TextView) findViewById(R.id.text_already_paid_name);
		textAlreadyPaidValue = (TextView) findViewById(R.id.text_already_paid_value);

		myListView = (ListView) findViewById(R.id.invoiceItemList);
		layoutBottom = (RelativeLayout) findViewById(R.id.invoice_bottom_layout);
		//layoutBottom = (RelativeLayout) findViewById(R.id.l

		list = (ListView) findViewById(R.id.invoiceItemList);

		
		payBillButton = (Button) findViewById(R.id.invoice_pay_button);
		payBillButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		            ViewCheck.this.goAddTip();
		    }
		});
		
		
		splitDollarButton = (Button) findViewById(R.id.splitDollarButton);
		splitDollarButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    
				
				
		    	showPayAmountDialog();
		    }
		});
		
		splitPercentButton = (Button) findViewById(R.id.splitPercentButton);
		splitPercentButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	showPayAmountDialogPercent();
		    }
		});
		
		
		
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
		
		for (int i = 0; i < theBill.getItems().size(); i++){
			LineItem item = theBill.getItems().get(i);
			item.setIsSelected(false);
		}
		

		displayBill();
		
		populateListView();
		registerClickCallback();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	
	
	protected void displayBill(){
		
		String check = "";
		if (theBill == null || theBill.getItems().size() == 0) {
			toastShort("Could not locate your check");
			finish();
			return;
		}
		
		invoiceId = String.valueOf(theBill.getId());
		if (theBill.getServiceCharge() == null){
			theBill.setServiceCharge(0.0);
		}
		
		if (theBill.getDiscount() == null){
			theBill.setDiscount(0.0);
		}
		
		if (theBill.getAmountPaid() == null){
			theBill.setAmountPaid(0.0);
		}
				
		
		totalBill = myPayment = theBill.getBaseAmount() + theBill.getTaxAmount() + theBill.getServiceCharge() - theBill.getDiscount() - theBill.getAmountPaid();
		amountPaid = theBill.getAmountPaid();
		
		taxPercent = theBill.getTaxAmount() / theBill.getBaseAmount();
		
		textSubtotalValue.setText(String.format("%.2f", theBill.getBaseAmount()));
		textTaxValue.setText(String.format("%.2f", theBill.getTaxAmount()));
		
		
		int nextAboveIdLeft = textTaxName.getId();
		int nextAboveIdRight = textTaxValue.getId();
		
		
		int added = 0;
		if (theBill.getServiceCharge() > 0){
			added += 40;
			textServiceChargeValue.setText(String.format("%.2f", theBill.getServiceCharge()));

			setServiceChargeLayout(nextAboveIdLeft, nextAboveIdRight);
			nextAboveIdLeft = textServiceChargeName.getId();
			nextAboveIdRight = textServiceChargeValue.getId();
			
			
		}else{
			textServiceChargeName.setVisibility(View.GONE);
			textServiceChargeValue.setVisibility(View.GONE);

		}
		
		if (theBill.getDiscount() > 0){
			added += 40;

			textDiscountValue.setText(String.format("%.2f", theBill.getDiscount()));

			setDiscountLayout(nextAboveIdLeft, nextAboveIdRight);
			nextAboveIdLeft = textDiscountName.getId();
			nextAboveIdRight = textDiscountValue.getId();
			
			
		}else{
			textDiscountName.setVisibility(View.GONE);
			textDiscountValue.setVisibility(View.GONE);

		}
		
		
		
		if (theBill.getAmountPaid() > 0){
			added += 40;

			textAlreadyPaidValue.setText(String.format("- %.2f", theBill.getAmountPaid()));

			setAlreadyPaidLayout(nextAboveIdLeft, nextAboveIdRight);
			nextAboveIdLeft = textAlreadyPaidName.getId();
			nextAboveIdRight = textAlreadyPaidValue.getId();
		}else{
			textAlreadyPaidName.setVisibility(View.GONE);
			textAlreadyPaidValue.setVisibility(View.GONE);

		}
		
		setAmountDueLayout(nextAboveIdLeft, nextAboveIdRight);

		
	   // RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    //p.addRule(RelativeLayout.ALIGN_BOTTOM, tv.getId());
		
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 170 + added);

		params.addRule(RelativeLayout.BELOW, list.getId());
		//params.addRule(RelativeLayout.ABOVE, myTotalTextView.getId());
		params.setMargins(0, 10, 0, 20);
		layoutBottom.setLayoutParams(params);
		
		textAmountDueValue.setText(String.format("$%.2f", totalBill));


		myTotalTextView.setText(String.format("My Total: $%.2f", totalBill));
		

		ArrayList<Payments> payments = theBill.getPayments();
	
		
		//set the size of the view
		int count = theBill.getItems().size();
	
		
		if (count > 5){
			
			RelativeLayout.LayoutParams newparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			newparams.setMargins(0, 5, 0, 0);
			newparams.addRule(RelativeLayout.ABOVE, layoutBottom.getId());
			newparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			myListView.setLayoutParams(newparams);
			
			
			RelativeLayout.LayoutParams newparams1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			newparams1.setMargins(0, 10, 0, 20);
			newparams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			newparams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			newparams1.addRule(RelativeLayout.ABOVE, myTotalTextView.getId());
			layoutBottom.setLayoutParams(newparams1);
			
			
		}

		

	}
	

	private void populateListView() {
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.invoiceItemList);
		list.setAdapter(adapter);
	}
	
	private void registerClickCallback() {
		
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				
				LineItem clickedItem = theBill.getItems().get(position);
				
				if (clickedItem.getIsSelected()){
					
					clickedItem.setIsSelected(false);

					
					if (areAnyRowsSelected()){
						
						if (clickedItem.getMyPayment() > 0){
							myPayment -= (clickedItem.getMyPayment() + clickedItem.getMyPayment()*taxPercent);

						}else{
							myPayment -=  clickedItem.getAmount() * (clickedItem.getValue() + clickedItem.getValue()*taxPercent);

						}
						
						showMyPayment();

						
					}else{
						myTotalTextView.setText("My Total: $" + totalBill);

					}
					
					clickedItem.setMyPayment(0.0);
					adapter.notifyDataSetChanged();

				}else{
					
					if (clickedItem.getAmount() > 1){
						currentSelectedIndex = position;
						showHowManyDialog();
					}else{
						
						if (areAnyRowsSelected()){

							myPayment += clickedItem.getValue() + clickedItem.getValue()*taxPercent;							
							
						}else{
							myPayment = clickedItem.getValue() + clickedItem.getValue()*taxPercent;
						}
						
						showMyPayment();
						clickedItem.setIsSelected(true);
						adapter.notifyDataSetChanged();

					}
					

				}
				
			
			}
		});
		
		//Long click on the cell
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				
				LineItem clickedItem = theBill.getItems().get(position);
				
				if (clickedItem.getIsSelected()){
					
				

				}else{
					
					currentSelectedIndex = position;

					if (clickedItem.getAmount() > 1){
						showHowManyDialog();
					}else{
				
						showHowMuchDialog();
					}
					

				}
				
			
				return true;
			}
		});
	}
	
	
	private boolean areAnyRowsSelected(){
		
		for (int i = 0; i < theBill.getItems().size(); i++){
			LineItem item = theBill.getItems().get(i);
			if (item.getIsSelected()){
				return true;
			}
			
		}
		return false;
	}
	
	private void deselectAllRows(){
		
		for (int i = 0; i < theBill.getItems().size(); i++){
			LineItem item = theBill.getItems().get(i);
			item.setIsSelected(false);
		}
		
		adapter.notifyDataSetChanged();
		myTotalTextView.setText(String.format("My Total: $%.2f", totalBill));


	}
	
	private void showMyPayment(){
		
		
		myTotalTextView.setText(String.format("My Total: $%.2f", myPayment));

	}
	private class MyListAdapter extends ArrayAdapter<LineItem> {
		public MyListAdapter() {
			super(ViewCheck.this, R.layout.item_row, theBill.getItems());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.item_row, parent, false);
			}
			
			// Find the car to work with.
			LineItem currentItem = theBill.getItems().get(position);
			
			// Amount
			TextView amountText = (TextView) itemView.findViewById(R.id.item_quantity);
            int amountInt = (int) Math.round(currentItem.getAmount());
			amountText.setText("" + amountInt);
			
			// Name:
			TextView nameText = (TextView) itemView.findViewById(R.id.item_name);
			nameText.setText(currentItem.getDescription());

			// Price:
			TextView priceText = (TextView) itemView.findViewById(R.id.item_price);
			priceText.setText(String.format("%.2f", currentItem.getValue()));
			
			// You Pay:
			TextView youPay = (TextView) itemView.findViewById(R.id.item_you_pay);
			//priceText.setText(String.format("%.2f", currentItem.getValue()));
			
			Boolean isLarge = false;
			if (currentItem.getIsSelected()){
				
				if (currentItem.getMyPayment() > 0.0){
					youPay.setVisibility(View.VISIBLE);
					youPay.setText("You Pay: $" + String.format("%.2f", currentItem.getMyPayment()));
					isLarge = true;

				}else{
					youPay.setVisibility(View.GONE);
				}
				
			}else{
				youPay.setVisibility(View.GONE);

			}
						
						
			ImageView backImageView = (ImageView) itemView.findViewById(R.id.back_image_view);
			ImageView backImageView2 = (ImageView) itemView.findViewById(R.id.back_image_view_two);

			if (isLarge){
				backImageView2.setVisibility(View.VISIBLE);
			}else{
				backImageView2.setVisibility(View.GONE);

			}
			
			backImageView.getLayoutParams().height = 52;

			if (currentItem.getIsSelected()){
				backImageView.setVisibility(View.VISIBLE);

				amountText.setTextColor(Color.WHITE);
				nameText.setTextColor(Color.WHITE);
				priceText.setTextColor(Color.WHITE);
				youPay.setTextColor(Color.WHITE);


			}else{
				
				amountText.setTextColor(Color.BLACK);
				nameText.setTextColor(Color.BLACK);
				priceText.setTextColor(Color.BLACK);
				backImageView.setVisibility(View.INVISIBLE);

			}
			
			
			return itemView;
		}				
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	public void onViewPaymentsClick(View v) {
		showInfoDialog(paymentInfo);
	}

	public void onMakePaymentClick(View v) {
		showPayAmountDialog();
	}

	//
	// LayoutInflater factory = LayoutInflater.from(this);
	// final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
	// return new AlertDialog.Builder(AlertDialogSamples.this)
	// .setIcon(R.drawable.alert_dialog_icon)
	// .setTitle(R.string.alert_dialog_text_entry)
	// .setView(textEntryView)
	// .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// /* User clicked OK so do some stuff */
	// }
	// })
	// .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// /* User clicked cancel so do some stuff */
	// }
	// })
	// .create();

	private void makePayment() {
		ArrayList<Cards> cards = DBController.getCards(getContentProvider());
		if (cards.size() == 0) {
			toastShort("You must add a card first (see Funds in slide-out menu)");
			return;
		}

		Cards useFirstCard = cards.get(0);

		String token = getString(Keys.DEV_TOKEN);
		String customerId = getString(Keys.DEV_CUSTOMER_ID);

		String account = ""; //useFirstCard.getNumber().replace(" ", "");
		String month = useFirstCard.getExpirationMonth();
		if (month.length() == 1) {
			month = "0" + month;
		}
		String year = useFirstCard.getExpirationYear().substring(2, 4);
		String expiration = month + "-" + year;
		String pin = useFirstCard.getCVV();
		String type = PaymentFlags.PaymentType.CREDIT.toString();
		String cardType = PaymentFlags.CardType.V.toString();
		String splitType = PaymentFlags.SplitType.DOLLAR.toString();

		Logger.d("make payment with expiration " + expiration + " and pin " + pin + " and type = " + type + " and card type = " + cardType);

		Double tipAmount = 0d;
		CreatePayment newPayment = new CreatePayment(merchantId, customerId, invoiceId, totalBill, myBill, tipAmount, account, type, cardType, expiration, pin, null, splitType, null, null, null);

		MakePaymentTask task = new MakePaymentTask(token, newPayment, getApplicationContext());
		task.execute();
	}

	private void showPayAmountDialog() {
		Logger.d("SHOWING");
		payDialog = null;

		deselectAllRows();
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		
		input.setFocusable(true);
		input.setFocusableInTouchMode(true);
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("How much would you like to pay?");
		input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		final TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
	
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
			remainingBalance.setTextColor(getResources().getColor(R.color.white));
		}

		final Double remainingBill = totalBill;
		remainingBalance.setText("Remaining balance: " + money.format(remainingBill));
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		//builder.setIcon(R.drawable.logo);
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
		payDialog = builder.create();
		
		payDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		payDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = payDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String paymentAmount = input.getText().toString();
						if (paymentAmount == null || paymentAmount.trim().length() == 0) {
							toastShort("Please enter an amount or cancel");
							return;
						} else {
							
							
							myBill = myPayment = Double.parseDouble(paymentAmount);
							if (myBill > (remainingBill)) {
								toastShort("Can't pay more than is remaining");
								return;
							}
							//toastShort("Make payment of $" + paymentAmount);
							payDialog.dismiss();
							ViewCheck.this.myTotalTextView.setText(String.format("My Total: $%.2f", myPayment));
							//makePayment();
							
						}
						payDialog.dismiss();
					}
				});
			}
		});
		payDialog.show();
	
	}
	
	
	private void showPayAmountDialogPercent() {
		payDialog = null;

		deselectAllRows();
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		input.setFocusable(true);
		input.setFocusableInTouchMode(true);
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("How many people are splitting the bill?");
		input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		final TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
	

		final Double remainingBill = totalBill;
		remainingBalance.setText("Remaining balance: " + money.format(remainingBill));
		
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
			remainingBalance.setTextColor(getResources().getColor(R.color.white));
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		
		
		
		
		//builder.setIcon(R.drawable.logo);
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
		payDialog = builder.create();
		
		
		payDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		
		payDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = payDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String paymentAmount = input.getText().toString();
						if (paymentAmount == null || paymentAmount.trim().length() == 0) {
							toastShort("Please enter an number or cancel");
							return;
						} else {
							
							
							double numPeople = Double.parseDouble(paymentAmount);
							
							myBill = myPayment = ViewCheck.this.totalBill / numPeople;
							
							if (myBill > (remainingBill)) {
								toastShort("Can't pay more than is remaining");
								return;
							}
							//toastShort("Make payment of $" + paymentAmount);
							payDialog.dismiss();
							ViewCheck.this.myTotalTextView.setText(String.format("My Total: $%.2f", myPayment));
							//makePayment();
							
						}
						payDialog.dismiss();
					}
				});
			}
		});
		payDialog.show();
	}
	
	
	private void showHowManyDialog() {
		payDialog = null;
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		input.setFocusable(true);
		input.setFocusableInTouchMode(true);
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("How many would you like to pay for?");
		input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		final TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
	

		LineItem clickedItem = theBill.getItems().get(currentSelectedIndex);

		String thisValue = String.format("%.2f", clickedItem.getValue()/clickedItem.getAmount());
		
		remainingBalance.setText(clickedItem.getAmount() + " " + clickedItem.getDescription() + ", $" + thisValue + " each");
		
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
			remainingBalance.setTextColor(getResources().getColor(R.color.white));
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		
		
		
		
		//builder.setIcon(R.drawable.logo);
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
		payDialog = builder.create();
		
		
		payDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		
		payDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = payDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String paymentAmount = input.getText().toString();
						Double amountPayingFor = Double.parseDouble(paymentAmount);
						
						LineItem clickedItem = theBill.getItems().get(currentSelectedIndex);
						
						if (amountPayingFor > clickedItem.getAmount()){
							toastShort("You cannot pay for more items than are on the bill, please enter a smaller number");
						}else{
							Double pricePerItem = clickedItem.getValue()/clickedItem.getAmount();
							
							Double amountToPay = amountPayingFor * pricePerItem;
							
							//**TODO change the TOTAL AMOUNT based on this click
							
							if (areAnyRowsSelected()){

								myPayment += amountToPay + amountToPay*taxPercent;								
								
							}else{
								myPayment = amountToPay + amountToPay*taxPercent;								
							}
							showMyPayment();

							
							
							clickedItem.setIsSelected(true);
							clickedItem.setMyPayment(amountToPay);
							adapter.notifyDataSetChanged();

							payDialog.dismiss();

						}

						
					}
				});
			}
		});
		payDialog.show();
	}
	
	
	
	private void showHowMuchDialog() {
		payDialog = null;
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		input.setFocusable(true);
		input.setFocusableInTouchMode(true);
		
		TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
		paymentTitle.setText("How many people are splitting this item?");
		input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		final TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
	

		LineItem clickedItem = theBill.getItems().get(currentSelectedIndex);

		String thisValue = String.format("%.2f", clickedItem.getValue()/clickedItem.getAmount());
		
		remainingBalance.setText(clickedItem.getDescription() + ": $" + thisValue);
		
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
		
		//Set colors
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

			paymentTitle.setTextColor(getResources().getColor(R.color.white));
			remainingBalance.setTextColor(getResources().getColor(R.color.white));
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		
		
		
		
		//builder.setIcon(R.drawable.logo);
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
		payDialog = builder.create();
		
		
		payDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		
		payDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = payDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String paymentAmount = input.getText().toString();
						Double numberPeopleSplitting = Double.parseDouble(paymentAmount);
						
						LineItem clickedItem = theBill.getItems().get(currentSelectedIndex);
						
						if (numberPeopleSplitting <= 1){
							toastShort("You must enter a number greater than 1.");
						}else{

							
							Double amountToPay = clickedItem.getValue() / numberPeopleSplitting;
							
							//**TODO change the TOTAL AMOUNT based on this click
							
							if (areAnyRowsSelected()){

								myPayment += amountToPay + amountToPay*taxPercent;								
								
							}else{
								myPayment = amountToPay + amountToPay*taxPercent;								
							}
							showMyPayment();

							
							
							clickedItem.setIsSelected(true);
							clickedItem.setMyPayment(amountToPay);
							adapter.notifyDataSetChanged();

							payDialog.dismiss();

						}

						
					}
				});
			}
		});
		payDialog.show();
	}
	
	

	private void showInfoDialog(String display) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setMessage(display);
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// hideSuccessMessage();
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
	
	private void goAddTip(){
		
		theBill.setMyBasePayment(myPayment);
		
		Intent viewCheck = new Intent(getApplicationContext(), AdditionalTip.class);
		viewCheck.putExtra(Constants.INVOICE, theBill);
		startActivity(viewCheck);
		
	}
	
	
	private void setServiceChargeLayout(int aboveIdLeft, int aboveIdRight){
		
	}
	
	private void setDiscountLayout(int aboveIdLeft, int aboveIdRight){
		
	}
	
	
	private void setAlreadyPaidLayout(int aboveIdLeft, int aboveIdRight){

		RelativeLayout.LayoutParams nameparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		nameparams.addRule(RelativeLayout.BELOW, aboveIdLeft);
		nameparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textAlreadyPaidName.setLayoutParams(nameparams);	
		
		
		RelativeLayout.LayoutParams valueparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		valueparams.addRule(RelativeLayout.BELOW, aboveIdRight);
		valueparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		textAlreadyPaidValue.setLayoutParams(valueparams);
		
		
		
	}
	
	private void setAmountDueLayout(int aboveIdLeft, int aboveIdRight){

		
		RelativeLayout.LayoutParams nameparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		nameparams.addRule(RelativeLayout.BELOW, aboveIdLeft);
		nameparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textAmountDueName.setLayoutParams(nameparams);	
		
		
		RelativeLayout.LayoutParams valueparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		valueparams.addRule(RelativeLayout.BELOW, aboveIdRight);
		valueparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		textAmountDueValue.setLayoutParams(valueparams);
		
		
		
		
	}
	
}
