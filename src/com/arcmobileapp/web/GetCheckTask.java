package com.arcmobileapp.web;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.LineItem;
import com.arcmobileapp.domain.Payments;
import com.arcmobileapp.domain.Payments.PaidItems;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Logger;

public class GetCheckTask extends AsyncTask<Void, Void, Void> {
	
	private String mToken;
	private String mMerchantId;
	private String mResponse;
	private String mInvoiceNumber;
	private Boolean mSuccess;
	private Context mContext;
	private Check theBill;
	
	public GetCheckTask(String token, String merchantId, String invoiceNumber, Context context) {
		super();
		mToken = token;
		mMerchantId = merchantId;
		mInvoiceNumber = invoiceNumber;
		mContext = context;
		mResponse = null;
		mSuccess = false;
		theBill = null;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		performTask();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void param) {
		super.onPostExecute(param);
		performPostExec();
	}
	
	protected boolean performTask() {
		WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
		mResponse = webService.getCheck(mToken, mMerchantId, mInvoiceNumber);
		// TODO - check if the the invoice is ready or if it needs called again
//		try {
//			JSONObject json =  new JSONObject(mResponse);
//			mSuccess = json.getBoolean(WebKeys.SUCCESS);
//			if(mSuccess) {
//				
//				parseJSON(json);
//			}
		
		
		
		return true;
	}
	
