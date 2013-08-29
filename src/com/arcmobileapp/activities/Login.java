package com.arcmobileapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class Login extends BaseActivity {

	public String TAG = "Login";

	public Login() {
		super();
	}

	public Login(int titleRes) {
		super(titleRes);
	}

	private LinearLayout theView;
	private EditText usernameEt;
	private EditText passwordEt;
	private CheckBox rememberMeCb;
	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login);
			theView = (LinearLayout) findViewById(R.id.login_layout);
			theView.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.login_fade_in));

			usernameEt = (EditText) findViewById(R.id.username_et);
			passwordEt = (EditText) findViewById(R.id.password_et);
			rememberMeCb = (CheckBox) findViewById(R.id.remember_me_cb);
			usernameEt.setText("");
			passwordEt.setText("");
			rememberMeCb.setChecked(false);
			
			initCredentials();
			initActionBar();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Login.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	protected void initActionBar() {
		try {
			actionBar = getSupportActionBar();
			actionBar.setTitle(R.string.app_name);
			actionBar.setDisplayHomeAsUpEnabled(false);
		} catch (Exception e) {
			(new CreateClientLogTask("Login.initActionBar", "Exception Caught", "error", e)).execute();

		}
	}

	private void initCredentials() {
//		if (getBoolean(Constants.PREFS_REMEMBER_ME_KEY)) {
//			usernameEt.setText(getString(Constants.PREFS_UNAME_KEY));
//			passwordEt.setText(getString(Constants.PREFS_PASS_KEY));
//		}
	}

	private void saveCredentials() {
//		putString(Constants.PREFS_UNAME_KEY, usernameEt.getText().toString());
//		putString(Constants.PREFS_PASS_KEY, passwordEt.getText().toString());
//		putBoolean(Constants.PREFS_REMEMBER_ME_KEY, rememberMeCb.isChecked());
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.main_menu_version) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.version);
			builder.setMessage(R.string.version);
			//builder.setIcon(R.drawable.logo);
			builder.show();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void onLoginClick(View v) {
		saveCredentials();
		// startActivity(new Intent(getApplicationContext(), Setup.class));
		Intent goHome = new Intent(getApplicationContext(), Home.class);
		goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goHome);
		slideRightLeft();
		finish();
	}
}
