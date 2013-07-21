package com.arcmobileapp.web;

public final class ErrorCodes {
	
	public static final int USER_ALREADY_EXISTS = 103;
	public static final int INCORRECT_PASSCODE = 105;
	public static final int INCORRECT_LOGIN_INFO = 106;
	public static final int INVOICE_CLOSED = 603;
	public static final int INVOICE_NOT_FOUND = 604;
	public static final int MERCHANT_CANNOT_ACCEPT_PAYMENT_TYPE = 400;
	public static final int OVER_PAID = 401;
	public static final int INVALID_AMOUNT = 402;

	public static final int CANNOT_PROCESS_PAYMENT = 500;
	public static final int CANNOT_TRANSFER_TO_SAME_ACCOUNT = 501;
	public static final int INVALID_ACCOUNT_PIN = 502;
	public static final int INSUFFICIENT_FUNDS = 503;

	public static final int PAYMENT_MAYBE_PROCESSED = 602;
	public static final int FAILED_TO_VALIDATE_CARD = 605;
	public static final int FIELD_FORMAT_ERROR = 606;
	public static final int INVALID_ACCOUNT_NUMBER = 607;
	public static final int CANNOT_GET_PAYMENT_AUTHORIZATION = 608;
	public static final int INVALID_EXPIRATION_DATE = 610;
	public static final int UNKOWN_ISIS_ERROR = 699;
	public static final int DUPLICATE_TRANSACTION = 612;

	//Micros
	public static final int CARD_ALREADY_PROCESSED = 628;
	public static final int CHECK_IS_LOCKED = 630;
	public static final int NO_AUTHORIZATION_PROVIDED = 631;


	public static final int NETWORK_ERROR_CONFIRM_PAYMENT = 998;
	public static final int NETWORK_ERROR = 999;
	public static final int MAX_RETRIES_EXCEEDED = 1000;
	
	public static final String ARC_ERROR_MSG = "Dutch is experiencing network issues, please try again.";


}
