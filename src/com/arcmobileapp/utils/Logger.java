package com.arcmobileapp.utils;

import android.util.Log;

public final class Logger {
	
	private static final String DEFAULT_TAG = "ARC_MOBILE";
	private static final Boolean ShouldDebug = true;
	
	// PRINT DEBUG MESSAGES TO LOGCAT
	public static final void d(String tag, String message) {
		if (ShouldDebug){
			Log.d(tag, message);

		}
	}
	
	public static final void d(String message) {
		if (ShouldDebug){
			d(DEFAULT_TAG, message);
		}
	}
	
	// PRINT ERRORS TO LOGCAT
	public static final void e(String tag, String message) {
		if (ShouldDebug){
			Log.e(tag, message);
		}
	}
	
	public static final void e(String message) {
		if (ShouldDebug){
			e(DEFAULT_TAG, message);
		}
	}
	
	// PRINT INFO TO LOGCAT
	public static final void i(String tag, String message) {
		if (ShouldDebug){
			Log.i(tag, message);
		}
	}
	
	public static final void i(String message) {
		if (ShouldDebug){
			i(DEFAULT_TAG, message);
		}
	}
	
}
