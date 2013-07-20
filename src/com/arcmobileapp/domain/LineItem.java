package com.arcmobileapp.domain;

import java.io.Serializable;

public class LineItem implements Serializable {

	//"Items":[{"Id":19540,"POSkey":"60092","Amount":1.0,"Display":true,"Description":"Lobster Special","Value":45.0},{"Id":19541,"POSkey":"59956","Amount":1.0,"Display":true,"Description":"Pasta Diavlo","Value":34.0},{"Id":19542,"POSkey":"60089","Amount":1.0,"Display":true,"Description":"Brat Sandwich","Value":6.0},{"Id":19543,"POSkey":"59685","Amount":1.0,"Display":true,"Description":"Heineken Btl","Value":5.5},{"Id":19544,"POSkey":"59687","Amount":1.0,"Display":true,"Description":"Boddingtons Drft","Value":8.0},{"Id":19545,"POSkey":"59696","Amount":1.0,"Display":true,"Description":"1/2 Guinness","Value":6.5}],
	
	private static final long serialVersionUID = 5633010804691171264L;
	private int id;
    private String mPosKey;
    private Double mAmount;
    private Boolean mDisplay;
    private String mDescription;
    private Double mValue;
    private Boolean isSelected;
    private Double mMyPayment;
    private Double mPercent;
    private String mIsPaidFor;
    
    
    public LineItem(){
    	setId(0);
    	setPosKey("");
    	setAmount(0.0);
    	setDisplay(false);
    	setDescription("");
    	setValue(0.0);
    	setMyPayment(0.0);
    	setPercent(0.0);
    	setIsPaidFor("");
    }
    public LineItem(int id, String posKey, Double amount, Boolean display, String description, Double value) {
    	setId(id);
    	setPosKey(posKey);
    	setAmount(amount);
    	setDisplay(display);
    	setDescription(description);
    	setValue(value);
    	setMyPayment(0.0);
    	setPercent(0.0);
    	setIsPaidFor("");
    }
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIsPaidFor() {
		return mIsPaidFor;
	}

	public void setIsPaidFor(String paidFor) {
		this.mIsPaidFor = paidFor;
	}
	
	
	public String getPosKey() {
		return mPosKey;
	}

	public void setPosKey(String posKey) {
		this.mPosKey = posKey;
	}

	public Double getPercent() {
		return mPercent;
	}

	public void setPercent(Double amount) {
		this.mPercent = amount;
	}
	
	
	public Double getAmount() {
		return mAmount;
	}

	public void setAmount(Double amount) {
		this.mAmount = amount;
	}

	public Boolean getDisplay() {
		return mDisplay;
	}

	public void setDisplay(Boolean display) {
		this.mDisplay = display;
	}
	
	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean selected) {
		this.isSelected = selected;
	}
	
	public void setMyPayment(Double myPayment){
		this.mMyPayment = myPayment;
	}
	public double getMyPayment(){
		return mMyPayment;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		this.mDescription = description;
	}

	public Double getValue() {
		return mValue;
	}

	public void setValue(Double value) {
		this.mValue = value;
	}	
}
