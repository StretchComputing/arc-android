package com.arcmobileapp.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.ServerObject;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class GetServerTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<ServerObject> mServerList;
	private int mErrorCode;
	private String mToken;
	
	public GetServerTask(String token, Context context) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		performTask();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		performPostExec();
	}
	
	protected void performTask() {
		WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
		mResponse = webService.getServer(mToken);
		
		//Logger.d("RESPONSE FROM GET CURRENT SERVER: " + mResponse);
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
	
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				JSONObject results = json.getJSONObject(WebKeys.RESULTS);
				setNewUrl(results);
			

			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("GetServer.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving merchants, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("GetServer.performTask", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void setNewUrl(JSONObject results){
		
		try{
			String newServer = results.getString(WebKeys.SERVER);
			Boolean isSSL = false;
			
			try{
				isSSL = results.getBoolean(WebKeys.SSL);
			}catch(Exception e){
				
			}
			
			Logger.d("New Server: " + newServer);
			if (newServer != null && newServer.length() > 0){
				
				String scheme = "https";
                if(!isSSL){
                	scheme = "http";
                }
                
                String fullUrl = scheme + "://" + newServer + "/rest/v1/";
                
                
                Logger.d("FINAL FULL URL: " + fullUrl);
                
    			ArcPreferences myPrefs = new ArcPreferences(mContext);
    			myPrefs.putAndCommitString(Keys.DUTCH_URL, fullUrl);

			}
			
		}catch(JSONException e){
			(new CreateClientLogTask("GetServer.performTask", "Exception Caught", "error", e)).execute();

		}
		
		
	}

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<ServerObject> getServers(){
		return mServerList;
	}
	
	public Context getContext() {
		return mContext;
	}	
	
	public int getErrorCode(){
		return mErrorCode;
	}
	
	public Boolean getSuccess(){
		return mSuccess;
	}
}