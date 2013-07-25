package com.arcmobileapp.activities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.domain.LineItem;
import com.arcmobileapp.domain.Payments;
import com.arcmobileapp.domain.Payments.PaidItems;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentFlags;
import com.arcmobileapp.web.MakePaymentTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

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
	private ArrayList<LineItem> myItems;
	private ArrayList<PaidItems> currentPaidItemsArray;
	
	private String checkNumber;
	private String merchantName;
	private int currentSelectedIndex;


	private ArrayAdapter<LineItem> adapter;

	private String merchantId;

	String paymentInfo;
	private AlertDialog payDialog;
	private AlertDialog alreadyPaidDialog;

	private Double totalBill;
	private Double myBill;
	private Double amountPaid;
	private String invoiceId;
	private Check theBill;
	DecimalFormat money = new DecimalFormat("$#.00");
	NumberFormat quantity = new DecimalFormat("#");
	ListView myListView;
	
	private RelativeLayout helpLayout;
	private TextView helpItemText;
	private TextView helpDollarText;
	private TextView helpTitleText;
	private TextView helpTotalText;
	private TextView helpItemHoldText;
	private ImageView helpTotalArrow;

	private ImageView helpDollarArrow;
	
	private int helpStage;
	
	Handler handler = new Handler();
	    
	Runnable runnableTwo = new Runnable() {
	        public void run() {
	        	goStage2();
	        }
	};
	
	Runnable runnableThree = new Runnable() {
        public void run() {
        	goStage3();
        }
	};


	Runnable runnableFour = new Runnable() {
		public void run() {
    	goStage4();
		}
	};
	private Button payBillButton;

	public ViewCheck() {
		super();
	}

	public ViewCheck(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("View Check - OnCreate");

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
			
			splitDollarButton.setVisibility(View.GONE);
			splitPercentButton = (Button) findViewById(R.id.splitPercentButton);
			splitPercentButton.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			    	showPayAmountDialogPercent();
			    }
			});
			splitPercentButton.setVisibility(View.GONE);

			
			
			
			theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
			checkNumber = (String) getIntent().getSerializableExtra(Constants.CHECK_NUM);
			merchantName = (String) getIntent().getSerializableExtra(Constants.VENUE);

			for (int i = 0; i < theBill.getItems().size(); i++){
				LineItem item = theBill.getItems().get(i);
				item.setIsSelected(false);
			}
			

			displayBill();
			
			populateListView();
			registerClickCallback();
			
			showPaidItems();
			
			
			helpLayout = (RelativeLayout) findViewById(R.id.help_layout);
			helpItemText = (TextView) findViewById(R.id.help_item_text);
			helpDollarText = (TextView) findViewById(R.id.help_dollar_text);
			helpTitleText = (TextView) findViewById(R.id.help_title_text);
			helpTotalText = (TextView) findViewById(R.id.help_total_text);

			helpItemHoldText = (TextView) findViewById(R.id.help_item_hold_text);
			helpDollarArrow = (ImageView) findViewById(R.id.help_dollar_arrow);
			helpTotalArrow = (ImageView) findViewById(R.id.help_total_arrow);

			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			
			Boolean hasSeenCheckHelp = myPrefs.getBoolean(Keys.SEEN_INVOICE_HELP);
			

			if (hasSeenCheckHelp){
				helpLayout.setVisibility(View.GONE);
			}else{
				myPrefs.putAndCommitBoolean(Keys.SEEN_INVOICE_HELP, true);

		        handler.postDelayed(runnableTwo, 6000);

				helpDollarArrow.setVisibility(View.INVISIBLE);
				helpDollarText.setVisibility(View.INVISIBLE);
				helpTotalText.setVisibility(View.INVISIBLE);
				helpTotalArrow.setVisibility(View.INVISIBLE);



				helpStage = 1;
				
				helpLayout.setOnTouchListener(new View.OnTouchListener(){
					 
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						
						hideHelp();
						
						return false;
					}
			 
				});
	
			}
			
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.onCreate", "Exception Caught", "error", e)).execute();

		}


	}
	
	public void goStage2(){
		if (helpStage == 1){
			 helpStage = 2;
			 
			 handler = new Handler();
		      handler.postDelayed(runnableThree, 6000);
		      helpItemText.setVisibility(View.INVISIBLE);
		      helpItemHoldText.setVisibility(View.INVISIBLE);

		      	helpTitleText.setVisibility(View.INVISIBLE);

				helpDollarArrow.setVisibility(View.VISIBLE);
				helpDollarText.setVisibility(View.VISIBLE);


		 }
	}
	
	public void goStage3(){
		if (helpStage == 2){
			 helpStage = 3;
			 handler = new Handler();

		        handler.postDelayed(runnableFour, 6000);

		        helpDollarArrow.setVisibility(View.INVISIBLE);
				helpDollarText.setVisibility(View.INVISIBLE);
				helpTotalText.setVisibility(View.VISIBLE);
				helpTotalArrow.setVisibility(View.VISIBLE);


			 
		 }
	}

	public void goStage4(){
		helpLayout.setVisibility(View.GONE);

	}

	 public void hideHelp()
     {
			AppActions.add("View Check - Hiding Help - Stage:" + helpStage);

		 if (helpStage == 1){
			 helpStage = 2;
			 
			 handler = new Handler();
		      handler.postDelayed(runnableThree, 6000);
		      helpItemText.setVisibility(View.INVISIBLE);
		      helpItemHoldText.setVisibility(View.INVISIBLE);

		      	helpTitleText.setVisibility(View.INVISIBLE);

				helpDollarArrow.setVisibility(View.VISIBLE);
				helpDollarText.setVisibility(View.VISIBLE);


		 }else if (helpStage == 2){
			 helpStage = 3;
			 handler = new Handler();

		        handler.postDelayed(runnableFour, 6000);

		        helpDollarArrow.setVisibility(View.INVISIBLE);
				helpDollarText.setVisibility(View.INVISIBLE);
				helpTotalText.setVisibility(View.VISIBLE);
				helpTotalArrow.setVisibility(View.VISIBLE);


			 
		 }else{
				helpLayout.setVisibility(View.GONE);

		 }

     }
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	
	protected void displayBill(){
		
		try {
			

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
					
			if(checkNumber == null){
				checkNumber = "itwasnull";
			}
			if (merchantName == null){
				merchantName = "itwasnull";
			}
			AppActions.add("View Check - Displaying Bill #: " + checkNumber + " for Merchant: " + merchantName);
			totalBill = myPayment = theBill.getBaseAmount() + theBill.getTaxAmount() + theBill.getServiceCharge() - theBill.getDiscount() - theBill.getAmountPaid();
			AppActions.add("View Check - Bill Details - Total:" + totalBill + ", Tax:" + theBill.getTaxAmount() + ", Service Charge:" + theBill.getServiceCharge() + ", Discount:" + theBill.getDiscount() + ", Amount Paid:" + theBill.getAmountPaid());

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
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.displayBill", "Exception Caught", "error", e)).execute();

		}

		

	}
	

	private void populateListView() {
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.invoiceItemList);
		list.setAdapter(adapter);
	}
	
	private void registerClickCallback() {
		
		
		try {
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					try {
						
						LineItem clickedItem = theBill.getItems().get(position);
						AppActions.add("View Check - Line Item Clicked: " + clickedItem.getDescription());

						
						if (clickedItem.getIsPaidFor().equals("yes")){
							
							toastShort("This Item has already been paid in full.  Please choose a different item.");
							
						}else if (clickedItem.getIsSelected()){
							
							clickedItem.setIsSelected(false);

							
							if (areAnyRowsSelected()){
								
								if (clickedItem.getMyPayment() > 0){
									myPayment -= (clickedItem.getMyPayment() + clickedItem.getMyPayment()*taxPercent);

								}else{
									myPayment -=  clickedItem.getAmount() * (clickedItem.getValue() + clickedItem.getValue()*taxPercent);

								}
								
								showMyPayment();

								
							}else{
								myTotalTextView.setText(String.format("My Total: $%.2f", totalBill));

							}
							
							clickedItem.setMyPayment(0.0);
							adapter.notifyDataSetChanged();

						}else{
							
							if (clickedItem.getIsPaidFor().equals("maybe")){

								toastShort("This Item has already been partially paid for.  If you wish to pay for part of it, press and hold the item.");

								
							}else if (clickedItem.getAmount() > 1){
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
					} catch (Exception e) {
						(new CreateClientLogTask("ViewCheck.onRegisterClickCallBack.onItemClick", "Exception Caught", "error", e)).execute();

					}
					
				
				}
			});
			
			//Long click on the cell
			list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					try {
						LineItem clickedItem = theBill.getItems().get(position);
						AppActions.add("View Check - Line Item Long Clicked: " + clickedItem.getDescription());

						
						if (clickedItem.getIsPaidFor().equals("yes")){
							
							toastShort("This Item has already been paid in full.  Please choose a different item.");
							
						}else if (clickedItem.getIsSelected()){
							
						

						}else{
							
							currentSelectedIndex = position;

							if (clickedItem.getAmount() > 1){
								showHowManyDialog();
							}else{
						
								showHowMuchDialog();
							}
							

						}
						

						return true;
					} catch (Exception e) {
						(new CreateClientLogTask("ViewCheck.onRegisterClickCallBack.onItemLongClick", "Exception Caught", "error", e)).execute();
						return false;

					}
				}
			});
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.registerClickCallback", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private boolean areAnyRowsSelected(){
		
		try {
			for (int i = 0; i < theBill.getItems().size(); i++){
				LineItem item = theBill.getItems().get(i);
				if (item.getIsSelected()){
					return true;
				}
				
			}
			return false;
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.areAnyRowsSelected", "Exception Caught", "error", e)).execute();
			return false;

		}
	}
	
	private void deselectAllRows(){
		
		try {
			for (int i = 0; i < theBill.getItems().size(); i++){
				LineItem item = theBill.getItems().get(i);
				item.setIsSelected(false);
			}
			
			adapter.notifyDataSetChanged();
			myTotalTextView.setText(String.format("My Total: $%.2f", totalBill));
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.deselectAllRows", "Exception Caught", "error", e)).execute();

		}


	}
	
	private void showMyPayment(){
		
		
		try {
			myTotalTextView.setText(String.format("My Total: $%.2f", myPayment));
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.showMyPayment", "Exception Caught", "error", e)).execute();

		}

	}
	private class MyListAdapter extends ArrayAdapter<LineItem> {
		public MyListAdapter() {
			super(ViewCheck.this, R.layout.item_row, theBill.getItems());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			try {
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
				priceText.setText(String.format("%.2f", currentItem.getValue() * currentItem.getAmount()));
				
				// You Pay:
				TextView youPay = (TextView) itemView.findViewById(R.id.item_you_pay);
				//priceText.setText(String.format("%.2f", currentItem.getValue()));
				
				Boolean isLarge = false;
				
				RelativeLayout backImageView = (RelativeLayout) itemView.findViewById(R.id.back_image_view);
				RelativeLayout backImageView2 = (RelativeLayout) itemView.findViewById(R.id.back_image_view_two);
				
				
				if (currentItem.getIsPaidFor().equals("yes")){

					isLarge = true;
					youPay.setVisibility(View.VISIBLE);
					youPay.setText("PAID");
				}else if (currentItem.getIsSelected()){
					
					if (currentItem.getMyPayment() > 0.0){
						youPay.setVisibility(View.VISIBLE);
						youPay.setText("You Pay: $" + String.format("%.2f", currentItem.getMyPayment()));
						isLarge = true;

					}else{
						youPay.setVisibility(View.GONE);
					}
					
				}else if (currentItem.getIsPaidFor().equals("maybe")){
					youPay.setVisibility(View.VISIBLE);
					youPay.setText("% PAID");
					isLarge = true;

				}else{
					youPay.setVisibility(View.GONE);

				}
							
							


				if (isLarge){
					backImageView2.setVisibility(View.VISIBLE);
				}else{
					backImageView2.setVisibility(View.GONE);

				}
				

				backImageView.setBackgroundColor(Color.BLUE);
				backImageView2.setBackgroundColor(Color.BLUE);


				if (currentItem.getIsPaidFor().equals("yes")){
					
					backImageView.setVisibility(View.VISIBLE);
					backImageView.setBackgroundColor(Color.LTGRAY);
					backImageView2.setBackgroundColor(Color.LTGRAY);

					amountText.setTextColor(Color.BLACK);
					nameText.setTextColor(Color.BLACK);
					priceText.setTextColor(Color.BLACK);
					youPay.setTextColor(Color.WHITE);

					
				}else if (currentItem.getIsSelected()){
					backImageView.setVisibility(View.VISIBLE);

					amountText.setTextColor(Color.WHITE);
					nameText.setTextColor(Color.WHITE);
					priceText.setTextColor(Color.WHITE);
					youPay.setTextColor(Color.WHITE);


				}else if (currentItem.getIsPaidFor().equals("maybe")){
					
					backImageView.setVisibility(View.VISIBLE);
					backImageView.setBackgroundColor(Color.LTGRAY);
					backImageView2.setBackgroundColor(Color.LTGRAY);
					youPay.setTextColor(Color.WHITE);

					amountText.setTextColor(Color.BLACK);
					nameText.setTextColor(Color.BLACK);
					priceText.setTextColor(Color.BLACK);
					
					
				}else{
				
					
				
					
					
					amountText.setTextColor(Color.BLACK);
					nameText.setTextColor(Color.BLACK);
					priceText.setTextColor(Color.BLACK);
					backImageView.setVisibility(View.GONE);

				}
				
				
				return itemView;
			} catch (Exception e) {
				(new CreateClientLogTask("ViewCheck.MyListAdapter.getView", "Exception Caught", "error", e)).execute();
				return convertView;

			}
		}				
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.view_check_menu, menu);
		return true;
	}

	public void onViewPaymentsClick(View v) {
		showInfoDialog(paymentInfo);
	}

	public void onMakePaymentClick(View v) {
		showPayAmountDialog();
	}



	

	private void showPayAmountDialog() {
		try {
			
			AppActions.add("View Check - Showing Dollar Split Dialog");

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

					try {
						Button b = payDialog.getButton(AlertDialog.BUTTON_POSITIVE);
						b.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								String paymentAmount = input.getText().toString();
								if (paymentAmount == null || paymentAmount.trim().length() == 0) {
									toastShort("Please enter an amount or cancel");
									return;
								} else if (paymentAmount.equals(".")){
									toastShort("Please enter an amount or cancel");
									return;
								}else {
									
									AppActions.add("View Check - Dollar Split Amount Enetered:" + paymentAmount);

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
					} catch (Exception e) {
						(new CreateClientLogTask("ViewCheck.showPayAmountDialog.onShow.onClick", "Exception Caught", "error", e)).execute();

					}
				}
			});
			payDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("ViewCheck.showPayAmountDialog", "Exception Caught", "error", e)).execute();

		}
	
	}
	
	
	private void showPayAmountDialogPercent() {
		try {
			
			AppActions.add("View Check - Showing Percent Split Dialog");

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
							try {
								String paymentAmount = input.getText().toString();
								if (paymentAmount == null || paymentAmount.trim().length() == 0) {
									toastShort("Please enter an number or cancel");
									return;
								}else if (paymentAmount.equals(".")){
									toastShort("Please enter an amount or cancel");
									return;
								} else {
									
									AppActions.add("View Check - Percent Split Amount Enetered:" + paymentAmount);

									double numPeople = Double.parseDouble(paymentAmount);
									
									if (numPeople < 1){
										toastShort("Please enter a number greater than 1");
										return;
									}
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
							} catch (NumberFormatException e) {
								(new CreateClientLogTask("ViewCheck.showPayAmountDialogPercent.onShow.onClick", "Exception Caught", "error", e)).execute();

							}
						}
					});
				}
			});
			payDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("ViewCheck.showPayAmountDialogPercent", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private void showHowManyDialog() {
		try {
			
			AppActions.add("View Check - Showing Split Item (Multi Amount)");

			
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

			String thisValue = String.format("%.2f", clickedItem.getValue());
			
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
							try {
								String paymentAmount = input.getText().toString();
								
								if (paymentAmount == null || paymentAmount.trim().length() == 0) {
									toastShort("Please enter an number or cancel");

								}else{
									
									Double amountPayingFor = Double.parseDouble(paymentAmount);

									LineItem clickedItem = theBill.getItems().get(currentSelectedIndex);
									
									if (amountPayingFor > clickedItem.getAmount()){
										toastShort("You cannot pay for more items than are on the bill, please enter a smaller number");
									}else{
										
										
										AppActions.add("View Check - Showing Split Item (Multi Amount) - Amount Paying For:" + amountPayingFor);

										Double pricePerItem = clickedItem.getValue();
										
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
								
							} catch (NumberFormatException e) {
								(new CreateClientLogTask("ViewCheck.showHowManyDialog.onShow.onClick", "Exception Caught", "error", e)).execute();

							}

							
						}
					});
				}
			});
			payDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("ViewCheck.showHowManyDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	
	private void showHowMuchDialog() {
		try {
			
			AppActions.add("View Check - Showing Split Item (Single Amount)");

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
							try {
								String paymentAmount = input.getText().toString();
								
								if (paymentAmount == null || paymentAmount.trim().length() == 0) {
									toastShort("Please enter an number or cancel");

								}else{
									Double numberPeopleSplitting = Double.parseDouble(paymentAmount);
									
									LineItem clickedItem = theBill.getItems().get(currentSelectedIndex);
									
									if (numberPeopleSplitting <= 1){
										toastShort("You must enter a number greater than 1.");
									}else{

										AppActions.add("View Check - Showing Split Item (Single Amount) - Number people splitting:" + numberPeopleSplitting);

										
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
								
							} catch (NumberFormatException e) {
								(new CreateClientLogTask("ViewCheck.showHowMuchDialog.onShow.onClick", "Exception Caught", "error", e)).execute();

							}

							
						}
					});
				}
			});
			payDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("ViewCheck.showHowMuchDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	

	private void showInfoDialog(String display) {
		try {
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
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void goAddTip(){
		
		try {
			

			theBill.setMyBasePayment(myPayment);
			
			theBill.setMyItems(new ArrayList<LineItem>());
			buildMyArray();
			theBill.setMyItems(myItems);
			
			AppActions.add("View Check - Pay Bill Clicked - My Payment:" + theBill.getMyBasePayment() + ", Number Of Items:" + myItems.size());

			Intent viewCheck = new Intent(getApplicationContext(), AdditionalTip.class);
			viewCheck.putExtra(Constants.INVOICE, theBill);
			startActivity(viewCheck);
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.goAddTip", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
	private void setServiceChargeLayout(int aboveIdLeft, int aboveIdRight){
		
	}
	
	private void setDiscountLayout(int aboveIdLeft, int aboveIdRight){
		
	}
	
	
	private void setAlreadyPaidLayout(int aboveIdLeft, int aboveIdRight){

		try {
			RelativeLayout.LayoutParams nameparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			nameparams.addRule(RelativeLayout.BELOW, aboveIdLeft);
			nameparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			textAlreadyPaidName.setLayoutParams(nameparams);	
			
			
			RelativeLayout.LayoutParams valueparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			valueparams.addRule(RelativeLayout.BELOW, aboveIdRight);
			valueparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			textAlreadyPaidValue.setLayoutParams(valueparams);
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.setAlreadyPaidLayout", "Exception Caught", "error", e)).execute();

		}
		
		
		
	}
	
	private void setAmountDueLayout(int aboveIdLeft, int aboveIdRight){

		
		try {
			RelativeLayout.LayoutParams nameparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			nameparams.addRule(RelativeLayout.BELOW, aboveIdLeft);
			nameparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			textAmountDueName.setLayoutParams(nameparams);	
			
			
			RelativeLayout.LayoutParams valueparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			valueparams.addRule(RelativeLayout.BELOW, aboveIdRight);
			valueparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			textAmountDueValue.setLayoutParams(valueparams);
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.setAmoutnDueLayout", "Exception Caught", "error", e)).execute();

		}
		
		
		
		
	}
	
	
	public void buildMyArray(){
		
		 try {
			myItems = new ArrayList<LineItem>();

			 
			 if (areAnyRowsSelected()) {
			     
				 
			     for (int i = 0; i < theBill.getItems().size(); i++){
			    	 
			    	 LineItem tmpItem = theBill.getItems().get(i);
			    	 LineItem sendInItem = new LineItem();
			    	 
			    	if (tmpItem.getIsSelected()){
			    		
			    		double myAmount = tmpItem.getMyPayment();
			    		
			    		if (myAmount == 0.0){
			    			//Selected and 0.0 = paying for 1 whole item
			    			
			    			LineItem newItem = new LineItem();
			    			
			    			newItem.setAmount(1.0);
			    			newItem.setId(tmpItem.getId());
			    			newItem.setPercent(1.0);
			    			myItems.add(newItem);
			    			
			    			
			    		}else{
			    			
			    			double totalAmount = tmpItem.getValue();

			        		double myPercent = myAmount/totalAmount;
			        		while (myPercent > 1){
			        			
			        			myPercent -= 1;
			        			
			        			LineItem newItem = new LineItem();
			        			
			        			newItem.setAmount(1.0);
			        			newItem.setId(tmpItem.getId());
			        			newItem.setPercent(1.0);
			        			myItems.add(newItem);
			        			
			        		}
			        		
			        	
			        		sendInItem.setAmount(1.0);
			        		sendInItem.setId(tmpItem.getId());
			        		sendInItem.setPercent(myPercent);
			        		myItems.add(sendInItem);
			        		
			    		}
			    		
			    		
			    		
			    	}
			     }
			     




			 }
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.buildMyArray", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	public void showPaidItems(){
	    
	    try {
			if (true) {
			    
			    //self.didShowPaidItems = YES;
			    
			    consolidatePartialPayments();
			    
			    ArrayList<PaidItems> myPaidItemsArray = theBill.getPaidItems();

			    if (myPaidItemsArray != null && myPaidItemsArray.size() > 0){
			    	AppActions.add("View Check - Showing Paid Items - Count:" + myPaidItemsArray.size());
				    
				    for (int i = 0; i < theBill.getItems().size(); i++) {
				        
				        LineItem item = theBill.getItems().get(i);
				        
				        
				        for (int j = 0; j < myPaidItemsArray.size(); j++) {
				            
				        	PaidItems paidItem = myPaidItemsArray.get(j);
				            
				            
				            if (paidItem.getItemId() == item.getId()) {
				                
				                //Item is at least partially paid for
				                
				                if (paidItem.getPercentPaid() == 1.0) {
				                    
				                    double paidItemAmount = paidItem.getAmount();
				                    double myItemAmount = item.getAmount();
				                    
				                    if (paidItemAmount >= myItemAmount) {
				                    	
				                    	item.setIsPaidFor("yes");
				                        myPaidItemsArray.remove(j);
				                        break;
				                    }else{
				                    	item.setIsPaidFor("maybe");
				                        myPaidItemsArray.remove(j);
				                        break;
				                    }
				                    
				                }else{
				                    //[item setValue:@"maybe" forKey:@"isPaidFor"];
				                    
				                }
				                
				            }
				            
				        	item.setIsPaidFor("ano");

				            
				        }
				        
				        
				    }

				    currentPaidItemsArray = myPaidItemsArray;
				    
				    //myPaidItems array still contains payments of partial Items, go through again
				    
				    for (int i = 0; i < theBill.getItems().size(); i++) {
				        
				        LineItem item = theBill.getItems().get(i);
				        
				        if (item.getIsPaidFor().equals("ano")) {
				            
				        	 for (int j = 0; j < myPaidItemsArray.size(); j++) {
				                    
				                 PaidItems paidItem = myPaidItemsArray.get(j);
				                    
				                 if (paidItem.getItemId() == item.getId()) {
				                        
				                        //Item is at least partially paid for
				                 	item.setIsPaidFor("maybe");

				                        
				                    myPaidItemsArray.remove(j);
				                        
				                    break;
				
				                 }
				                    
				             	item.setIsPaidFor("ano");
				                
				        	 }
				        }
		
				    }
				    
				    
				    /* sort and reload the table
				    NSSortDescriptor *sorter = [[NSSortDescriptor alloc] initWithKey:@"isPaidFor" ascending:YES];
				    NSArray *sortDescriptors = [NSArray arrayWithObject:sorter];
				    self.myInvoice.items = [NSMutableArray arrayWithArray:[self.myInvoice.items sortedArrayUsingDescriptors:sortDescriptors]];
				    
				    
				    
				    */

					adapter.notifyDataSetChanged();

			    }
				
			    
			}
		} catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.showPaidItems", "Exception Caught", "error", e)).execute();

		}
	    
	    
	}

	public void consolidatePartialPayments(){
	    
		try{

	   
	        ArrayList<PaidItems> myPaidItemsArray = theBill.getPaidItems();

	        if (myPaidItemsArray != null && myPaidItemsArray.size() > 0){
	        	
	        	 for (int i = 0; i < myPaidItemsArray.size(); i++) {
	 	            
	                 PaidItems paidItem = myPaidItemsArray.get(i);
	 	            
	 	            if (i != myPaidItemsArray.size() - 1) {
	 	                
	 	                
	 	                for (int j = i+1; j < myPaidItemsArray.size(); j++) {
	 	                    
	 	                    PaidItems paidItemCheck = myPaidItemsArray.get(j);
	 	                    
	 	                    if (paidItem.getItemId() == paidItemCheck.getItemId()) {
	 	                        
	 	                        double initialPercent = paidItem.getPercentPaid();
	 	                        double newPercent = paidItemCheck.getPercentPaid();
	 	                    
	 	                        
	 	                        initialPercent += newPercent;
	 	                        
	 	                        paidItem.setPercentPaid(initialPercent);
	 	                        
	                             myPaidItemsArray.remove(j);
	 	                        j--;
	 	                    }
	 	                }
	 	            }
	 	        }
	 	        //Consolidated, but Percent might be > 1.0
	 	        
	 	        for (int i = 0; i < myPaidItemsArray.size(); i++) {
	 	            
	                 PaidItems paidItem = myPaidItemsArray.get(i);
	 	            
	 	            if (paidItem.getPercentPaid() > 1.0) {
	 	                //uh oh
	 	                double percent = paidItem.getPercentPaid();
	 	                
	 	                paidItem.setPercentPaid(1.0);
	 	                paidItem.setAmount(percent);
	 	                
	 	              //  Payments myPayment = new Payments();
	 	              //  PaidItems newPaidItem = myPayment.new PaidItems(paidItem.getItemId(), percent, 1.0, paidItem.getPaidBy(), paidItem.getPaidByAct() );
	 	               // myPaidItemsArray.add(newPaidItem);
	 	      
	 	            
	                    // myPaidItemsArray.remove(i);
	 	               // i--;

	 	                
	 	            }
	 	        }
	 	        
	        }
	        
	       
	         
		}catch (Exception e) {
			(new CreateClientLogTask("ViewCheck.consolidatePartialPayments", "Exception Caught", "error", e)).execute();


	    }
	  
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

	

		case R.id.splitByDollar:
	    	showPayAmountDialog();
			break;
			
		case R.id.splitByPercent:
	    	showPayAmountDialogPercent();

			break;
			
		case R.id.showBalance:
			showAlreadyPaidDialog();
			break;
			
	
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showAlreadyPaidDialog() {
		try {
			alreadyPaidDialog = null;

			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.who_paid, null);
			final ScrollView myScroll = (ScrollView) makePaymentView.findViewById(R.id.whoPaidScroll);
			myScroll.setBackgroundColor(Color.WHITE);
			TextView remainingText = (TextView) makePaymentView.findViewById(R.id.remainingText);
			TextView paymentsText = (TextView) makePaymentView.findViewById(R.id.paymentsText);
			
			remainingText.setText(String.format("$%.2f", totalBill));
			if (theBill.getPayments().size() > 0){
				paymentsText.setText("Payments:");
				
				LinearLayout child = (LinearLayout) makePaymentView.findViewById(R.id.scrollerLayout);

				for (int i = 0; i < theBill.getPayments().size(); i++){
					
					Payments payment = theBill.getPayments().get(i);
					
					LayoutInflater inflater = LayoutInflater.from(this);
					View paymentView = inflater.inflate(R.layout.who_paid_sub, null);
					
					TextView nameText = (TextView) paymentView.findViewById(R.id.nameText);
					TextView amountText = (TextView) paymentView.findViewById(R.id.amountText);
					TextView notesText = (TextView) paymentView.findViewById(R.id.notesText);
					
					nameText.setText("Name: " + payment.getCustomerName());
					amountText.setText(String.format("Amount: $%.2f", payment.getAmount()));
					notesText.setText("Notes: " + payment.getNotes());
					
					child.addView(paymentView);

					
				}
			}

			

			
			AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
			builder.setTitle("Remaining Balance");
			builder.setView(makePaymentView);
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("Pay Remaining", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
			alreadyPaidDialog = builder.create();
			

			alreadyPaidDialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {

					Button b = alreadyPaidDialog.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							try {
								ViewCheck.this.deselectAllRows();
								ViewCheck.this.myPayment = ViewCheck.this.totalBill;
								ViewCheck.this.goAddTip();
								alreadyPaidDialog.dismiss();
							} catch (Exception e) {
								(new CreateClientLogTask("ViewCheck.showAlreadyPaidDialog.onShow.onClickPositive", "Exception Caught", "error", e)).execute();

							}
						}
					});
					
					Button c = alreadyPaidDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
					c.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							alreadyPaidDialog.dismiss();
						}
					});
					
					
				}
			});
			alreadyPaidDialog.show();
		} catch (Exception e) {

			(new CreateClientLogTask("ViewCheck.showAlreadyPaidDialog", "Exception Caught", "error", e)).execute();

		}
	
	}
	
}
