package com.arcmobileapp.activities;

import com.arcmobileapp.R;
import com.arcmobileapp.R.layout;
import com.arcmobileapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TermsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terms, menu);
		return true;
	}

}
