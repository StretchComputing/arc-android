package com.arcmobileapp.web;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class MakePaymentTask extends AsyncTask<Void, Void, Void> {
	
	private String mToken;
	private CreatePayment mPayment;
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private String mResponseTicket;
	private Boolean finalSuccess;
	private int mPaymentId;
	private int mErrorCode;

	public MakePaymentTask(String token, CreatePayment payment, Context context) {
		super();
		mToken = token;
		mPayment = payment;
		mContext = context;
		mResponse = null;
		mSuccess = false;
		mResponseTicket = null;
		finalSuccess = false;
		mPaymentId = 0;
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
		WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
		mResponse = webService.createPayment(mToken, mPayment);
		
		if (mResponse == null){

			return false;
		}
		try {

			JSONObject json =  new JSONObject(mResponse);

			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				mResponseTicket = json.getString(WebKeys.RESULTS);
				//6, 2, 2, 3, 4, 5, 6, 7, 8, 9, and 10
				if(!checkPaymentConfirmation(6000) && mErrorCode == 0) {
					if(!checkPaymentConfirmation(2000) && mErrorCode == 0) {
						if(!checkPaymentConfirmation(3000) && mErrorCode == 0) {
							if(!checkPaymentConfirmation(3000) && mErrorCode == 0) {
								if(!checkPaymentConfirmation(3000) && mErrorCode == 0) {
									if(!checkPaymentConfirmation(4000) && mErrorCode == 0) {
										if(!checkPaymentConfirmation(5000) && mErrorCode == 0) {
											if(!checkPaymentConfirmation(6000) && mErrorCode == 0) {
												return false;
											}
										}
									}
								}
							}
						}
					}
				}
				// if we successfulyl got a response ticket, we need to query with the confirm 
				// call to know if it was successful or not
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

			Logger.e("Error creating payment, JSON Exception: " + e.getMessage());
		} catch (Exception e){
			(new CreateClientLogTask("MakePaymentTask.performTask", "Exception Caught", "error", e)).execute();

		}
		
		
		return true;
	}
	
	protected boolean checkPaymentConfirmation(int sleep) {
		try{
			Thread.sleep(sleep);
			WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
			mResponse = webService.confirmPayment(mToken, getResponseTicket());
			
			if (mResponse == null){

				return false;
			}
			
			
			try {
				JSONObject json =  new JSONObject(mResponse);
				
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

					return false;
				}
				mSuccess = json.getBoolean(WebKeys.SUCCESS);

				JSONObject result;

					try{
						result = json.getJSONObject(WebKeys.RESULTS);
					}catch(JSONException e){
						return false;
					}
					

					finalSuccess = true;
					mPaymentId = result.getInt(WebKeys.PAYMENT_ID);

					return true;
				
			} catch (JSONException e) {

				Logger.e("Error getting confirmation, JSON Exception: " + e.getMessage());
			}
			
		}catch (Exception e){
			(new CreateClientLogTask("MakePaymentTask.checkPaymentConfirmation", "Exception Caught", "error", e)).execute();

		}
		return false;
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
		
	}
	
	public Boolean getFinalSuccess(){
		return finalSuccess;
	}
	public String getResponse() {
		return mResponse;
	}
		
	public Boolean getSuccess() {
		return mSuccess;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public String getResponseTicket() {
		return mResponseTicket;
	}
	
	public int getPaymentId(){
		return mPaymentId;
	}
	
	public int getErrorCode(){
		return mErrorCode;
	}
}