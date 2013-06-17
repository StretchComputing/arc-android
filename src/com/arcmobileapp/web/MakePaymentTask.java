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
	
	public MakePaymentTask(String token, CreatePayment payment, Context context) {
		super();
		mToken = token;
		mPayment = payment;
		mContext = context;
		mResponse = null;
		mSuccess = false;
		mResponseTicket = null;
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
				if(!checkPaymentConfirmation(200)) {
					if(!checkPaymentConfirmation(1000)) {
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
		Logger.d("ATTEMPTING PAYMENT CONFIRMATION: " + sleep);
		try{
			Thread.sleep(sleep);
			WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
			mResponse = webService.confirmPayment(mToken, getResponseTicket());
			try {
				JSONObject json =  new JSONObject(mResponse);
				mSuccess = json.getBoolean(WebKeys.SUCCESS);
				if(mSuccess) {
					Logger.d("SUCCESSFULLY GOT THE PAYMENT CONFIRMATION");
					return true;
					
//						"Results": {
//						    "PaymentId": <Integer>,     // Id of the payment made by the customer
//						    "TipPaid": <double>,        // Amount of the Tip that was paid
//						    "AmountPaid": <double>,     // Amount of the Tip that was paid
//						    "InvoicePaid": <boolean>,   // Check if the invoice was completely paid or covered
//						    "Points":<single>,          // Points Awarded by the payment
//						    "Confirmation": <string>,  	// Payment Confirmation
//						    "DateCreated": <date> 	// Datestamp created by the Server
						 
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
}