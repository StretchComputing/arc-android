package com.arcmobileapp.domain;

import java.io.Serializable;

public class CreateReview implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mInvoiceId;
	private String mPaymentId;
	private Double mReviewRating;
	private String mCustomerId;
	private String mAdditionalComments;
	
	
	
	public CreateReview(String mInvoiceId, String mPaymentId,
			Double mReviewRating, String mCustomerId, String mAdditionalComments) {
		super();
		this.mInvoiceId = mInvoiceId;
		this.mPaymentId = mPaymentId;
		this.mReviewRating = mReviewRating;
		this.mCustomerId = mCustomerId;
		this.mAdditionalComments = mAdditionalComments;
	}
	
	public String getInvoiceId() {
		return mInvoiceId;
	}
	public void setInvoiceId(String mInvoiceId) {
		this.mInvoiceId = mInvoiceId;
	}
	public String getPaymentId() {
		return mPaymentId;
	}
	public void setPaymentId(String mPaymentId) {
		this.mPaymentId = mPaymentId;
	}
	public Double getReviewRating() {
		return mReviewRating;
	}
	public void setReviewRating(Double mReviewRating) {
		this.mReviewRating = mReviewRating;
	}
	public String getCustomerId() {
		return mCustomerId;
	}
	public void setCustomerId(String mCustomerId) {
		this.mCustomerId = mCustomerId;
	}
	public String getAdditionalComments() {
		return mAdditionalComments;
	}
	public void setAdditionalComments(String mAdditionalComments) {
		this.mAdditionalComments = mAdditionalComments;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
