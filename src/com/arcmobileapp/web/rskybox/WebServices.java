package com.arcmobileapp.web.rskybox;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.WebKeys;

public class WebServices {
	
	private DefaultHttpClient httpClient;
	private HttpPost httpPost;
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private String errorMsg = "";
	
	// rSkybox Credentials
	private static final String BASIC_AUTH_TOKEN = "ekokq167k46gbrmr6hvbht9lab";
	private static final String APPLICATION_ID = "ahRzfnJza3lib3gtc3RyZXRjaGNvbXITCxILQXBwbGljYXRpb24YgPYvDA";
	
	// URLs
	private static final String HTTPS_BASE_URL = "https://rskybox-stretchcom.appspot.com/";
	private static final String REST_BASE_URL = HTTPS_BASE_URL + "rest/v1/";
	private static final String APPLICATION_RESOURCE_URI = "applications";
	private static final String FEEDBACK_RESOURCE_URI = "feedback";
	private static final String CRASH_DETECT_RESOURCE_URI = "crashDetects";
	private static final String CLIENT_LOG_RESOURCE_URI = "clientLogs";

	
	public WebServices() {
		this.httpClient = new DefaultHttpClient();
	}	
	
	private String getResponse(String url, String json, String token) {
		StringBuilder reply = null;
		try {
			this.httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(url);

			if (json != null && json != "") {
				httpPost.setEntity(new ByteArrayEntity(json.getBytes("UTF8")));
				httpPost.setHeader("Content-type", "application/json");
				if(token!=null) {
					//httpPost.setHeader("Authorization", "Basic VEU5SFNVNWZWRmxRUlY5RFZWTlVUMDFGVWpwcWFXMXRlV1JoWjJobGNrQm5iV0ZwYkM1amIyMDZNVEV4TVE9PQ==");
					httpPost.setHeader("Authorization", "Basic " + token);
				}
			}

			httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpEntity.getContent()));
			String inputLine;

			reply = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				reply.append(inputLine);
			}
			in.close();

			httpEntity.consumeContent();
			httpClient.getConnectionManager().shutdown();
		} catch (IOException e) {
			Logger.e("|POST RESONSE ERROR| " + e.getMessage());
			setError(e.getMessage());
			return null;
		}

		return reply.toString();
	}


	public String getError() {
		if (this.errorMsg == null || this.errorMsg == "")
			this.errorMsg = "No Error.";

		return this.errorMsg;
	}

	private void setError(String error) {
		this.errorMsg = error;
	}
	
	
	public String createClientLog(String theLogName, String theLogMessage, String theLogLevel, Exception theException) {
		String resp = "";
		try {
	        String url = REST_BASE_URL + "applications/" + APPLICATION_ID + "/" + CLIENT_LOG_RESOURCE_URI;
			Logger.d("|arc-web-services|", "CREATE CLIENTLOG URL  = " + url);
			
	/*
		[tempDictionary setObject:logName  forKey:@"logName"];
		[tempDictionary setObject:logLevel forKey:@"logLevel"];
		
		if (exception) {
		    logMessage = [logMessage stringByAppendingFormat:@" - %@ - %@", [exception name], [exception description]];
		}
		[tempDictionary setObject:logMessage forKey:@"message"];
		
		[tempDictionary setObject:[rSkybox getUserId] forKey:@"userId"];
		
		//HardCoded at top of page
		[tempDictionary setObject:ARC_VERSION_NUMBER forKey:@"version"];
		
		++++++ MORE CODE NOT COPIED HERE
		
  "summary"        : "<summary_message>",    // Optional. Summary of the crash. OS version and other context.
  "logLevel"       : "<log_level>",          // Log level. Possible values: [debug, info, warn, error].
                                             // Default is 'error'.
  "logName"        : "<log_name>",           // Required. Unique log name assigned by client app code. Used to
                                             // thread log messages together and enable/disable log entry points.
  "message"        : "<message>",            // log message
  "stackBackTrace" : ["part1", "part2", ...] // stack back trace where each 'part' of the stack is passed in
                                             // as a string.
  "userId"      : "<user_id>",               // Optional. Unique ID for user that does not change over time.
  "userName"    : "<user_name>",             // Optional. User name of end user. Does not need to be unique or same over time.
                                             // No defined format but suggested to include a combination of the following 
                                             // fields if present: first name, last name, emailAddress, phoneNumber 
  "localEndpoint"  : "<local_endpoint>",     // Optional. Name of the local end point. If localEndpoint is specified,
                                             // then remoteEndpoint must also be specified.
  "remoteEndpoint" : "<remote_endpoint>",    // Optional. Name of the remote end point. If remoteEndpoint is specified,
                                             // then localEndpoint must also be specified.                                        
  "date"           : "<created_date>",       // Optional.  If provided, use ISO 8601 format YYYY-MM-DDThh:mm:ss.fffZ
                                             // If not provided, server will set createdDate to time this API request
                                             // is received.
  "appActions"  : [ {                        // FIFO queue of application actions with the most recent on the top
    "description" : "<description>",         // description of action.
    "timestamp"   : "<timestamp>",           // timestamp when action taken. Format: YYYY-MM-DDThh:mm:ss.fffZ
                                             // for backward compatibility, also support YYYY-MM-DD hh:mm:ss.SSS
    "duration"    : "<duration>",            // milli-seconds since last action. If first action, then -1.
  }, ...]
	 */
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.SUMMARY newPayment.getTotalAmount());
			
			resp = this.getResponse(url, json.toString(), BASIC_AUTH_TOKEN);
			Logger.d("|arc-web-services|", "GET MERCHANTS RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
}
