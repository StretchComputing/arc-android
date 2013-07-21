package com.arcmobileapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.GetCheckTask;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class GetCheck extends BaseActivity {

	private TextView title;
	private ProgressBar activityBar;
	private EditText invoice;
	private String venueName;
	private String merchantId;
	private ProgressDialog loadingDialog;
	
	public GetCheck() {
		super();
	}

	public GetCheck(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.get_check);
			invoice = (EditText) findViewById(R.id.invoice);
			title = (TextView) findViewById(R.id.title);
			//activityBar = (ProgressBar) findViewById(R.id.activityBar);
			//activityBar.setVisibility(View.INVISIBLE);
			
			venueName = getIntent().getStringExtra(Constants.VENUE);
			merchantId = getIntent().getStringExtra(Constants.VENUE_ID);
			title.setText(venueName);
			
			loadingDialog = new ProgressDialog(GetCheck.this);
			loadingDialog.setTitle("Getting Invoice");
			loadingDialog.setMessage("Please Wait...");
			loadingDialog.setCancelable(false);
			

		} catch (Exception e) {
			(new CreateClientLogTask("GetCheck.onCreate", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
	public void onViewBillClick(View v) {
		try {
			String checkNum = invoice.getText().toString();
			if(checkNum == null || checkNum.trim().length() == 0) {
				toastLong("Please enter your check number");
				return;
			}
			Intent viewCheck = new Intent(getApplicationContext(), ViewCheck.class);
			viewCheck.putExtra(Constants.VENUE, venueName);
			viewCheck.putExtra(Constants.CHECK_NUM, checkNum);
			viewCheck.putExtra(Constants.VENUE_ID, merchantId);
			
			//.setVisibility(View.VISIBLE);

			loadingDialog.show();
			
			getInvoice();
		} catch (Exception e) {
			(new CreateClientLogTask("GetCheck.onViewBillClick", "Exception Caught", "error", e)).execute();

		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	protected void getInvoice() {
		try {
			String token = getToken();
			if (token != null) {
				GetCheckTask getInvoiceTask = new GetCheckTask(token, merchantId, invoice.getText().toString(), getApplicationContext()) {
					@Override
					protected void onPostExecute(Void result) {
						try {
							super.onPostExecute(result);
							

							loadingDialog.hide();
							int errorCode = getErrorCode();

							
							if (getFinalSuccess() && errorCode == 0) {

								Check theBill = getTheBill();

								if (theBill == null || theBill.getItems().size() == 0) {
									toastShort("Could not locate your check");
									//.setVisibility(View.INVISIBLE);
									return;
								}else{
								
								     
									Intent viewCheck = new Intent(getApplicationContext(), ViewCheck.class);
									viewCheck.putExtra(Constants.INVOICE, theBill);
									startActivity(viewCheck);

									
								}

							} else {
								//Not Succes
								
								if (errorCode != 0){
									
									String errorMsg = "";
									
									if(errorCode == ErrorCodes.INVOICE_NOT_FOUND) {
						                errorMsg = "Can not find invoice.";
						            } else if(errorCode == ErrorCodes.INVOICE_CLOSED) {
						                errorMsg = "Invoice closed.";
						            }else if (errorCode == ErrorCodes.CHECK_IS_LOCKED){
						                errorMsg = "Invoice being access by your server.  Try again in a few minutes.";
						            } else if (errorCode == ErrorCodes.NETWORK_ERROR){
						                errorMsg = "Arc is having problems connecting to the internet.  Please check your connection and try again.  Thank you!";
						                
						            } else {
						                errorMsg = ErrorCodes.ARC_ERROR_MSG;
						            }
									
									
									
									toastShort(errorMsg);
									
								}else{
									toastShort("Error retreiving invoice");

								}
								//.setVisibility(View.INVISIBLE);

							}
						} catch (Exception e) {
							(new CreateClientLogTask("GetCheck.getInvoice.onPostExecute", "Exception Caught", "error", e)).execute();

						}
					}
				};
				getInvoiceTask.execute();
			} else {
				Logger.d("NO TOKEN - GET TOKEN AND THEN GET THE INVOICE NUMBER");
			}
		} catch (Exception e) {
			(new CreateClientLogTask("GetCheck.getInvoice", "Exception Caught", "error", e)).execute();

		}
	}
}
