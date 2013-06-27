package com.arcmobileapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
//import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/*
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Logger;
*/

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



public class AdditionalTip extends BaseActivity {

	
	private Check theBill;
	private TextView textTotalPayment;
	
	private EditText myTipText;
	
	private RadioButton radioEightteen;
	private RadioButton radioTwenty;
	private RadioButton radioTwentyTwo;
    private RadioGroup radiogroup1;
    private boolean didChooseRadio;

	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_additional_tip);
		
		
		
		theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
		
		textTotalPayment = (TextView) findViewById(R.id.text_total_payment);
		myTipText = (EditText) findViewById(R.id.my_tip_text);

		radioEightteen = (RadioButton) findViewById(R.id.radio_eightteen);
		radioTwenty = (RadioButton) findViewById(R.id.radio_twenty);
		radioTwentyTwo = (RadioButton) findViewById(R.id.radio_twenty_two);


        radiogroup1 = (RadioGroup) findViewById(R.id.tip_radio_group);
		
		
		
		textTotalPayment.setText(String.format("$%.2f", theBill.getMyBasePayment()));
		
		if (theBill.getServiceCharge() == 0.0){
			
			Logger.d("Selecting TWENTY");
			radioTwenty.setChecked(true);
			
			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .20));
		}
		
		myTipText.addTextChangedListener(new TextWatcher()
		{
		 

		    @Override
		    public void onTextChanged( CharSequence s, int start, int before, int count)
		    {
		    	
		    	if (AdditionalTip.this.didChooseRadio){
		    		AdditionalTip.this.didChooseRadio = false;
		    	}else{
		    		AdditionalTip.this.radioEightteen.setChecked(false);
			    	AdditionalTip.this.radioTwenty.setChecked(false);
			    	AdditionalTip.this.radioTwentyTwo.setChecked(false);
		    	}
		    	

		    }

		    @Override
		    public void beforeTextChanged( CharSequence s, int start, int count, int after)
		    {}

		    @Override
		    public void afterTextChanged( final Editable s)
		    {
		    	


		    }
		});


	}

	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();

    	didChooseRadio = true;
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_eightteen:
	            if (checked)
	    			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .18));
	            break;
	        case R.id.radio_twenty:
	            if (checked)
	    			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .20));
	            break;
	        case R.id.radio_twenty_two:
	            if (checked)
	    			myTipText.setText(String.format("%.2f", theBill.getMyBasePayment() * .22));
	            break;
	    }
	}

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onContinueButtonClicked(View view) {
	    // Is the button now checked?
	   toastShort("Continuing");
	}
	
	
	
	
	

}
