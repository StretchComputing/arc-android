package com.arcmobileapp.web;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;

public class MakePaymentTask extends AsyncTask<Void, Void, Void> {
	
	private String mToken;
	private CreatePayment mPayment;
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private String mResponseTicket;
	private Boolean finalSuccess;
	private int mPaymentId;

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
		try {

			JSONObject json =  new JSONObject(mResponse);

			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				mResponseTicket = json.getString(WebKeys.RESULTS);
				//6, 2, 2, 3, 4, 5, 6, 7, 8, 9, and 10
				if(!checkPaymentConfirmation(6000)) {
					if(!checkPaymentConfirmation(2000)) {
						if(!checkPaymentConfirmation(3000)) {
							if(!checkPaymentConfirmation(3000)) {
								if(!checkPaymentConfirmation(3000)) {
									if(!checkPaymentConfirmation(4000)) {
										if(!checkPaymentConfirmation(5000)) {
											if(!checkPaymentConfirmation(6000)) {
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
			}
		} catch (JSONException exc) {
			Logger.e("Error creating payment, JSON Exception: " + exc.getMessage());
		}
		
		
		return true;
	}
	
	protected boolean checkPaymentConfirmation(int sleep) {
		try{
			Thread.sleep(sleep);
			WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
			mResponse = webService.confirmPayment(mToken, getResponseTicket());
			try {
				JSONObject json =  new JSONObject(mResponse);
				JSONObject result = json.getJSONObject(WebKeys.RESULTS);
				mSuccess = json.getBoolean(WebKeys.SUCCESS);

				if (result == null){

					return false;
				}else{
					finalSuccess = true;
					mPaymentId = result.getInt(WebKeys.PAYMENT_ID);

					return true;
				}
				
			} catch (JSONException exc) {
				Logger.e("Error getting confirmation, JSON Exception: " + exc.getMessage());
			}
			
		}catch (Exception exc){}
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
}