package com.arcmobileapp.web;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class CreateUserTask extends AsyncTask<Void, Void, Void> {
	
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
	private String mResponseTicket;
	private Boolean finalSuccess;
	private String mFirstName;
	private String mLastName;
	private int mErrorCode;


	
	public CreateUserTask(String login, String password, String firstName, String lastName, boolean isGuest, Context context) {
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
		mResponseTicket = null;
		finalSuccess = false;
		mFirstName = firstName;
		mLastName = lastName;
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
		
		try{
			WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
			mDevResponse = webService.register(mLogin, mPassword, mFirstName, mLastName);
			
			if (mDevResponse == null){

				return false;
			}
		}catch (Exception e){
			return false;
		}
	
		
		try {
			Logger.d("Token Response: " + mDevResponse);
			JSONObject json =  new JSONObject(mDevResponse);
			
			

			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				//JSONObject result = json.getJSONObject(WebKeys.RESULTS);
				//mDevCustomerId = result.getString(WebKeys.ID);
				//mDevToken = result.getString(WebKeys.TOKEN);
				//String arcNumber = result.getString(WebKeys.ARC_NUMBER);  // do we need this?
				
				
				if (json.has(WebKeys.RESULTS)){
					mResponseTicket = json.getString(WebKeys.RESULTS);
					Logger.d("Sending with TICKET: " + mResponseTicket);
					//6, 2, 2, 3, 4, 5, 6, 7, 8, 9, and 10
					if(!checkRegisterConfirmation(2000) && mErrorCode == 0) {
						if(!checkRegisterConfirmation(2000) && mErrorCode == 0) {
							if(!checkRegisterConfirmation(3000) && mErrorCode == 0) {
								if(!checkRegisterConfirmation(3000) && mErrorCode == 0) {
									if(!checkRegisterConfirmation(3000) && mErrorCode == 0) {
										if(!checkRegisterConfirmation(4000) && mErrorCode == 0) {
											if(!checkRegisterConfirmation(5000) && mErrorCode == 0) {
												if(!checkRegisterConfirmation(6000) && mErrorCode == 0) {
													return false;
												}
											}
										}
									}
								}
							}
						}
					}
				}else{
					return false;
				}
				
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

					return false;
				}
			}

			
			
		} catch (JSONException e) {

			Logger.e("Error retrieving token, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("CreateUserTask.performTask", "Exception Caught", "error", e)).execute();

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
	
	
	protected boolean checkRegisterConfirmation(int sleep) {
		try{
			Thread.sleep(sleep);
			WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
			mDevResponse = webService.confirmRegister(mResponseTicket);
			
			if (mDevResponse == null){

				return false;
			}
			
			
			try {
				JSONObject json =  new JSONObject(mDevResponse);
				
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

					return false;
				}
				
				JSONObject result = json.getJSONObject(WebKeys.RESULTS);
				mSuccess = json.getBoolean(WebKeys.SUCCESS);

				if (result == null){

					return false;
				}else{
					Logger.d("RESULTS " + result);
					
					mDevCustomerId = result.getString(WebKeys.ID);
					mDevToken = result.getString(WebKeys.TOKEN);
					
					finalSuccess = true;
					//mPaymentId = result.getInt(WebKeys.PAYMENT_ID);
					//Store customer ID, customer email, customer token

					return true;
				}
				
			} catch (JSONException e) {
				Logger.e("Error getting confirmation, JSON Exception: " + e.getMessage());
			}
			
		}catch (Exception e){
			(new CreateClientLogTask("CreateUserTask.checkRegisterConfirmation", "Exception Caught", "error", e)).execute();

		}
		return false;
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