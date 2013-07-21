package com.arcmobileapp.web.rskybox;


import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.arcmobileapp.utils.Logger;

public class CreateClientLogTask extends AsyncTask<Void, Void, Void> {
	
	private String logName;
	private String logMessage;
	private String logLevel;
	private Exception exception;
	private String rskyboxResponse;
	
	public CreateClientLogTask(String theLogName, String theLogMessage, String theLogLevel, Exception theException) {
		super();
		this.logName = theLogName;
		this.logMessage = theLogMessage;
		this.logLevel = theLogLevel;
		this.exception = theException;
	}
	
	@Override
	// runs on UI thread
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		performTask();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void param) {
		super.onPostExecute(param);
		performPostExec();
	}
	
	// runs on a separate, background thread
	protected boolean performTask() {
		WebServices webService = new WebServices();
		this.rskyboxResponse = webService.createClientLog(this.logName, this.logMessage, this.logLevel, this.exception);
		Logger.d("rSkybox Create Client Log with logLevel = " + this.logLevel);
		Logger.d("rSkybox CREATE CLIENT LOG RESPONSE: " + this.rskyboxResponse);
		return true;
	}
	
	// runs on UI thread
	protected void performPostExec() {
		Logger.d("UI thread: Create Client Log Response: " + this.rskyboxResponse);
		if(this.rskyboxResponse == null) {
			return;
		}
		
		try {
			// TODO process rSkybox repsonse for possible commands -- simulated "push"
		} catch (/*JSON*/Exception exc) {
			Logger.e("Error retrieving invoice, JSON Exception: " + exc.getMessage());
		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
	}
	
	public String getResponse() {
		return this.rskyboxResponse;
	}
}