package com.arcmobileapp.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentHistoryObject;
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.GetPaymentHistoryTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
import com.dutchmobileapp.R;

public class PaymentHistory extends BaseActivity {

	private ProgressDialog loadingDialog;
	private Boolean isFirstLoad;
	private ArrayList<PaymentHistoryObject> payments;
	private ArrayAdapter<PaymentHistoryObject> adapter;

	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_payment_history);
			
			list = (ListView) findViewById(R.id.paymentsList);

			isFirstLoad = true;
			
		} catch (NotFoundException e) {

			(new CreateClientLogTask("PaymentHistory.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	
	private void populateListView() {
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.paymentsList);
		list.setAdapter(adapter);
	}
	
	
	private class MyListAdapter extends ArrayAdapter<PaymentHistoryObject> {
		public MyListAdapter() {
			super(PaymentHistory.this, R.layout.payment_item_row, payments);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			try {
				View itemView = convertView;
				if (itemView == null) {
					itemView = getLayoutInflater().inflate(R.layout.payment_item_row, parent, false);
				}
				
				// Find the car to work with.
				PaymentHistoryObject currentItem = payments.get(position);
				
				
				// Amount
				TextView totalPayment = (TextView) itemView.findViewById(R.id.p_total_payment);
				totalPayment.setTypeface(ArcMobileApp.getLatoBoldTypeface());
				double theAmount = currentItem.gratuityAmount + currentItem.invoiceAmount;
				totalPayment.setText(String.format("$%.2f", theAmount));
				
				// Name:
				TextView merchantName = (TextView) itemView.findViewById(R.id.p_merchant_name);
				merchantName.setText(currentItem.merchantName);
				merchantName.setTypeface(ArcMobileApp.getLatoBoldTypeface());

				// Price:
				TextView date = (TextView) itemView.findViewById(R.id.p_date);
				date.setText(getReadableDate(currentItem.invoiceDate));
				date.setTypeface(ArcMobileApp.getLatoLightTypeface());

				// You Pay:
				TextView invoiceNumber = (TextView) itemView.findViewById(R.id.p_check_number);
				invoiceNumber.setText("Check #: " + currentItem.invoiceNumber);
				invoiceNumber.setTypeface(ArcMobileApp.getLatoLightTypeface());

		
				
				return itemView;
			} catch (Exception e) {
			
				(new CreateClientLogTask("PaymentHhistory.MyListAdapter.getView", "Exception Caught", "error", e)).execute();
				return convertView;

			}
		}				
	}
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadingDialog = new ProgressDialog(PaymentHistory.this);
		loadingDialog.setTitle("Getting Payments");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		
		if (isFirstLoad){
			getPaymentHistory();
		}
	}
	
	
	protected void getPaymentHistory() {
		try {
			
			String token = getToken();
			String customerId = getId();
			
			loadingDialog.show();
			
			GetPaymentHistoryTask getMerchantsTask = new GetPaymentHistoryTask(getApplicationContext(), token, customerId) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						int errorCode = getErrorCode();

						payments = new ArrayList<PaymentHistoryObject>();
						
						payments = getPayemntList();
						

						loadingDialog.hide();
						if (payments != null && payments.size() > 0){
						
							AppActions.add("Home - Get Payments Succeeded - Number Of Payments:" + payments.size());

							Logger.d("POPULATING LIST VIEW **********");
							PaymentHistory.this.populateListView();
							PaymentHistory.this.registerClickCallback();

										        
						}else{
							AppActions.add("Home - Get Payments Failed - Error Code:" + errorCode);

							//Remove carousel view?
						//	scrollView.setVisibility(View.INVISIBLE);
						//	currentMerchantText.setVisibility(View.INVISIBLE);
						//	currentMerchantAddressText.setVisibility(View.INVISIBLE);

							
							if (errorCode != 0){
								
								String errorMsg = "";
								
								if(errorCode == 999) {
					                errorMsg = "Can not find payments.";
					            } else {
					                errorMsg = ErrorCodes.ARC_ERROR_MSG;
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("Error retrieving payment history.");

							}


						}
					} catch (Exception e) {
						
						//scrollView.setVisibility(View.INVISIBLE);
						//currentMerchantText.setVisibility(View.INVISIBLE);
						//currentMerchantAddressText.setVisibility(View.INVISIBLE);
					//	
						toastShort("Error retrieving payment history.");

						(new CreateClientLogTask("PaymentHistory.getPaymentHistory.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getMerchantsTask.execute();
		} catch (Exception e) {

			(new CreateClientLogTask("PaymentHistory.getPaymentHistory", "Exception Caught", "error", e)).execute();

		}
	}
	
private String getReadableDate(String isoDate){
		
		String newString = "";
		try{
			
			Logger.d("Entering");

			TimeZone tz = TimeZone.getTimeZone("UTC");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
			
			Logger.d("Step 1");

			//df.setTimeZone(tz);
			
			Date myDate = df.parse(isoDate);
			
			Logger.d("Date: " + myDate);
			df = new SimpleDateFormat("MM/dd hh:mma");
			
			newString = df.format(myDate);
			Logger.d("NewString " + newString);

		}catch (Exception e){
			Logger.d("Exception " + e.getLocalizedMessage());
			return "";
		}
		
		
		return newString;
		
	}

	
	
private void registerClickCallback() {
		
		
		try {
			
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					try {

						PaymentHistoryObject payment = payments.get(position);

						Intent detail = new Intent(getApplicationContext(), PaymentDetail.class);
						detail.putExtra(Constants.PAYMENT_OBJECT, payment);
						startActivity(detail);
						
					
					} catch (Exception e) {

						(new CreateClientLogTask("PaymentHistory.onRegisterClickCallBack.onItemClick", "Exception Caught", "error", e)).execute();

					}
					
				
				}
			});
			
		
		} catch (Exception e) {

			(new CreateClientLogTask("PaymentHistory.registerClickCallback", "Exception Caught", "error", e)).execute();

		}
	}



}
