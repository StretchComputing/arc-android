package com.arcmobileapp.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.ServerObject;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class GetDutchServersTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<ServerObject> mServerList;
	private int mErrorCode;
	private String mToken;
	
	public GetDutchServersTask(String token, Context context) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
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
		mResponse = webService.getDutchServers(mToken);
		
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
			(new CreateClientLogTask("GetDutchServers.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving merchants, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("GetDutchServers.performTask", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
		try {
			// GET MERCHANTS RESP = {"Success":true,"Results":[{"Id":12,"Name":"Isis Lab","Street":"111 Kidzie St.","City":"Chicago","State":"IL","Zipcode":"60654","Latitude":41.889456,"Longitude":-87.6317749999,"PaymentAccepted":"VNMADZ","TwitterHandler":"@IsisLab","GeoDistance":-1.0,"Status":"A","Accounts":[],"Cards":[]}],"ErrorCodes":[]}
			JSONArray results = json.getJSONArray(WebKeys.RESULTS);  // get an array of returned results
			//Logger.d("Results: " + results);
			mServerList = new ArrayList<ServerObject>();

			for(int i = 0; i < results.length(); i++) {
				
				ServerObject myServer = new ServerObject();
				
				JSONObject result = results.getJSONObject(i);
				String name = result.getString(WebKeys.NAME);
				String serverUrl = result.getString(WebKeys.URL);
				Integer serverId = result.getInt(WebKeys.ID);
			

				myServer.serverName = name;
				myServer.serverId = serverId;
				myServer.serverUrl = serverUrl;
				
				
				
				mServerList.add(myServer);
				
			}
		} catch (Exception e) {
			(new CreateClientLogTask("GetDutchServers.parseJson", "Exception Caught", "error", e)).execute();

		}
	}

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<ServerObject> getServers(){
		return mServerList;
	}
	
	public Context getContext() {
		return mContext;
	}	
	
	public int getErrorCode(){
		return mErrorCode;
	}
}
