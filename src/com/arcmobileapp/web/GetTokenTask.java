package com.arcmobileapp.web;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class GetTokenTask extends AsyncTask<Void, Void, Void> {
	
	private String mLogin;
	private String mPassword;
	private boolean mIsGuest;
	private String mDevCustomerId;
	private String mProdCustomerId;
	private String mDevToken;
	private String mProdToken;
	private Boolean mSuccess;
	private Context mContext;
	private String mDevResponse;
	private String mProdResponse;
	private int mErrorCode;
	
	public GetTokenTask(String login, String password, boolean isGuest, Context context) {
		super();
		mLogin = login;
		mPassword = password;
		mIsGuest = isGuest;
		mDevCustomerId = null;
		mProdCustomerId = null;
		mDevToken = null;
		mProdToken = null;
		mSuccess = false;
		mContext = context;
		mDevResponse = null;
		mProdResponse = null;
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
		WebServices webService = new WebServices(URLs.DUTCH_SERVER);
		mDevResponse = webService.getToken(mLogin, mPassword, mIsGuest);
		
		if (mDevResponse == null){

			return false;
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
		
		try {
			Logger.d("Token Response: " + mDevResponse);
			JSONObject json =  new JSONObject(mDevResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				JSONObject result = json.getJSONObject(WebKeys.RESULTS);
				mDevCustomerId = result.getString(WebKeys.ID);
				mDevToken = result.getString(WebKeys.TOKEN);
				//String arcNumber = result.getString(WebKeys.ARC_NUMBER);  // do we need this?
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
			
//			json =  new JSONObject(mProdResponse);
//			mSuccess = json.getBoolean(WebKeys.SUCCESS);
//			if(mSuccess) {
//				JSONObject result = json.getJSONObject(WebKeys.RESULTS);
//				mProdCustomerId = result.getString(WebKeys.ID);
//				mProdToken = result.getString(WebKeys.TOKEN);
//				//String arcNumber = result.getString(WebKeys.ARC_NUMBER);  // do we need this?
//			}
			
			
		} catch (JSONException e) {
			(new CreateClientLogTask("GetTokenTask.performTask", "Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving token, JSON Exception");
		}
	}
	
	public String getDevResponse() {
		return mDevResponse;
	}
	
	public String getProdResponse() {
		return mProdResponse;
	}
	
	public String getDevCustomerId() {
		return mDevCustomerId;
	}
	
	public String getProdCustomerId() {
		return mProdCustomerId;
	}
	
	public String getDevToken() {
		return mDevToken;
	}
	
	public String getProdToken() {
		return mProdToken;
	}
	
	public Boolean getSuccess() {
		return mSuccess;
	}
	
	public Context getContext() {
		return mContext;
	}
	public int getErrorCode(){
		return mErrorCode;
	}
}