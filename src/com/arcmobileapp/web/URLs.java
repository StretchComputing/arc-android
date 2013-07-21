package com.arcmobileapp.web;


public final class URLs {
	
	public static final String PROD_SERVER = "https://arc.dagher.mobi";
	public static final String DUTCH_SERVER = "http://dev.dagher.mobi";
	//public static final String DUTCH_SERVER = "https://arc.dagher.mobi";

	//public static final String DEV_SERVER = "http://dtnetwork.asuscomm.com:8700/arc-dev";
	
	
	public static final String STAGING_SERVER = "http://stg.dagher.mobi";
	
	public static final String GET_MERCHANT_LIST = "/rest/v1/merchants/list";
	public static final String GET_TOKEN = "/rest/v1/customers/token";
	public static final String GET_CHECK = "/rest/v1/invoices/criteria";
	
	public static final String REGISTER = "/rest/v1/customers/create";
	public static final String CONFIRM_REGISTER = "/rest/v1/customers/register/confirm";

	
	public static final String UPDATE_CUSTOMER = "/rest/v1/customers/update/current";

	
	public static final String CREATE_PAYMENT = "/rest/v1/payments/create";
	public static final String CONFIRM_PAYMENT = "/rest/v1/payments/confirm";
	
	public static final String CREATE_REVIEW = "/rest/v1/reviews/new";

	public static String getHost(String theUrl) {
		if(theUrl == null) {return null;}
		
		int index = theUrl.indexOf("//");
		return theUrl.substring(index+2);
	}
}
