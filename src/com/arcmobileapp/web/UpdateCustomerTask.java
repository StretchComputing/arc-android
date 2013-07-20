package com.arcmobileapp.web;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;

public class UpdateCustomerTask extends AsyncTask<Void, Void, Void> {
	
	private String mLogin;
	private String mPassword;
	private boolean mIsGuest;
	private String mNewCustomerToken;
	private String mDevResponse;
	private boolean mSuccess;
	private boolean finalSuccess;
	private Context mContext;

	
	public UpdateCustomerTask(String login, String password, Context context) {
		super();
		mLogin = login;
		mPassword = password;
		mIsGuest = false;
		mNewCustomerToken = "";
		mContext = context;
		
	}
	
	@Override
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
	
	protected boolean performTask() {
		// get a token for the dev server
		
		
		WebServices webService = new WebServices(URLs.DEV_SERVER);
		
		ArcPreferences myPrefs = new ArcPreferences(mContext);
		String guestToken = myPrefs.getString(Keys.GUEST_TOKEN);
		
		Logger.d("SENDING IN GUEST TOKEN: " + guestToken);
		
		mDevResponse = webService.updateCustomer(mLogin, mPassword, guestToken);
		
		
		try {
			Logger.d("UPDATE Response: " + mDevResponse);
			JSONObject json =  new JSONObject(mDevResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
			
				mNewCustomerToken = json.getString(WebKeys.RESULTS);
				mSuccess = json.getBoolean(WebKeys.SUCCESS);

				if (mNewCustomerToken == null){

					return false;
				}else{
					Logger.d("RESULTS " + mNewCustomerToken);
					
					
					finalSuccess = true;
		

					return true;
				}
				
			}
			
			
		} catch (JSONException exc) {
			Logger.e("Error retrieving token, JSON Exception");
		}
		// get a token for the prod server
		//webService = new WebServices(URLs.PROD_SERVER);
		//mProdResponse = webService.getToken(mLogin, mPassword, mIsGuest);
		return true;
	}
	
	protected void performPostExec() {
		if(mDevResponse == null) { // || mProdResponse == null) {
			return;
		}
		
	
	}
	
	
	
	
	
	public String getDevResponse() {
		return mDevResponse;
	}
	
	
	public String getNewCustomerToken() {
		return mNewCustomerToken;
	}
	
	
	public Boolean getSuccess() {
		return mSuccess;
	}
	
	public Boolean getFinalSuccess(){
		return finalSuccess;
	}
	public Context getContext() {
		return mContext;
	}
}