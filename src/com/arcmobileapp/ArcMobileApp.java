package com.arcmobileapp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;

import com.arcmobileapp.utils.DateTimeFormatter;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

public class ArcMobileApp extends Application {
	
	private static ArcMobileApp instance;
	protected static DateTimeFormatter DTFormatter;
	private static Context context;
	private static Typeface modernPics;

    public ArcMobileApp() {
        instance = this;
    }
    
    // Font loader, global view set
 	//http://stackoverflow.com/questions/9797872/use-roboto-font-for-earlier-devices
 	private void initFonts() {
 		modernPics = Typeface.createFromAsset(getAssets(), "fonts/modernpics.otf");  
 	}
 	
	public static Typeface getModernPicsTypeface() {
		return modernPics;
	}
    
    public static DateTimeFormatter getFormatter(){
    	if(DTFormatter == null) {
    		DTFormatter = new DateTimeFormatter();
    	}
        return DTFormatter;
    }
    
    private BroadcastReceiver br = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            DTFormatter = new DateTimeFormatter();
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        ArcMobileApp.context = getApplicationContext();
        initFonts();
        EasyTracker.getInstance().setContext(getApplicationContext()); // Initialize Google Analytics
        GoogleAnalytics.getInstance(getApplicationContext()).setDebug(true); // Force Enable analytics in Debug Build - Not Intended for Production Release
        
        DTFormatter = new DateTimeFormatter();
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(br, filter);
        //Thread.setDefaultUncaughtExceptionHandler(new AmazonUncaughtExceptionHandler(getApplicationContext()));
    }
    
    /**
     * accesses the current application context
     * @return the instance of the application
     */
    public static Context getContext() {
        return instance;
    }

    public static Context getAppContext() {
        return ArcMobileApp.context;
    }
    
    /**
     * REMOVE BEFORE PRODUCTION RELEASE!!
     * @return
     */
    public static boolean isDebug() {
    	return true;
    }

    public static String getVersion() {
    	try {
			return getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
    	return "failedRetrievingVersionNunber";
    }
}
