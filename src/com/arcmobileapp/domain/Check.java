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
    private Double mServiceCharge;
    private Double mDiscount;
    private String mDateCreated;
    private String mLastUpdated;
    private String mExpiration;
    private Double mAmountPaid;
    private Double myBasePayment;
    private Double myTip;
    
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
    	setServiceCharge(0.0);
    	setDiscount(0.0);
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
	
	public Double getServiceCharge() {
		return mServiceCharge;
	}

	public void setServiceCharge(Double serviceCharge) {
		this.mServiceCharge = serviceCharge;
	}
	
	public Double getDiscount() {
		return mDiscount;
	}

	public void setDiscount(Double discount) {
		this.mDiscount = discount;
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

	
	public Double getMyBasePayment() {
		return myBasePayment;
	}

	public void setMyBasePayment(Double basePayment) {
		this.myBasePayment = basePayment;
	}
	
	
	public Double getMyTip() {
		return myTip;
	}

	public void setMyTip(Double tip) {
		this.myTip = tip;
	}
	
	
	/*
	 @Override
	    public int describeContents()
	    {
	        // TODO Auto-generated method stub
	        return 0;
	    }
	 
	    @Override
	    public void writeToParcel(Parcel dest, int flag)
	    {
	        // TODO Auto-generated method stub
	        //dest.writeString(Name);
	        //dest.writeString(Address);
	        //dest.writeInt(Age);
	    	dest.writeInt(mId);
	    	dest.writeDouble(mBaseAmount);
	    	dest.writeDouble(mTaxAmount);
	    	dest.writeDouble(mAmountPaid);

	    	dest.writeString(mStatus);
	    	dest.writeString(mNumber);
	    	dest.writeString(mTableNumber);
	    	dest.writeString(mWaiterRef);
	    	dest.writeString(mMerchantId);
	    	dest.writeString(mLastUpdated);
	    	dest.writeString(mExpiration);
	    	dest.writeString(mDateCreated);
	    	dest.writeTypedList(mPayments);
	    	dest.writeTypedList(mItems);



	    
	        
	        
	    }
	    public Check(Parcel in)
	    {
	        //this.Name = in.readString();
	        //this.Address = in.readString();
	       // this.Age = in.readInt();
	    	this.mId = in.readInt();
	    	this.mBaseAmount = in.readDouble();
	    	this.mTaxAmount = in.readDouble();
	    	this.mAmountPaid = in.readDouble();
	    	
	    	this.mStatus = in.readString();
	    	this.mNumber = in.readString();
	    	this.mTableNumber = in.readString();
	    	this.mWaiterRef = in.readString();
	    	this.mMerchantId = in.readString();
	    	this.mLastUpdated = in.readString();
	    	this.mExpiration = in.readString();
	    	this.mDateCreated = in.readString();


	    	
	    	in.readTypedList(mPayments, Payments.Creator);
	    	in.readTypedList(mItems, LineItem.Creator);


	    }
	 
	    @SuppressWarnings("unchecked")
	    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	        public Check createFromParcel(Parcel in)
	        {
	            return new Check(in);
	        }
	 
	        public Check[] newArray(int size)
	        {
	            return new Check[size];
	        }
	    };
	    
	    */
	    
    
}
