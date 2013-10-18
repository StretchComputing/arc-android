package com.arcmobileapp.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.app.ProgressDialog;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentHistoryObject;
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.SendEmailReceiptTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
import com.dutchmobileapp.R;

public class PaymentDetail extends BaseActivity {

	private ProgressDialog loadingDialog;
	private PaymentHistoryObject paymentObject;
	
	private TextView totalAmount;
	private TextView merchantName;
	private TextView invoiceDate;

	private TextView baseAmountLabel;
	private TextView tipLabel;
	private TextView paymentLabel;
	private TextView checkNumberLabel;
	private TextView confirmationNumberLabel;
	private TextView notesLabel;

	private TextView baseAmountText;
	private TextView tipText;
	private TextView paymentText;
	private TextView checkNumberText;
	private TextView confirmationNumberText;
	private TextView notesText;

	private TextView copyText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_payment_detail);
		
			paymentObject =  (PaymentHistoryObject) getIntent().getSerializableExtra(Constants.PAYMENT_OBJECT);

			
			totalAmount = (TextView) findViewById(R.id.totalAmountText);
			merchantName = (TextView) findViewById(R.id.merchantNameText);
			invoiceDate = (TextView) findViewById(R.id.dateText);

			baseAmountLabel = (TextView) findViewById(R.id.baseAmountLabel);
			tipLabel = (TextView) findViewById(R.id.tipLabel);
			paymentLabel = (TextView) findViewById(R.id.paymentLabel);
			checkNumberLabel = (TextView) findViewById(R.id.checkNumberLabel);
			confirmationNumberLabel = (TextView) findViewById(R.id.confirmationNumberLabel);
			notesLabel = (TextView) findViewById(R.id.notesLabel);

			baseAmountText = (TextView) findViewById(R.id.baseAmountValue);
			tipText = (TextView) findViewById(R.id.tipValue);
			paymentText = (TextView) findViewById(R.id.paymentValue);
			checkNumberText = (TextView) findViewById(R.id.checkNumberValue);
			confirmationNumberText = (TextView) findViewById(R.id.confirmationNumberValue);
			notesText = (TextView) findViewById(R.id.notesValue);

			copyText = (TextView) findViewById(R.id.copyLabel);

			
			
			
			totalAmount.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			merchantName.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			invoiceDate.setTypeface(ArcMobileApp.getLatoLightTypeface());
			baseAmountLabel.setTypeface(ArcMobileApp.getLatoLightTypeface());
			tipLabel.setTypeface(ArcMobileApp.getLatoLightTypeface());
			paymentLabel.setTypeface(ArcMobileApp.getLatoLightTypeface());
			checkNumberLabel.setTypeface(ArcMobileApp.getLatoLightTypeface());
			confirmationNumberLabel.setTypeface(ArcMobileApp.getLatoLightTypeface());
			notesLabel.setTypeface(ArcMobileApp.getLatoLightTypeface());
			baseAmountText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			tipText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			paymentText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			checkNumberText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			confirmationNumberText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			notesText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			copyText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			
			
			totalAmount.setText(String.format("$%.2f", paymentObject.gratuityAmount + paymentObject.invoiceAmount));
			merchantName.setText(paymentObject.merchantName);
			invoiceDate.setText(getReadableDate(paymentObject.invoiceDate));
			
			baseAmountText.setText(String.format("$%.2f", paymentObject.invoiceAmount));
			tipText.setText(String.format("$%.2f", paymentObject.gratuityAmount));
			paymentText.setText(paymentObject.cardNumber);
			checkNumberText.setText(paymentObject.invoiceNumber);
			confirmationNumberText.setText(paymentObject.confirmationNumber);
			
			if (paymentObject.invoiceNotes.length() > 0){
				notesText.setText(paymentObject.invoiceNotes);
			}else{
				notesText.setText("--");
			}


		} catch (NotFoundException e) {
			(new CreateClientLogTask("PaymentDetailonCreate", "Exception Caught", "error", e)).execute();

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
		loadingDialog = new ProgressDialog(PaymentDetail.this);
		loadingDialog.setTitle("Sending Receipt");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
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
	
	
	public void onResendClick(View view) {

		try {
	
			sendEmailReceipt();
			
		} catch (Exception e) {
			(new CreateClientLogTask("PaymentDetail.onResendClick", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	protected void sendEmailReceipt() {
		try {
			
			String token = getToken();
			String customerId = getId();
			
			loadingDialog.show();
			
			SendEmailReceiptTask sendTask = new SendEmailReceiptTask(getApplicationContext(), token, paymentObject.paymentId) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						int errorCode = getErrorCode();

						
						loadingDialog.hide();
						if (getSuccess()){

							AppActions.add("Home - Send Receipt Succeeded");
							toastShort("Receipt sent successfully!.");


						}else{
							AppActions.add("Home - Send Receipt Failed - Error Code:" + errorCode);

			

							toastShort("Error sending receipt, please try again.");



						}
					} catch (Exception e) {
						
			
						toastShort("Error sending receipt, please try again.");

						(new CreateClientLogTask("PaymentDetail.sendEmailReceipt.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			sendTask.execute();
		} catch (Exception e) {

			(new CreateClientLogTask("PaymentDetail.sendEmailReceipt", "Exception Caught", "error", e)).execute();

		}
	}
	
}
