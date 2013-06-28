package com.arcmobileapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils.StringSplitter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentFlags;
import com.arcmobileapp.utils.Security;
import com.arcmobileapp.web.MakePaymentTask;
//import android.view.Menu;



public class Review extends BaseActivity {

	
	private EditText textAdditionalComments;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);
	

		
		textAdditionalComments = (EditText) findViewById(R.id.text_additional_comments);
		
		textAdditionalComments.setHint(R.string.review_hint);
		


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
    public void onSkipClicked(View view) {
		
	
    	Intent goBackHome = new Intent(getApplicationContext(), Home.class);
		goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goBackHome);
		
		
	}
    
    
    public void onSubmitClicked(View view) {
		
	
		
		
	}






}
