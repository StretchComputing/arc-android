package com.arcmobileapp.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Check implements Serializable {

	private static final long serialVersionUID = 6406815055461255727L;
	private int mId;
    private String mStatus;
    private String mNumber;
    private String mTableNumber;
    private String mWaiterRef;
    private String mMerchantId;
    private Double mBaseAmount;
    private Double mTaxAmount;
    private String mDateCreated;
    private String mLastUpdated;
    private String mExpiration;
    private Double mAmountPaid;
    private ArrayList<LineItem> mItems;
    private ArrayList<Payments> mPayments;
        
    public Check(int id, String merchantId, String checkNumber, String tableNumber, String status, String waiterRef, Double baseAmount, Double taxAmount, String dateCreated, String lastUpdated, String expiration, Double amtPaid, ArrayList<LineItem> items, ArrayList<Payments> payments) {
    	setId(id);
    	setStatus(status);
    	setNumber(checkNumber);
    	setTableNumber(tableNumber);
    	setWaiterRef(waiterRef);
    	setMerchantId(merchantId);
    	setBaseAmount(baseAmount);
    	setTaxAmount(taxAmount);
    	setDateCreated(dateCreated);
    	setLastUpdated(lastUpdated);
    	setExpiration(expiration);
    	setAmountPaid(amtPaid);
    	setItems(items);
    	setPayments(payments);
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

	public String getNumber() {
		return mNumber;
	}

	public void setNumber(String number) {
		this.mNumber = number;
	}

	public String getTableNumber() {
		return mTableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.mTableNumber = tableNumber;
	}

	public String getWaiterRef() {
		return mWaiterRef;
	}

	public void setWaiterRef(String waiterRef) {
		this.mWaiterRef = waiterRef;
	}

	public String getMerchantId() {
		return mMerchantId;
	}

	public void setMerchantId(String merchantId) {
		this.mMerchantId = merchantId;
	}

	public Double getBaseAmount() {
		return mBaseAmount;
	}

	public void setBaseAmount(Double baseAmount) {
		this.mBaseAmount = baseAmount;
	}

	public Double getTaxAmount() {
		return mTaxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.mTaxAmount = taxAmount;
	}

	public String getDateCreated() {
		return mDateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.mDateCreated = dateCreated;
	}

	public String getLastUpdated() {
		return mLastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.mLastUpdated = lastUpdated;
	}

	public String getExpiration() {
		return mExpiration;
	}

	public void setExpiration(String expiration) {
		this.mExpiration = expiration;
	}

	public Double getAmountPaid() {
		return mAmountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.mAmountPaid = amountPaid;
	}

	public ArrayList<LineItem> getItems() {
		return mItems;
	}

	public void setItems(ArrayList<LineItem> items) {
		this.mItems = items;
	}
	
	public ArrayList<Payments> getPayments() {
		return mPayments;
	}

	public void setPayments(ArrayList<Payments> payments) {
		this.mPayments = payments;
	}
}
