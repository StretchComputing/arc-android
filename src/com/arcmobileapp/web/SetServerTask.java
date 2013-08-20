package com.arcmobileapp.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.ServerObject;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class SetServerTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<ServerObject> mServerList;
	private int mErrorCode;
	private String mToken;
	private String mCustomerId;
	private int mServerId;
	
	public SetServerTask(String token, String customerId, int serverId, Context context) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
		mCustomerId = customerId;
		mServerId = serverId;
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
		mResponse = webService.setDutchServer(mToken, mCustomerId, mServerId);
		
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
	
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				//parseJSON(json);
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("GetDutchServers.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving merchants, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("GetDutchServers.performTask", "Exception Caught", "error", e)).execute();

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

