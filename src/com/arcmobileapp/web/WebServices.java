package com.arcmobileapp.web;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.domain.CreateReview;
import com.arcmobileapp.domain.LineItem;
import com.arcmobileapp.utils.Logger;

public class WebServices {
	
	private DefaultHttpClient httpClient;
	private HttpPost httpPost;
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private String errorMsg = "";
	private String serverAddress;
	
	public WebServices() {
		setServer(URLs.DEV_SERVER);
		this.httpClient = new DefaultHttpClient();
	}	

	public WebServices(String server) {
		setServer(server);
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
	
	public void setServer(String server) {
		this.serverAddress = server;
	}
	
	public String getMerchants() {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.GET_MERCHANT_LIST;
			Logger.d("|arc-web-services|", "GET MERCHANTS URL  = " + url);
			
			JSONObject json = new JSONObject();
			//json.put("id","12");
			
			resp = this.getResponse(url, json.toString(), null);
			Logger.d("|arc-web-services|", "GET MERCHANTS RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	
	public String register(String login, String password, String firstName, String lastName) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.REGISTER;
			Logger.d("|arc-web-services|", "REGISTER URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.EMAIL,login);
			json.put(WebKeys.PASSWORD,password);
			
			json.put(WebKeys.FIRST_NAME,firstName);
			json.put(WebKeys.LAST_NAME,lastName);

			json.put(WebKeys.IS_GUEST, false);
			json.put(WebKeys.ACCEPT_TERMS, true);


			
			resp = this.getResponse(url, json.toString(), null);
			Logger.d("|arc-web-services|", "REGISTER RESP = " + resp);
			return resp;
		} catch (Exception e) {
			Logger.d("EXCEPTION IN REGISTER");

			setError(e.getMessage());
			return resp;
		}
	}
	
	public String updateCustomer(String login, String password, String token) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.UPDATE_CUSTOMER;
			Logger.d("|arc-web-services|", "REGISTER URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.EMAIL,login);
			json.put(WebKeys.PASSWORD,password);
			json.put(WebKeys.IS_GUEST, false);


			
			resp = this.getResponse(url, json.toString(), token);
			Logger.d("|arc-web-services|", "REGISTER RESP = " + resp);
			return resp;
		} catch (Exception e) {
			Logger.d("EXCEPTION IN REGISTER");

			setError(e.getMessage());
			return resp;
		}
	}
	
	
	public String confirmRegister(String ticketId) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.CONFIRM_REGISTER;
			Logger.d("|arc-web-services|", "CONFIRM REGISTER URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.TICKET_ID, ticketId);
			
			Logger.d("CONFIRM PAYMENT JSON =\n\n" + json.toString());
			
			resp = this.getResponse(url, json.toString(), null);
			Logger.d("|arc-web-services|", "CONFIRM REGISTER RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	
	
	public String getToken(String login, String password, boolean isGuest) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.GET_TOKEN;
			Logger.d("|arc-web-services|", "GET TOKEN URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.LOGIN,login);
			json.put(WebKeys.PASSWORD,password);
			json.put(WebKeys.IS_GUEST, isGuest);
			if (isGuest){
				json.put("GuestKey", "Forgetmenot00");
			}
			
			resp = this.getResponse(url, json.toString(), null);
			Logger.d("|arc-web-services|", "GET TOKEN RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	public String getCheck(String token, String merchantId, String invoiceNumber, String requestId) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.GET_CHECK;
			Logger.d("|arc-web-services|", "GET CHECK URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.MERCHANT_ID, merchantId);
			json.put(WebKeys.INVOICE_NUMBER, invoiceNumber);
			json.put(WebKeys.POS, true);
			
			if (requestId.length() > 0){
				json.put(WebKeys.REQUEST_ID, requestId);
				json.put(WebKeys.PROCESS, false);
			}else{
				json.put(WebKeys.PROCESS, true);

			}
			
			resp = this.getResponse(url, json.toString(), token);
			Logger.d("|arc-web-services|", "GET CHECK RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	public String createPayment(String token, CreatePayment newPayment) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.CREATE_PAYMENT;
			Logger.d("|arc-web-services|", "CREATE PAYMENT URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.INVOICE_AMOUNT, newPayment.getTotalAmount());
			json.put(WebKeys.AMOUNT, newPayment.getPayingAmount());
			json.put(WebKeys.GRATUITY, newPayment.getGratuity());
			json.put(WebKeys.FUND_SOURCE_ACCOUNT, newPayment.getAccount());
			json.put(WebKeys.MERCHANT_ID, newPayment.getMerchantId());
			json.put(WebKeys.INVOICE_ID, newPayment.getInvoiceId());
			json.put(WebKeys.CUSTOMER_ID, newPayment.getCustomerId());
			json.put(WebKeys.PIN, Integer.parseInt(newPayment.getPIN()));
			json.put(WebKeys.EXPIRATION, newPayment.getExpiration());
			json.put(WebKeys.TYPE, newPayment.getType());
			json.put(WebKeys.CARD_TYPE, newPayment.getCardType());
			json.put(WebKeys.SPLIT_TYPE, newPayment.getSplitType());
			json.put(WebKeys.AUTH_TOKEN, "");
			json.put(WebKeys.TAG, "");
			json.put(WebKeys.NOTES, "");
			
			ArrayList<JSONObject> myArrayList = new ArrayList<JSONObject>();
			
			for (int i = 0; i < newPayment.getItems().size(); i++){
				
				LineItem lineItem = newPayment.getItems().get(i);
				
				JSONObject itemJson = new JSONObject();
				
				itemJson.put("Percent", lineItem.getPercent());
				itemJson.put("Amount", lineItem.getAmount());
				itemJson.put("ItemId", lineItem.getId());
				
				myArrayList.add(itemJson);
				
				
			}
			
			JSONArray jsonArray = new JSONArray(myArrayList);
			
			json.put(WebKeys.ITEMS, jsonArray);
			
			String jsonString = json.toString();
			jsonString = jsonString.replace("\\", "");

			Logger.d("CREATE PAYMENT JSON =\n\n" + jsonString);
			
			resp = this.getResponse(url, jsonString, token);
			Logger.d("|arc-web-services|", "CREATE PAYMENT RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	public String confirmPayment(String token, String ticketId) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.CONFIRM_PAYMENT;
			Logger.d("|arc-web-services|", "CONFIRM PAYMENT URL  = " + url);
			
			JSONObject json = new JSONObject();
			json.put(WebKeys.TICKET_ID, ticketId);
			
			Logger.d("CONFIRM PAYMENT JSON =\n\n" + json.toString());
			
			resp = this.getResponse(url, json.toString(), token);
			Logger.d("|arc-web-services|", "CONFIRM PAYMENT RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	public String createReview(String token, CreateReview newReview) {
		String resp = "";
		try {

			String url = this.serverAddress + URLs.CREATE_REVIEW;
			Logger.d("|arc-web-services|", "CREATE REVIEW URL  = " + url);
			
			JSONObject json = new JSONObject();
			
			json.put(WebKeys.INVOICE_ID, newReview.getInvoiceId());
			json.put(WebKeys.CUSTOMER_ID, newReview.getCustomerId());
			json.put(WebKeys.PAYMENT_ID, newReview.getPaymentId());
			json.put(WebKeys.COMMENTS, newReview.getAdditionalComments());

			json.put(WebKeys.DRINKS, newReview.getReviewRating());
			json.put(WebKeys.FOOD, newReview.getReviewRating());
			json.put(WebKeys.PRICE, newReview.getReviewRating());
			json.put(WebKeys.SERVICE, newReview.getReviewRating());
			json.put(WebKeys.FOOD, newReview.getReviewRating());

		


			
			Logger.d("CREATE PAYMENT JSON =\n\n" + json.toString());
			
			resp = this.getResponse(url, json.toString(), token);
			Logger.d("|arc-web-services|", "CREATE PAYMENT RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
	
	
}
