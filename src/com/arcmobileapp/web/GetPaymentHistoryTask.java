package com.arcmobileapp.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentHistoryObject;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class GetPaymentHistoryTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private String mToken;
	private String mCustomerId;

	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<PaymentHistoryObject> mPaymentList;
	private int mErrorCode;
	
	public GetPaymentHistoryTask(Context context, String token, String customerId) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
		mCustomerId = customerId;

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
		mResponse = webService.getPaymentHistory(mToken, mCustomerId);
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
	
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				parseJSON(json);
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("GetPaymentHistoryTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving payments, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("GetPaymentHistoryTask.performTask", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
		try {
			
			if (json.isNull(WebKeys.RESULTS)){
				return;
			}
			
			JSONArray results = json.getJSONArray(WebKeys.RESULTS);  // get an array of returned results
			Logger.d("PAYMENT HISTORY RESULTS****: " + results);
			mPaymentList = new ArrayList<PaymentHistoryObject>();

			for(int i = 0; i < results.length(); i++) {
				
				PaymentHistoryObject myPayment = new PaymentHistoryObject();
				
				JSONObject result = results.getJSONObject(i);
				
				myPayment.paymentId = result.getString(WebKeys.PAYMENT_ID);
				myPayment.merchantId = result.getString(WebKeys.MERCHANT_ID);
				myPayment.merchantName = result.getString(WebKeys.MERCHANT);

				myPayment.invoiceNumber = result.getString(WebKeys.INVOICE_NUMBER);
				myPayment.confirmationNumber = result.getString(WebKeys.CONFIRMATION);
				myPayment.cardNumber = result.getString(WebKeys.CARD);
				myPayment.invoiceStatus = result.getString(WebKeys.STATUS);
				myPayment.invoiceAmount = result.getDouble(WebKeys.AMOUNT);
				myPayment.gratuityAmount = result.getDouble(WebKeys.GRATUITY);
				myPayment.invoiceNotes = result.getString(WebKeys.NOTES);
				myPayment.invoiceDate = result.getString(WebKeys.DATE_CREATED);

				
				mPaymentList.add(myPayment);

				
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("GetPaymentHistoryTask.parseJson", "Exception Caught", "error", e)).execute();

		}
	}

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<PaymentHistoryObject> getPayemntList(){
		return mPaymentList;
	}
	
	public Context getContext() {
		return mContext;
	}	
	
	public int getErrorCode(){
		return mErrorCode;
	}
}