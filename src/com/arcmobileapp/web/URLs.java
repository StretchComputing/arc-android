package com.arcmobileapp.web;


public final class URLs {
	
	public static final String PROD_SERVER = "https://arc.dagher.mobi/rest/v1/";
	public static final String DEV_SERVER = "http://dev.dagher.mobi/rest/v1/";
	//public static final String DUTCH_SERVER = "https://arc.dagher.mobi";

	//public static final String DEV_SERVER = "http://dtnetwork.asuscomm.com:8700/arc-dev";
	
	
	public static final String STAGING_SERVER = "http://stg.dagher.mobi/rest/v1/";
	
	public static final String GET_MERCHANT_LIST = "merchants/list";
	public static final String GET_TOKEN = "customers/token";
	public static final String GET_CHECK = "invoices/criteria";
	
	public static final String REGISTER = "customers/create";
	public static final String CONFIRM_REGISTER = "customers/register/confirm";

	
	public static final String UPDATE_CUSTOMER = "customers/update/current";

	
	public static final String CREATE_PAYMENT = "payments/create";
	public static final String CONFIRM_PAYMENT = "payments/confirm";
	
	public static final String CREATE_REVIEW = "reviews/new";

	public static String getHost(String theUrl) {
		if(theUrl == null) {return null;}
		
		int index = theUrl.indexOf("//");
		int index2 = theUrl.indexOf("/rest/");
		String finalUrl = theUrl.substring(index+2);
		finalUrl = finalUrl.substring(0, index2 - 7);
		return finalUrl;
	}
}
