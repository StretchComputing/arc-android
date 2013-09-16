package com.arcmobileapp;

import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.arcmobileapp.activities.Support;
import com.arcmobileapp.db.ArcProvider;
import com.arcmobileapp.fragments.MenuListFragment;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Enums;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.Utils;
import com.arcmobileapp.web.GetServerTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	// Before you build in release mode, make sure to adjust your proguard configuration by adding the following to proguard.cnf:
	//
	// -keep class io.card.**
	// -keepclassmembers class io.card.** {
	private static Integer activityCount = 0;

	private ActionBar actionBar;
	private ArcPreferences myPrefs;
	private int mTitleRes;
	protected ListFragment mFrag;
	private GoogleAnalytics mGoogleAnalyticsInstance;
	private Tracker mGoogleAnalyticsTracker;
	protected ContentProviderClient mProvider;
	public ContentResolver contentResolver;
	private CanvasTransformer mTransformer;

	
	public String TAG = "BaseActivity";


    
    
    
	public BaseActivity() {
		mTitleRes = R.string.app_name;
		
	}

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			
	        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

	        
	        
			 Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			        @Override
			        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {

						(new CreateClientLogTask("GLOBAL", "Exception Caught: " + paramThrowable.getMessage(), "error", null)).execute();

			        }
			    });
			 
			 
			setTitle(mTitleRes);
			// set the Behind View
			setBehindContentView(R.layout.menu_frame);
			if (savedInstanceState == null) {
				FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
				mFrag = new MenuListFragment();
				t.replace(R.id.menu_frame, mFrag);
				t.commit();
			} else {
				mFrag = (ListFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
			}

			initSlidingMenu();
			initActionBar();
			initContentProvider();

			myPrefs = new ArcPreferences(getApplicationContext());
			// theView = (LinearLayout) findViewById(R.id.login_layout);
			// theView.setAnimation(AnimationUtils.loadAnimation(this,
			// R.anim.login_fade_in));
			// initActionBar(getResources().getString(R.string.app_name), null);
		} catch (Exception e) {
			(new CreateClientLogTask("BaseActivity.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	protected Typeface getModernPicsTypeface() {
		return ArcMobileApp.getModernPicsTypeface();
	}

	protected TextView getModernPic(Enums.ModernPicTypes type) {
		try {
			TextView tv = new TextView(getApplicationContext());
			String symbol = Utils.convertModernPicType(type);
			tv.setText(symbol);
			tv.setTextSize(75);
			tv.setTypeface(getModernPicsTypeface());
			return tv;
		} catch (Exception e) {
			(new CreateClientLogTask("BaseActivity.getModerPic", "Exception Caught", "error", e)).execute();
			return null;
		}
	}

	private static Interpolator interp = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}
	};

	protected void initSlidingMenu() {
		try {
			mTransformer = new CanvasTransformer() {
				@Override
				public void transformCanvas(Canvas canvas, float percentOpen) {
					canvas.translate(0, canvas.getHeight() * (1 - interp.getInterpolation(percentOpen)));
				}
			};

			SlidingMenu sm = getSlidingMenu();
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setFadeDegree(0.35f);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			sm.setBehindScrollScale(0.0f);
			sm.setBehindCanvasTransformer(mTransformer);
			sm.setBackgroundColor(0xFF393939);
			
			sm.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
			    public void onOpened() {

			    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                            INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		           // ViewCheck.this.goAddTip();
		    }
		});
			// TODO add an image back there that is hidden when the menu covers it (or something cool..random quote, etc)
		} catch (Exception e) {
			(new CreateClientLogTask("BaseActivity.initSlidingMenu", "Exception Caught", "error", e)).execute();

		}
	}

	// https://github.com/jfeinstein10/SlidingMenu
	// customize the SlidingMenu
	protected void initActionBar() {
		try {
			actionBar = getSupportActionBar();
			//menuIcon.png(null);
			//setActionBarIcon(android.R.drawable.ic_menu_view);
			setActionBarIcon(R.drawable.menuicon);
			setActionBarTitle("");
			setActionBarHomeAsUpEnabled(false);

			actionBar.setHomeButtonEnabled(true);

			// actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00853c")));
		} catch (Exception e) {
			(new CreateClientLogTask("BaseActivity.initActionBar", "Exception Caught", "error", e)).execute();

		}
	}
	
	protected void initContentProvider() {
		contentResolver = getContentResolver();
		mProvider = contentResolver.acquireContentProviderClient(ArcProvider.CONTENT_URI);
	}

	public ContentProviderClient getContentProvider() {
		return mProvider;
	}

	public void releaseContentProvider() {
		if (mProvider != null) {
			mProvider.release();
		}
	}

	protected void setLayout(int resId) {
		setContentView(resId);
	}

	public String getServer() {
		return myPrefs.getServer();
	}

	public String getToken() {
		
		try{
			if (getString(Keys.CUSTOMER_TOKEN) != null && getString(Keys.CUSTOMER_TOKEN).length() > 0){
				return getString(Keys.CUSTOMER_TOKEN);
			}else{
				return getString(Keys.GUEST_TOKEN);
			}
		}catch(Exception e){
			(new CreateClientLogTask("BaseActivity.getToken", "Exception Caught", "error", e)).execute();

			return "";
		}
		
	}
	
	public String getId() {
		
		try{
			if (getString(Keys.CUSTOMER_ID) != null && getString(Keys.CUSTOMER_ID).length() > 0){
				return getString(Keys.CUSTOMER_ID);
			}else{
				return getString(Keys.GUEST_ID);
			}
		}catch(Exception e){
			(new CreateClientLogTask("BaseActivity.getId", "Exception Caught", "error", e)).execute();

			return "";
		}
		
	}	

	public boolean hasKey(String key) {
		return myPrefs.hasKey(key);
	}

	public String getString(String key) {
		return myPrefs.getString(key);
	}

	public void putString(String key, String value) {
		myPrefs.putAndCommitString(key, value);
	}

	public Boolean getBoolean(String key) {
		if (!hasKey(key))
			return null;
		return myPrefs.getBoolean(key);
	}

	public void putBoolean(String key, boolean value) {
		myPrefs.putAndCommitBoolean(key, value);
	}

	public Float getFloat(String key) {
		float returnVal = myPrefs.getFloat(key);
		if (myPrefs.isNullReturnVal(returnVal))
			return null;
		return returnVal;
	}

	public void putFloat(String key, float value) {
		myPrefs.putAndCommitFloat(key, value);
	}

	protected Integer getInteger(String key) {
		int returnVal = myPrefs.getInt(key);
		if (myPrefs.isNullReturnVal(returnVal))
			return null;
		return returnVal;
	}

	protected void putInteger(String key, int value) {
		myPrefs.putAndCommitInt(key, value);
	}

	protected Long getLong(String key) {
		long returnVal = myPrefs.getLong(key);
		if (myPrefs.isNullReturnVal(returnVal))
			return null;
		return returnVal;
	}

	protected void putLong(String key, long value) {
		myPrefs.putAndCommitLong(key, value);
	}

	protected void setActionBarTitle(int stringId) {
		actionBar.setTitle(stringId);
	}
	
	protected void setActionBarTitle(String title) {
		actionBar.setTitle(title);
	}

	protected void setActionBarIcon(int imageId) {
		actionBar.setIcon(imageId);
	}

	protected void setActionBarHomeAsUpEnabled(boolean enabled) {
		actionBar.setDisplayHomeAsUpEnabled(enabled);
	}

	protected void initActionBar(String title, Integer icon) {
		actionBar = getSupportActionBar();
		setActionBarTitle(R.string.app_name);
		if (icon != null)
			setActionBarIcon(icon.intValue());
		// setActionBarHomeAsUpEnabled(false);
	}

	protected void showOkDialog(String title, String message, Integer icon) {
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
		if (title != null) {
			builder.setTitle(title);
		}

		if (message != null) {
			builder.setMessage(message);
		}

		//builder.setIcon(R.drawable.logo);
		if (showOkButton()) {
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					okButtonClick();
				}
			});
		}
		if (showCancelButton()) {
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelButtonClick();
				}
			});
		}
		builder.create().show();
	}

	protected boolean showOkButton() {
		return true;
	}

	protected boolean showCancelButton() {
		return false;
	}

	protected void okButtonClick() {
		// toastShort("ok");
	}

	protected void cancelButtonClick() {
		toastShort("cancel");
	}

	private void toast(String message, int duration) {
		Toast.makeText(getApplicationContext(), message, duration).show();
		Toast.makeText(getApplicationContext(), message, duration).show();

	}

	protected void toastShort(String message) {
		
		try {
			toast(message, Toast.LENGTH_LONG);
			
	
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("BaseActivity.toastShort", "Exception Caught", "error", e)).execute();

		}
	}

	protected void toastLong(String message) {
		toast(message, Toast.LENGTH_LONG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	protected void slideLeftRight() {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	protected void slideRightLeft() {
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	protected void showChangeServerDialog() {
		/*
		final String[] servers = { "Dev", "Production" };
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Change server");
		int selectedIndex = -1;
		if (getString(Keys.SERVER) == URLs.DUTCH_SERVER) {
			selectedIndex = 0;
		} else if (getString(Keys.SERVER) == URLs.PROD_SERVER) {
			selectedIndex = 1;
		}
		alertDialog.setSingleChoiceItems(servers, selectedIndex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int itemSelected) {
				if (itemSelected == 0) {
					putString(Keys.SERVER, URLs.DUTCH_SERVER);
				} else if (itemSelected == 1) {
					putString(Keys.SERVER, URLs.PROD_SERVER);
				}
				toastShort("Now pointed to: " + getString(Keys.SERVER));
			}
		});
		AlertDialog alert = alertDialog.create();
		alert.show();
		*/
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			toggle();
			return true;
		} else if (itemId == R.id.whatIsArc) {
			AppActions.add("Help Menu - What is Dutch Selected");
			showOkDialog("What is dutch?", "Simply put, dutch speeds up the payment process.\n\n" + "1. Select your restaurant\n" + "2. Input your check number\n" + "3. Split and pay your portion!\n\n\n" + "We'll send a receipt to your email address.\n\n\n" + "Wasn't that easy?\n", null);
		} else if (itemId == R.id.feedback) {
			AppActions.add("Help Menu - Feedback Selected");
			if (!this.getClass().getSimpleName().equals("Support")){
				Intent about = (new Intent(getApplicationContext(), Support.class));
				startActivity(about);
			}
		}
//		else if (itemId == R.id.changeServer) {
//			showChangeServerDialog();
//		}
		return super.onOptionsItemSelected(item);
	}

	protected void trackEvent(String category, String action, String label, Long optional) {
		Logger.i(Constants.TRACKER_TAG, category + "->" + action + "->" + label + "->" + optional);
		EasyTracker.getTracker().trackEvent(category, action, label, optional);
	}

	private void setupAnalytics() {
		mGoogleAnalyticsInstance = GoogleAnalytics.getInstance(this);
		mGoogleAnalyticsInstance.setDebug(true);
		// EasyTracker needs a context before calls can be made.
		EasyTracker.getInstance().setContext(this);

		// Retrieve the tracker
		mGoogleAnalyticsTracker = EasyTracker.getTracker();
	}

	protected GoogleAnalytics getGoogleAnalyticsInstance() {
		return this.mGoogleAnalyticsInstance;
	}

	protected Tracker getTracker() {
		return this.mGoogleAnalyticsTracker;
	}

	@Override
	protected void onStart() {
	
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Google Analytics
	
		if (activityCount == 0){
			getCurrentServer();
		}
		activityCount++;

	}

	@Override
	protected void onStop() {
		
		activityCount--;
		
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Google Analytics
	

	}

	@Override
	protected void onResume() {
		super.onResume();
		setupAnalytics();
	  
	}

	@Override
	protected void onPause(){
		super.onPause();
	
	}
	
	
	@Override
	public void finish() {
		super.finish();
		slideLeftRight();
	}
	
	
	protected void getCurrentServer() {
		try {
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			
			if (customerToken != null && customerToken.length() > 0){
				
				GetServerTask getServerTask = new GetServerTask(customerToken, getApplicationContext()) {

					@Override
					protected void onPostExecute(Void result) {
						try {
							super.onPostExecute(result);
							int errorCode = getErrorCode();

							if (getSuccess()){

								AppActions.add("BaseActivity - Get Server Succeeded");

							}else{
								AppActions.add("EditServer - Get Servers Failed - Error Code:" + errorCode);
							}
						} catch (Exception e) {

							(new CreateClientLogTask("BaseActivity.getServer.onPostExecute", "Exception Caught", "error", e)).execute();
						}

					}
					
				};
				getServerTask.execute();
				
			}
		
		} catch (Exception e) {
			(new CreateClientLogTask("BaseActivity.getServer", "Exception Caught", "error", e)).execute();

		}
	}
	
}
