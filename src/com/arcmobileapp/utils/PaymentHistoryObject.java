package com.arcmobileapp.utils;

import java.io.Serializable;

public class PaymentHistoryObject implements Serializable {

	private static final long serialVersionUID = 6406810554461255727L;

	public String paymentId;
	public String merchantId;
	public String merchantName;
	public String invoiceNumber;
	public String confirmationNumber;
	public String cardNumber;
	public String invoiceStatus;
	public double invoiceAmount;
	public double gratuityAmount;
	public String invoiceNotes;
	public String invoiceDate;


	
	public PaymentHistoryObject() {
		// TODO Auto-generated constructor stub
		 paymentId= "";
		 merchantId= "";
		 merchantName= "";
		 invoiceNumber= "";
		 confirmationNumber= "";
		 cardNumber= "";
		 invoiceStatus= "";
		invoiceAmount= 0.0;
		gratuityAmount= 0.0;
		 invoiceNotes= "";
		 invoiceDate= "";
	}

}
