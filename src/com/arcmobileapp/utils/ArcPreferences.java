package com.arcmobileapp.utils;


import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.web.URLs;

/**
 * A typed wrapper for the Preference Manager
 */
public class ArcPreferences {

    private static final String SHARED_PREFERENCE_KEY = "Arc_Preferences";
	private static final String APP_INSTALL_UNIQUE_ID = "APP_INSTALL_UNIQUE_ID";
	private static final String BASE_SEED = "BASE_SEED";
	private static final Integer NULL_RETURN_VAL = -99;

    private SharedPreferences settings;
    private Context context;
    private Editor editor;
    
    public ArcPreferences() {
        context = ArcMobileApp.getContext();
        settings = context.getSharedPreferences(ArcPreferences.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        editor = settings.edit();
    }
    
    public ArcPreferences(Context ctx) {
        context = ctx;
        settings = context.getSharedPreferences(ArcPreferences.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        editor = settings.edit();
    }
    
	// if it's never been setup, return the dev server
	public String getServer() {
				
		String storedServer = getString(Keys.DUTCH_URL);
		if (storedServer != null && storedServer.length() > 0){
			return storedServer;
		}else{
			return URLs.PROD_SERVER;
		}
	}

    /**
     * gets the shared preference key used to identify application settings.
     * @return the shared preference key
     */
    static public String getSharedKey() {
        return SHARED_PREFERENCE_KEY;
    }
    
    public String getBaseSeed() {
    	return settings.getString(BASE_SEED, null);
    }
    
    public void setBaseSeed(String seed) {
    	putAndCommitString(BASE_SEED, seed);
    }
    
    public boolean hasKey(String find) {
    	return settings.contains(find);
    }
    
    public String getString(String find) {
    	return settings.getString(find, null);
    }
    
    public int getInt(String find) {
    	return settings.getInt(find, NULL_RETURN_VAL);
    }
    
    public long getLong(String find) {
    	return settings.getLong(find, NULL_RETURN_VAL);
    }
    
    public float getFloat(String find) {
    	return settings.getLong(find, NULL_RETURN_VAL);
    }
    
    public boolean getBoolean(String find) {
    	return settings.getBoolean(find, false);
    }
    
    public boolean isNullReturnVal(Number number) {
    	return number.intValue() == NULL_RETURN_VAL;
    }

    public void removeKeyAndCommit(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void putAndCommitBoolean(String key, boolean bool) {
        editor.putBoolean(key, bool);
        editor.commit();
    }
    
    public void putAndCommitFloat(String key, float value){
    	editor.putFloat(key, value);
    	editor.commit();
    }

    public void putAndCommitInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putAndCommitString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putAndCommitLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }
    
    public String getAppInstallUniqueID() {
    	String uniqueId = settings.getString(APP_INSTALL_UNIQUE_ID, null);
    	if(uniqueId == null) {
    		uniqueId = UUID.randomUUID().toString();
    		putAndCommitString(APP_INSTALL_UNIQUE_ID, uniqueId);
    	}
		return uniqueId;
	}
}
