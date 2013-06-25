package com.arcmobileapp.activities;

import java.util.ArrayList;

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
import com.arcmobileapp.domain.LineItem;
import com.arcmobileapp.domain.Payments;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.GetCheckTask;

public class GetCheck extends BaseActivity {

	private TextView title;
	private ProgressBar activityBar;
	private EditText invoice;
	private String venueName;
	private String merchantId;
	
	public GetCheck() {
		super();
	}

	public GetCheck(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_check);
		invoice = (EditText) findViewById(R.id.invoice);
		title = (TextView) findViewById(R.id.title);
		activityBar = (ProgressBar) findViewById(R.id.activityBar);
		activityBar.setVisibility(View.INVISIBLE);
		
		venueName = getIntent().getStringExtra(Constants.VENUE);
		merchantId = getIntent().getStringExtra(Constants.VENUE_ID);
		title.setText(venueName);
	}
	
	public void onViewBillClick(View v) {
		String checkNum = invoice.getText().toString();
		if(checkNum == null || checkNum.trim().length() == 0) {
			toastLong("Please enter your check number");
			return;
		}
		Intent viewCheck = new Intent(getApplicationContext(), ViewCheck.class);
		viewCheck.putExtra(Constants.VENUE, venueName);
		viewCheck.putExtra(Constants.CHECK_NUM, checkNum);
		viewCheck.putExtra(Constants.VENUE_ID, merchantId);
		
		activityBar.setVisibility(View.VISIBLE);

		getInvoice();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	protected void getInvoice() {
		String token = getToken();
		if (token != null) {
			GetCheckTask getInvoiceTask = new GetCheckTask(token, merchantId, invoice.getText().toString(), getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					

					if (getSuccess()) {

						Check theBill = getTheBill();

						if (theBill == null || theBill.getItems().size() == 0) {
							toastShort("Could not locate your check");
							activityBar.setVisibility(View.INVISIBLE);
							return;
						}else{
						
						     
							Intent viewCheck = new Intent(getApplicationContext(), ViewCheck.class);
							viewCheck.putExtra(Constants.INVOICE, theBill);
							startActivity(viewCheck);

							
						}
		
					} else {
						toastShort("Could not find your check");
						activityBar.setVisibility(View.INVISIBLE);

					}
				}
			};
			getInvoiceTask.execute();
		} else {
			Logger.d("NO TOKEN - GET TOKEN AND THEN GET THE INVOICE NUMBER");
		}
	}
}
