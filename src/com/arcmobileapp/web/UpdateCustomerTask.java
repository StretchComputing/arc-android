package com.arcmobileapp.web;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class UpdateCustomerTask extends AsyncTask<Void, Void, Void> {
	
	private String mLogin;
	private String mPassword;
	private boolean mIsGuest;
	private String mNewCustomerToken;
	private String mDevResponse;
	private boolean mSuccess;
	private boolean finalSuccess;
	private Context mContext;
	private int mErrorCode;
	
	public UpdateCustomerTask(String login, String password, Context context) {
		super();
		mLogin = login;
		mPassword = password;
		mIsGuest = false;
		mNewCustomerToken = "";
		mContext = context;
		mErrorCode = 0;
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
		
		
		WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
		
		ArcPreferences myPrefs = new ArcPreferences(mContext);
		String guestToken = myPrefs.getString(Keys.GUEST_TOKEN);
		
		Logger.d("SENDING IN GUEST TOKEN: " + guestToken);
		
		mDevResponse = webService.updateCustomer(mLogin, mPassword, guestToken);
		
		if (mDevResponse == null){

			return false;
		}
		try {
			Logger.d("*******UPDATE Response: " + mDevResponse);
			
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
				
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
			
			
		} catch (JSONException e) {
			(new CreateClientLogTask("UpdateCustomerTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving token, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("UpdateCustomerTask.performTask", "Exception Caught", "error", e)).execute();

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
	public int getErrorCode(){
		return mErrorCode;
	}
}