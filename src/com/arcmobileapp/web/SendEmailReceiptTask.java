package com.arcmobileapp.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.MerchantObject;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class SendEmailReceiptTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private Boolean mSuccess;
	private String mToken;
	private String mTicketId;

	private Context mContext;
	private ArrayList<MerchantObject> mMerchantList;
	private int mErrorCode;
	
	public SendEmailReceiptTask(Context context, String token, String ticketId) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
		mTicketId = ticketId;
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
		mResponse = webService.sendEmailReceipt(mToken, mTicketId);
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
	
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				return;
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("SendEmailReceiptTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving merchants, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("SendEmailReceiptTask.performTask", "Exception Caught", "error", e)).execute();

		}
	}
	
	

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<MerchantObject> getMerchants(){
		return mMerchantList;
	}
	
	public Context getContext() {
		return mContext;
	}	
	
	public Boolean getSuccess(){
		return mSuccess;
	}
	public int getErrorCode(){
		return mErrorCode;
	}
}