	protected void performPostExec() {
		
		Logger.d("Response: " + mResponse);
		if(mResponse == null) {
			return;
		}
		
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				parseJSON(json);
			}
		} catch (JSONException exc) {
			Logger.e("Error retrieving invoice, JSON Exception: " + exc.getMessage());
		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
		//GET INVOICE RESP = {"Success":true,"Results":{"Id":8447,"Status":"INVOICE_PAID_PARTIAL","Number":"350033","TableNumber":"HT1","WaiterRef":"88","MerchantId":12,"BaseAmount":105.0,"Tax":13.65,"DateCreated":"2013-04-20T14:44:58.203","LastUpdated":"2013-04-26T23:34:33.033","Expiration":"2013-04-20T15:44:57.547","AmountPaid":37.75,"Items":[{"Id":19540,"POSkey":"60092","Amount":1.0,"Display":true,"Description":"Lobster Special","Value":45.0},{"Id":19541,"POSkey":"59956","Amount":1.0,"Display":true,"Description":"Pasta Diavlo","Value":34.0},{"Id":19542,"POSkey":"60089","Amount":1.0,"Display":true,"Description":"Brat Sandwich","Value":6.0},{"Id":19543,"POSkey":"59685","Amount":1.0,"Display":true,"Description":"Heineken Btl","Value":5.5},{"Id":19544,"POSkey":"59687","Amount":1.0,"Display":true,"Description":"Boddingtons Drft","Value":8.0},{"Id":19545,"POSkey":"59696","Amount":1.0,"Display":true,"Description":"1/2 Guinness","Value":6.5}],"Tags":[],"Payments":[{"PaymentId":27256,"Status":"PAID","Confirmation":"488301","Name":"Unknown","Amount":1.0,"Type":"POS","Tag":"1860929","Account":"XXXXXXXXXXXX9830","PaidItems":[]},{"PaymentId":27278,"CustomerId":1,"Status":"PAID","Confirmation":"488312","Name":"Jimmy D","Amount":1.0,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX9830","PaidItems":[]},{"PaymentId":27279,"CustomerId":1,"Status":"PAID","Confirmation":"488313","Name":"Jimmy D","Amount":0.5,"Gratuity":0.08,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27280,"CustomerId":5,"Status":"PAID","Confirmation":"488337","Name":"Joe W","Amount":1.55,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27281,"CustomerId":5,"Status":"PAID","Confirmation":"488338","Name":"Joe W","Amount":6.22,"Gratuity":1.21,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27283,"CustomerId":1,"Status":"PAID","Confirmation":"488340","Name":"Jimmy D","Amount":0.12,"Gratuity":0.02,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27284,"CustomerId":5,"Status":"PAID","Confirmation":"488341","Name":"Joe W","Amount":0.55,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27285,"CustomerId":1,"Status":"PAID","Confirmation":"488342","Name":"Jimmy D","Amount":1.0,"Gratuity":0.16,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27286,"CustomerId":5,"Status":"PAID","Confirmation":"488343","Name":"Joe W","Amount":0.59,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27305,"CustomerId":1,"Status":"PAID","Confirmation":"488344","Name":"Jimmy D","Amount":1.0,"Gratuity":0.16,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX4284","PaidItems":[]},{"PaymentId":27361,"CustomerId":1,"Status":"PAID","Confirmation":"488690","Name":"Jimmy D","Amount":6.22,"Gratuity":1.1,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX4284","PaidItems":[{"Amount":1,"ItemId":19543,"Percent":1.0}]},{"PaymentId":27371,"CustomerId":5205,"Status":"PAID","Confirmation":"489038","Name":"7830 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27372,"CustomerId":5206,"Status":"PAID","Confirmation":"489043","Name":"9669 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27373,"CustomerId":5207,"Status":"PAID","Confirmation":"489048","Name":"1744 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27374,"CustomerId":5208,"Status":"PAID","Confirmation":"489057","Name":"8410 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27376,"CustomerId":5210,"Status":"PAID","Confirmation":"489086","Name":"8859 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":""
		JSONObject result = json.getJSONObject(WebKeys.RESULTS);
		
		double amountPaid = 0.0;
		double discount = 0.0;
		double serviceCharge = 0.0;
		
		try{
			amountPaid = result.getDouble(WebKeys.AMOUNT_PAID);
		}catch(Exception e){
		}
		
		try{
			discount = result.getDouble(WebKeys.DISCOUNT);
		}catch(Exception e){
		}
		
		try{
			serviceCharge = result.getDouble(WebKeys.SERVICE_CHARGE);
		}catch(Exception e){
		}
		
		theBill = new Check(result.getInt(WebKeys.ID), result.getString(WebKeys.MERCHANT_ID), result.getString(WebKeys.INVOICE_NUMBER), result.getString(WebKeys.TABLE_NUMBER), result.getString(WebKeys.STATUS), result.getString(WebKeys.WAITER_REF), result.getDouble(WebKeys.BASE_AMOUNT), result.getDouble(WebKeys.TAX), result.getString(WebKeys.DATE_CREATED), result.getString(WebKeys.LAST_UPDATED), result.getString(WebKeys.EXPIRATION), amountPaid, null, null);
		theBill.setDiscount(discount);
		theBill.setServiceCharge(serviceCharge);
		
		ArrayList<LineItem> lineItems = new ArrayList<LineItem>();
		JSONArray items = result.getJSONArray(WebKeys.ITEMS);  // get an array of returned results
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			LineItem lineItem = new LineItem(item.getInt(WebKeys.ID), item.getString(WebKeys.POS_KEY), item.getDouble(WebKeys.AMOUNT), item.getBoolean(WebKeys.DISPLAY), item.getString(WebKeys.DESCRIPTION), item.getDouble(WebKeys.VALUE));
			lineItems.add(lineItem);
			
			Logger.d(lineItem.getDescription() + " | " + lineItem.getValue());
		}
		theBill.setItems(lineItems);
		
		//"Payments":[{"PaymentId":27256,"Status":"PAID","Confirmation":"488301","Name":"Unknown","Amount":1.0,"Type":"POS","Tag":"1860929","Account":"XXXXXXXXXXXX9830","PaidItems":[]},{"PaymentId":27278,"CustomerId":1,"Status":"PAID","Confirmation":"488312","Name":"Jimmy D","Amount":1.0,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX9830","PaidItems":[]},{"PaymentId":27279,"CustomerId":1,"Status":"PAID","Confirmation":"488313","Name":"Jimmy D","Amount":0.5,"Gratuity":0.08,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27280,"CustomerId":5,"Status":"PAID","Confirmation":"488337","Name":"Joe W","Amount":1.55,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27281,"CustomerId":5,"Status":"PAID","Confirmation":"488338","Name":"Joe W","Amount":6.22,"Gratuity":1.21,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27283,"CustomerId":1,"Status":"PAID","Confirmation":"488340","Name":"Jimmy D","Amount":0.12,"Gratuity":0.02,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27284,"CustomerId":5,"Status":"PAID","Confirmation":"488341","Name":"Joe W","Amount":0.55,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27285,"CustomerId":1,"Status":"PAID","Confirmation":"488342","Name":"Jimmy D","Amount":1.0,"Gratuity":0.16,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX1111","PaidItems":[]},{"PaymentId":27286,"CustomerId":5,"Status":"PAID","Confirmation":"488343","Name":"Joe W","Amount":0.59,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX2677","PaidItems":[]},{"PaymentId":27305,"CustomerId":1,"Status":"PAID","Confirmation":"488344","Name":"Jimmy D","Amount":1.0,"Gratuity":0.16,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX4284","PaidItems":[]},{"PaymentId":27361,"CustomerId":1,"Status":"PAID","Confirmation":"488690","Name":"Jimmy D","Amount":6.22,"Gratuity":1.1,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX4284","PaidItems":[{"Amount":1,"ItemId":19543,"Percent":1.0}]},{"PaymentId":27371,"CustomerId":5205,"Status":"PAID","Confirmation":"489038","Name":"7830 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27372,"CustomerId":5206,"Status":"PAID","Confirmation":"489043","Name":"9669 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27373,"CustomerId":5207,"Status":"PAID","Confirmation":"489048","Name":"1744 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27374,"CustomerId":5208,"Status":"PAID","Confirmation":"489057","Name":"8410 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":"","Account":"XXXXXXXXXXX8106","PaidItems":[]},{"PaymentId":27376,"CustomerId":5210,"Status":"PAID","Confirmation":"489086","Name":"8859 G","Amount":1.0,"Gratuity":0.22,"Type":"CREDIT","Tag":"","Notes":""
		ArrayList<Payments> payments = new ArrayList<Payments>();
		
		JSONArray paymentsArray = result.getJSONArray(WebKeys.PAYMENTS);  // get an array of returned results
		for(int i = 0; i < paymentsArray.length(); i++) {
			
			JSONObject paymentItem = paymentsArray.getJSONObject(i);
			
			Payments payment = new Payments(paymentItem.getInt(WebKeys.PAYMENT_ID), 0, paymentItem.getString(WebKeys.NAME), paymentItem.getString(WebKeys.STATUS), paymentItem.getString(WebKeys.CONFIRMATION), paymentItem.getDouble(WebKeys.AMOUNT), null, paymentItem.getString(WebKeys.TYPE), null, paymentItem.getString(WebKeys.ACCOUNT), null);
			if(paymentItem.has(WebKeys.CUSTOMER_ID)) {
				payment.setCustomerId(paymentItem.getInt(WebKeys.CUSTOMER_ID));
			}
			
			if(paymentItem.has(WebKeys.GRATUITY)) {
				payment.setGratuity(paymentItem.getDouble(WebKeys.GRATUITY));
			}
			
			if(paymentItem.has(WebKeys.NOTES)) {
				payment.setNotes(paymentItem.getString(WebKeys.NOTES));
			}
			
			ArrayList<PaidItems> paidItems = new ArrayList<Payments.PaidItems>();
			JSONArray paidItemsArray = paymentItem.getJSONArray(WebKeys.PAID_ITEMS);
			for(int j = 0; j < paidItemsArray.length(); j++) {
				JSONObject paidItemObject = paidItemsArray.getJSONObject(j);
				PaidItems paidItem = payment.new PaidItems(paidItemObject.getInt(WebKeys.ITEM_ID), paidItemObject.getDouble(WebKeys.AMOUNT), paidItemObject.getDouble(WebKeys.PERCENT));
				paidItems.add(paidItem);
			}
			payment.setPaidItems(paidItems);
			payments.add(payment);
		}
		
		theBill.setPayments(payments);
		
	}
	
	public String getResponse() {
		return mResponse;
	}
		
	public Boolean getSuccess() {
		return mSuccess;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public Check getTheBill() {
		return theBill;
	}
}