package com.arcmobileapp.web.rskybox;


import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.Utils;

// App Actions Circular Queue
// --------------------------
// * Design: top and bottom must be persisted in SharedPreferences because the App Actions queue is designed to persist even after app terminated.
// * top = -1, bottom = -1  => queue is empty
// * top = 0, bottom = 0    => queue has single element
// * when adding to queue, bottom is incremented and if bottom == top, then top must advance too meaning oldest item just dropped off queue
// * for app actions, we only add to the queue, no explicit deletes
// * when returning all elements on the queue, queue is returned from top to bottom. Until queue start wrapping around, only initialized elements are returned.

public class AppActions {

    private static final String PREFERENCE_FILE_NAME = "rskyboxAppActions";
	private static final String QUEUE_TOP = "queueTop";
	private static final String QUEUE_BOTTOM = "queueBottom";
	private static final String ACTION_QUEUE_KEY = "appActionQueue_";
	private static final String ACTION_TIMESTAMP_QUEUE_KEY = "appActionTimestampQueue_";
	private static final Integer QUEUE_SIZE = 20;
	
	private static Integer queueTop = null;     // zero indexed -- indicates offset to the top of the AppAction queue (where first appAction stored)
	private static Integer queueBottom = null;  // zero indexed -- indicates offset to the bottom of the AppActions queue (where last action stored)
	private static Context context = null;
	private static SharedPreferences storedInfo = null;
	private static Editor editor = null;
	
	// Initialize common static variables used in other methods
	private static void init() {
        if(context == null) context = ArcMobileApp.getContext();
        if(storedInfo == null) storedInfo = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		if(editor == null) editor = storedInfo.edit();
        
        if(queueTop == null) {
        	// either app has been terminated or has never run before
        	queueTop = storedInfo.getInt(QUEUE_TOP, -2);
        	queueBottom = storedInfo.getInt(QUEUE_BOTTOM, -2);
        	if(queueTop == -2 || queueBottom == -2) {
        		// rskybox SharedPreferences have never been initialized for this application (app never ran before), so do it now
        		setupQueue();
        	}
        }
	}
    
	private static void setupQueue() {
    	queueTop = -1;    // -1 means queue is empty
    	queueBottom = -1; // -1 means queue is empty
		editor.putInt(QUEUE_BOTTOM, queueBottom);
		editor.putInt(QUEUE_TOP, queueTop);
		
		Boolean status = editor.commit();
		if(!status) {
			Logger.e("AppActions.setupQueue(): could not init queue");
		}
	}
	
	private static Integer modIncrement(Integer theOffset) {
		theOffset++;
		if(theOffset == QUEUE_SIZE) {
			theOffset = 0;
		}
		return theOffset;
	}
	
    public static void add(String theAppAction) {
    	AppActions.init();
    	
    	// TODO is the time zone needed? Is it GMT?
    	String timeStampStr = Utils.convertToIsoDate(new Date());
    	
    	//store the new appAction in the queue
    	if(queueTop == -1 || queueBottom == -1) {
    		// queue is empty -- special case
    		queueTop = 0;
    		queueBottom = 0;
    	} else {
    		queueBottom = modIncrement(queueBottom);
    		if(queueBottom == queueTop) modIncrement(queueTop);
    	}
    	editor.putString(ACTION_QUEUE_KEY+queueBottom, theAppAction);
    	editor.putString(ACTION_TIMESTAMP_QUEUE_KEY+queueBottom, timeStampStr);
		editor.putInt(QUEUE_BOTTOM, queueBottom);
		editor.putInt(QUEUE_TOP, queueTop);
    	
		Boolean status = editor.commit();
		if(!status) {
			Logger.e("AppActions.add(): could not add app Action to queue");
		}
    }
    
    // theActionQueue:    out parameter -- actions should be stored in this list
    // theTimestampQueue: out parameter -- timestamps should be stored in this list
    public static void getAll(List<String> theActionQueue, List<String> theTimestampQueue) {
    	AppActions.init();
    	
    	if(queueTop == -1 || queueBottom == -1) {
    		// queue is empty -- nothing to do
    	} else {
    		// queue has at least one appAction
    		Integer nextIndex = queueTop;
    		String nextAction = null;
    		String nextTimestamp = null;
    		while(true) {
    			nextAction = storedInfo.getString(ACTION_QUEUE_KEY+nextIndex, "");
    			nextTimestamp = storedInfo.getString(ACTION_TIMESTAMP_QUEUE_KEY+nextIndex, "");
    			if(nextAction.equals("") || nextTimestamp.equals("")) {
    				Logger.e("AppActions.getAppActions(): empty queue element encountered, should never happen, queue corrupt!");
    				break;
    			}
    			
    			theActionQueue.add(nextAction);
    			theTimestampQueue.add(nextTimestamp);
    			
    			// if we reached the bottom of the queue, stop
    			if(nextIndex == queueBottom) break;

    			nextIndex = modIncrement(nextIndex);
    		}
    	}
    	
    	return;
    }
}
