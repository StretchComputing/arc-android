package com.arcmobileapp.track;


/**
 * a list of intent keys for calling intents and activities
 */
public class GoogleAnalyticsKeys {

    public static final String TRACKING_ID = "UA-40006118-1";
    
    public static final Long DEFAULT_OPT_VAL = null;
    
    public class Categories {
    	public static final String UI = "ui";
    	public static final String SYSTEM = "system";
    	public static final String SERVICES = "services";
    	public static final String METRICS = "metrics";
    }
    
    public class Actions {
    	// UI
    	public static final String UI_BUTTON = "uiButton";
    	public static final String UI_MENU_ACTIONBAR = "uiMenuActionbar";
    	public static final String UI_FIELD = "uiField";
    	public static final String UI_GFX = "uiGFX";
    	
    	// Metrics
    	public static final String METRICS = "metrics";
    }
    
    public class Labels {
    	public static final String HOME_SCAN_BTN_CLICK = "homeScanButtonClick";
    }
}
