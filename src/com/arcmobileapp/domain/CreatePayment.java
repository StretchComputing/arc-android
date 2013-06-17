package com.arcmobileapp.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class CreatePayment implements Serializable {

	private static final long serialVersionUID = -4273243568312405686L;

    private String mMerchantId;
    private String mCustomerId;
    private String mInvoiceId;
    
    private Double mTotalAmount;
    private Double mPayingAmount;
    private Double mGratuity;
    
    private String mAccount;
    private String mPIN;
    
    private String mExpiration;
    private String mType;
    private String mCardType;
    
    private Double mPercentPaid;
    
    private String mSplitType;
    private String mTipEntry;
    private String mPercentEntry;
    
    private ArrayList<Items> mItems;
    
    public class Items {
    	int mItemId;
    	double mPaymentAmount;
    	double mPercentPaid;
    	
    	public Items(int itemId, double paymentAmount, double percentPaid) {
    		setItemId(itemId);
    		setPaymentAmount(paymentAmount);
    		setPercentPaid(percentPaid);
    	}

		public int getItemId() {
			return mItemId;
		}

		public void setItemId(int itemId) {
			this.mItemId = itemId;
		}

		public double getPaymentAmount() {
			return mPaymentAmount;
		}

		public void setPaymentAmount(double paymentAmount) {
			this.mPaymentAmount = paymentAmount;
		}

		public double getPercentPaid() {
			return mPercentPaid;
		}

		public void setPercentPaid(double percentPaid) {
			this.mPercentPaid = percentPaid;
		}
    }
        
    public CreatePayment(String merchantId, String customerId, String invoiceId, Double total, Double paying, Double gratuity, String account, String type, String cardType, String expiration, String pin, Double percentPaid, String splitType, String tipEntry, String percentEntry, ArrayList<Items> items) {
    	setMerchantId(merchantId);
    	setCustomerId(customerId);
    	setInvoiceId(invoiceId);
    	
    	setTotalAmount(total);
    	setPayingAmount(paying);
    	setGratuity(gratuity);
    	
    	setAccount(account);
    	setType(type);
    	setCardType(cardType);
    	
    	setExpiration(expiration);
    	setPIN(pin);
    	
    	setSplitType(splitType);
    	
    	setPercentPaid(percentPaid);
    	setPercentEntry(percentEntry);
    	setTipEntry(tipEntry);
    	
    	
    	setPaidItems(items);
    }

	public String getMerchantId() {
		return mMerchantId;
	}

	public void setMerchantId(String merchantId) {
		this.mMerchantId = merchantId;
	}

	public String getCustomerId() {
		return mCustomerId;
	}

	public void setCustomerId(String customerId) {
		this.mCustomerId = customerId;
	}
	
	public String getInvoiceId() {
		return mInvoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.mInvoiceId = invoiceId;
	}
    
	public Double getTotalAmount() {
		return mTotalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.mTotalAmount = totalAmount;
	}
	
	public Double getPayingAmount() {
		return mPayingAmount;
	}

	public void setPayingAmount(Double payingAmount) {
		this.mPayingAmount = payingAmount;
	}
	
	public Double getGratuity() {
		return mGratuity;
	}

	public void setGratuity(Double gratuity) {
		this.mGratuity = gratuity;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}
	
	public String getCardType() {
		return mCardType;
	}

	public void setCardType(String cardType) {
		this.mCardType = cardType;
	}

	public String getAccount() {
		return mAccount;
	}

	public void setAccount(String account) {
		this.mAccount = account;
	}
	
	public void setTipEntry(String tipEntry) {
		this.mTipEntry = tipEntry;
	}
    
    public String getTipEntry() {
    	return this.mTipEntry;
    }

    public void setPercentEntry(String percentEntry) {
		this.mPercentEntry = percentEntry;
	}
    
    public String getPercentEntry() {
    	return this.mPercentEntry;
    }

    public void setPercentPaid(Double percentPaid) {
		this.mPercentPaid = percentPaid;
	}
    
    public Double getPercentPaid() {
    	return this.mPercentPaid;
    }

    public void setSplitType(String splitType) {
		this.mSplitType = splitType;
	}
    
    public String getSplitType() {
    	return this.mSplitType;
    }

    public void setPIN(String pin) {
		this.mPIN = pin;
	}
    
    public String getPIN() {
    	return this.mPIN;
    }

    public void setExpiration(String expiration) {
		this.mExpiration = expiration;
	}
    
    public String getExpiration() {
    	return this.mExpiration;
    }

	public ArrayList<Items> getItems() {
		return mItems;
	}

	public void setPaidItems(ArrayList<Items> items) {
		this.mItems = items;
	}
}
