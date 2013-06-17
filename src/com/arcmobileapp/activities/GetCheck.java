package com.arcmobileapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.Constants;

public class GetCheck extends BaseActivity {

	private TextView title;
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
		startActivity(viewCheck);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
}
