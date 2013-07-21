package com.arcmobileapp.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.domain.CreateReview;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class SubmitReviewTask extends AsyncTask<Void, Void, Void> {
	
	
	private CreateReview mReview;
	
	private String mToken;
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private String mResponseTicket;
	private Boolean finalSuccess;
	private int mErrorCode;

	public SubmitReviewTask(String token, CreateReview review, Context context) {
		super();
		mToken = token;
		mContext = context;
		mResponse = null;
		mSuccess = false;
		mResponseTicket = null;
		finalSuccess = false;
		mErrorCode = 0;
		mReview = review;
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
		mResponse = webService.createReview(mToken, mReview);
		
		if (mResponse == null){

			return false;
		}
		
		
		try {

			JSONObject json =  new JSONObject(mResponse);
			//JSONObject result = json.getJSONObject(WebKeys.RESULTS);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {

				finalSuccess = true;
				return true;
				
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("SubmitReview.performTask", "Exception Caught", "error", e)).execute();

			Logger.e("Error creating payment, JSON Exception: " + e.getMessage());
		}
		
		
		return true;
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
	public int getErrorCode(){
		return mErrorCode;
	}
}
