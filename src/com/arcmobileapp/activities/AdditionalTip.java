package com.arcmobileapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;



public class AdditionalTip extends Activity {

	
	private Check theBill;
	private TextView textTotalPayment;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_additional_tip);
		
		
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
		
		textTotalPayment = (TextView) findViewById(R.id.text_total_payment);
		
		textTotalPayment.setText(String.format("$%.2f", theBill.getMyBasePayment()));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.additional_tip, menu);
		return true;
	}

}
