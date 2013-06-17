package com.arcmobileapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.Enums.ModernPicTypes;
import com.arcmobileapp.utils.Utils;

public class Social extends BaseActivity {

	private LinearLayout theView;
	private TextView facebook;
	private TextView twitter;

	public Social() {
		super();
	}

	public Social(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social);
		theView = (LinearLayout) findViewById(R.id.social_layout);
		theView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.login_fade_in));
		facebook = (TextView) findViewById(R.id.facebook);
		facebook.setText(Utils.convertModernPicType(ModernPicTypes.FacebookBox));
		facebook.setTextSize(220);
		facebook.setTypeface(ArcMobileApp.getModernPicsTypeface());
		facebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				facebookClick();
			}
		});
		twitter = (TextView) findViewById(R.id.twitter);
		twitter.setText(Utils.convertModernPicType(ModernPicTypes.TwitterBox));
		twitter.setTextSize(220);
		twitter.setTypeface(ArcMobileApp.getModernPicsTypeface());
		twitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				twitterClick();
			}
		});
	}
	
	private void twitterClick() {
		String display = "\nHi twitter\n";
		showInfoDialog(display);
	}
	
	private void facebookClick() {
		String display = "\nHi facebook\n";
		showInfoDialog(display);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	
	private void showInfoDialog(String display) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Social.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setMessage(display);
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//hideSuccessMessage();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//hideSuccessMessage();
			}
		});
		builder.create().show();
	}
}
