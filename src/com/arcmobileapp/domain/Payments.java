package com.arcmobileapp.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Payments implements Serializable {

	//"Payments":[{"PaymentId":27256,"Status":"PAID","Confirmation":"488301","Name":"Unknown","Amount":1.0,"Type":"POS","Tag":"1860929","Account":"XXXXXXXXXXXX9830","PaidItems":[]},{"PaymentId":27278,"CustomerId":1,"Status":"PAID","Confirmation":"488312","Name":"Jimmy D","Amount":1.0,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX9830","PaidItems":[]},{"PaymentId":27279,"CustomerId":1,"Status":"PAID","Confirmation":"488313","Name":"Jimmy D","Amount":0.5,"Gratuity":0.08,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27280,"CustomerId":5,"Status":"PAID","Confirmation":"488337","Name":"Joe W","Amount":1.55,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27281,"CustomerId":5,"Status":"PAID","Confirmation":"488338","Name":"Joe W","Amount":6.22,"Gratuity":1.21,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27283,"CustomerId":1,"Status":"PAID","Confirmation":"488340","Name":"Jimmy D","Amount":0.12,"Gratuity":0.02,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27284,"CustomerId":5,"Status":"PAID","Confirmation":"488341","Name":"Joe W","Amount":0.55,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27285,"CustomerId":1,"Status":"PAID","Confirmation":"488342","Name":"Jimmy D","Amount":1.0,"Gratuity":0.16,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27286,"CustomerId":5,"Status":"PAID","Confirmation":"488343","Name":"Joe W","Amount":0.59,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27305,"CustomerId":1,"Status":"PAID","Confirmation":"488344","Name":"Jimmy D","Amount":1.0,"Gratuity":0.16,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX4284","PaidItems":[]},{"PaymentId":27361,"CustomerId":1,"Status":"PAID","Confirmation":"488690","Name":"Jimmy D","Amount":6.22,"Gratuity":1.1,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX4284","PaidItems":[{"Amount":1,"ItemId":19543,"Percent":1.0}]},{"PaymentId":27371,"CustomerId":5205,"Status":"PAID","Confirmation":"489038","Name":"7830 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27372,"CustomerId":5206,"Status":"PAID","Confirmation":"489043","Name":"9669 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27373,"CustomerId":5207,"Status":"PAID","Confirmation":"489048","Name":"1744 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27374,"CustomerId":5208,"Status":"PAID","Confirmation":"489057","Name":"8410 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27376,"CustomerId":5210,"Status":"PAID","Confirmation":"489086","Name":"8859 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":""
	
	private static final long serialVersionUID = -4613105430942212783L;
	
	private int mId;
    private int mCustomerId;
    private String mStatus;
    private String mConfirmation;
    private String mCustomerName;
    private Double mAmount;
    private Double mGratuity;
    private String mType;
    private String mNotes;
    private String mAccount;
    private ArrayList<PaidItems> mPaidItems;
    
    public class PaidItems {
    	int mItemId;
    	double mPaymentAmount;
    	double mPercentPaid;
    	
    	public PaidItems(int itemId, double paymentAmount, double percentPaid) {
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
        
    public Payments(int id, int customerId, String customerName, String status, String confirmation, Double amount, Double gratuity, String type, String notes, String account, ArrayList<PaidItems> paidItems) {
    	setId(id);
    	setCustomerId(customerId);
    	setStatus(status);
    	setConfirmation(confirmation);
    	setCustomerName(customerName);
    	setAmount(amount);
    	setGratuity(gratuity);
    	setType(type);
    	setNotes(notes);
    	setAccount(account);
    	setPaidItems(paidItems);
    }
    
    public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}


	public Double getAmount() {
		return mAmount;
	}

	public void setAmount(Double amount) {
		this.mAmount = amount;
	}

	public int getCustomerId() {
		return mCustomerId;
	}

	public void setCustomerId(int customerId) {
		this.mCustomerId = customerId;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

	public String getConfirmation() {
		return mConfirmation;
	}

	public void setConfirmation(String confirmation) {
		this.mConfirmation = confirmation;
	}

	public String getCustomerName() {
		return mCustomerName;
	}

	public void setCustomerName(String customerName) {
		this.mCustomerName = customerName;
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

	public String getNotes() {
		return mNotes;
	}

	public void setNotes(String notes) {
		this.mNotes = notes;
	}

	public String getAccount() {
		return mAccount;
	}

	public void setAccount(String account) {
		this.mAccount = account;
	}

	public ArrayList<PaidItems> getPaidItems() {
		return mPaidItems;
	}

	public void setPaidItems(ArrayList<PaidItems> paidItems) {
		this.mPaidItems = paidItems;
	}
}
