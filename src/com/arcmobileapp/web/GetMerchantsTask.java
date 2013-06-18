package com.arcmobileapp.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;

public class GetMerchantsTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<String> mMerchantList;
	
	public GetMerchantsTask(Context context) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
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
		mResponse = webService.getMerchants();
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
			}
		} catch (JSONException exc) {
			Logger.e("Error retrieving merchants, JSON Exception");
		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
		// GET MERCHANTS RESP = {"Success":true,"Results":[{"Id":12,"Name":"Isis Lab","Street":"111 Kidzie St.","City":"Chicago","State":"IL","Zipcode":"60654","Latitude":41.889456,"Longitude":-87.6317749999,"PaymentAccepted":"VNMADZ","TwitterHandler":"@IsisLab","GeoDistance":-1.0,"Status":"A","Accounts":[],"Cards":[]}],"ErrorCodes":[]}
		JSONArray results = json.getJSONArray(WebKeys.RESULTS);  // get an array of returned results
		//Logger.d("Results: " + results);
		mMerchantList = new ArrayList<String>();

		for(int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			String name = result.getString(WebKeys.NAME);
			String street = result.getString(WebKeys.STREET);
			String city = result.getString(WebKeys.CITY);
			String state = result.getString(WebKeys.STATE);
			String zip = result.getString(WebKeys.ZIPCODE);
			double lat = result.getDouble(WebKeys.LATITUDE);
			double lon = result.getDouble(WebKeys.LONGITUDE);
			String paymentsAccepted = result.getString(WebKeys.PAYMENT_ACCEPTED);
			String twitterHandle = result.getString(WebKeys.TWITTER_HANDLE);
			String merchantId = result.getString(WebKeys.GET_MERCHANT_ID);
			double geoDistance = result.getDouble(WebKeys.GEO_DISTANCE);

			mMerchantList.add(name);
			
			Logger.d(name + " | " + merchantId + " | "  + street + " | " + city + " | " + state + " | " + zip + " | " + lat + " | " + lon + " | " + paymentsAccepted + " | " + twitterHandle + " | " + geoDistance);
		}
	}

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<String> getMerchants(){
		return mMerchantList;
	}
	
	public Context getContext() {
		return mContext;
	}	
}