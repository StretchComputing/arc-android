package com.arcmobileapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.arcmobileapp.BaseActivity;
import com.dutchmobileapp.R;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
import com.slidingmenu.lib.SlidingMenu;

public class FragmentChangeActivity extends BaseActivity {
	
	private Fragment mContent;
	
	public FragmentChangeActivity() {
		super(R.string.app_name);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			// set the Above View
			if (savedInstanceState != null)
				mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
			if (mContent == null) {
				mContent = new ColorFragment();
				Bundle args = new Bundle();
				args.putInt(ColorFragment.COLOR_KEY, R.color.red);
				mContent.setArguments(args);
			}
			
			// set the Above View
			setContentView(R.layout.content_frame);
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, mContent)
			.commit();
			
			// set the Behind View
			setBehindContentView(R.layout.menu_frame);
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.menu_frame, new ColorMenuFragment())
			.commit();
			
			// customize the SlidingMenu
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} catch (Exception e) {
			(new CreateClientLogTask("FragmentChangeActivity.onCreate", "Exception Caught", "error", e)).execute();

		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	public void switchContent(Fragment fragment) {
		try {
			mContent = fragment;
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, fragment)
			.commit();
			getSlidingMenu().showContent();
		} catch (Exception e) {
			(new CreateClientLogTask("FragmentChangeActivity.switchContent", "Exception Caught", "error", e)).execute();

		}
	}
}
