package com.arcmobileapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dutchmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class Splash extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.splash);
			
			final ImageView logo = (ImageView) findViewById(R.id.logo);
			logo.setAnimation(AnimationUtils.loadAnimation(this, R.anim.logo_animation));
			logo.getAnimation().setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
			      
					
				}
				
				public void onAnimationEnd(Animation animation) {
					
					try {
						logo.setVisibility(View.GONE);

						ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
						
						//If there is a guest token or customer token, go to HOME
						String guestToken = myPrefs.getString(Keys.GUEST_TOKEN);
						String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
						Boolean hasAgreed = myPrefs.getBoolean(Keys.AGREED_TERMS);

						if(guestToken == null && customerToken == null || !hasAgreed){
							//Go to initPage
							AppActions.add("Splash - No Tokens Found - Going to Init");

							startActivity(new Intent(getApplicationContext(), InitActivity.class));

						}else{
							//Go Home
							
							AppActions.add("Splash - Token Found - Going Home");

							startActivity(new Intent(getApplicationContext(), Home.class));

						}
						overridePendingTransition(0, 0);
						finish();
					} catch (Exception e) {
						(new CreateClientLogTask("Splash.onAnimationEnd", "Exception Caught", "error", e)).execute();

					}
				}
			});
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Splash.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

}
