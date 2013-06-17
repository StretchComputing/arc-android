package com.arcmobileapp.activities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.db.controllers.DBController;
import com.arcmobileapp.domain.Cards;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.domain.CreatePayment;
import com.arcmobileapp.domain.LineItem;
import com.arcmobileapp.domain.Payments;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.CurrencyFilter;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.PaymentFlags;
import com.arcmobileapp.web.GetCheckTask;
import com.arcmobileapp.web.MakePaymentTask;

public class ViewCheck extends BaseActivity {

	private String merchantId;
	private String venueName;
	private String checkNum;
	private TextView title;
	private TextView checkDetails;
	String paymentInfo;
	private AlertDialog payDialog;
	private Double totalBill;
	private Double myBill;
	private Double amountPaid;
	private String invoiceId;
	DecimalFormat money = new DecimalFormat("$#.00");
	NumberFormat quantity = new DecimalFormat("#");

	public ViewCheck() {
		super();
	}

	public ViewCheck(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		totalBill = 0d;
		amountPaid = 0d;
		setContentView(R.layout.view_check);
		title = (TextView) findViewById(R.id.title);
		checkDetails = (TextView) findViewById(R.id.check_details);
		venueName = getIntent().getStringExtra(Constants.VENUE);
		checkNum = getIntent().getStringExtra(Constants.CHECK_NUM);
		merchantId = getIntent().getStringExtra(Constants.VENUE_ID);
		title.setText(venueName + "\n" + checkNum);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getInvoice();
	}

	protected void getInvoice() {
		String token = getToken();
		if (token != null) {
			GetCheckTask getInvoiceTask = new GetCheckTask(token, merchantId, checkNum, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if (getSuccess()) {

						String check = "";
						Check theBill = getTheBill();

						if (theBill == null || theBill.getItems().size() == 0) {
							toastShort("Could not locate your check");
							finish();
							return;
						}
						invoiceId = String.valueOf(theBill.getId());
						check += "Invoice ID: " + invoiceId + "\n";
						check += "Check number: " + theBill.getNumber() + "\n";
						check += "Merchant ID: " + theBill.getMerchantId() + "\n\n";
						check += theBill.getLastUpdated() + "\n";
						check += "Waiter Ref: " + theBill.getWaiterRef() + "\n";
						check += "Table Number: " + theBill.getTableNumber() + "\n\n";
						check += "Base Amount: " + money.format(theBill.getBaseAmount()) + "\n";
						check += "Tax: " + money.format(theBill.getTaxAmount()) + "\n";
						check += "Amount Paid: " + money.format(theBill.getAmountPaid()) + "\n\n";

						totalBill = theBill.getBaseAmount() + theBill.getTaxAmount();
						amountPaid = theBill.getAmountPaid();

						for (LineItem item : theBill.getItems()) {
							check += quantity.format(item.getAmount()) + " | " + item.getDescription() + " | " + money.format(item.getValue()) + "\n";
						}
						// showInfoDialog(check);
						checkDetails.setText(check);

						paymentInfo = "";
						ArrayList<Payments> payments = theBill.getPayments();
						if (payments != null) {
							for (Payments payment : payments) {
								paymentInfo += "Payment ID: " + payment.getId() + "\n";
								paymentInfo += "Customer name: " + payment.getCustomerName() + "\n";
								paymentInfo += "Customer id: " + payment.getCustomerId() + "\n";
								paymentInfo += "Payment amount: " + payment.getAmount() + "\n";
								paymentInfo += "Gratuity: " + payment.getGratuity() + "\n";
								paymentInfo += payment.getType() + " " + payment.getAccount() + "\n";
								paymentInfo += payment.getConfirmation() + "\n";

								paymentInfo += "\n\n";
							}
						}

					} else {
						toastShort("Could not locate your check");
						finish();
					}
				}
			};
			getInvoiceTask.execute();
		} else {
			Logger.d("NO TOKEN - GET TOKEN AND THEN GET THE INVOICE NUMBER");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	public void onViewPaymentsClick(View v) {
		showInfoDialog(paymentInfo);
	}

	public void onMakePaymentClick(View v) {
		showPayAmountDialog();
	}

	//
	// LayoutInflater factory = LayoutInflater.from(this);
	// final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
	// return new AlertDialog.Builder(AlertDialogSamples.this)
	// .setIcon(R.drawable.alert_dialog_icon)
	// .setTitle(R.string.alert_dialog_text_entry)
	// .setView(textEntryView)
	// .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// /* User clicked OK so do some stuff */
	// }
	// })
	// .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// /* User clicked cancel so do some stuff */
	// }
	// })
	// .create();

	private void makePayment() {
		ArrayList<Cards> cards = DBController.getCards(getContentProvider());
		if (cards.size() == 0) {
			toastShort("You must add a card first (see Funds in slide-out menu)");
			return;
		}

		Cards useFirstCard = cards.get(0);

		String token = getString(Keys.DEV_TOKEN);
		String customerId = getString(Keys.DEV_CUSTOMER_ID);

		String account = useFirstCard.getNumber().replace(" ", "");
		String month = useFirstCard.getExpirationMonth();
		if (month.length() == 1) {
			month = "0" + month;
		}
		String year = useFirstCard.getExpirationYear().substring(2, 4);
		String expiration = month + "-" + year;
		String pin = useFirstCard.getCVV();
		String type = PaymentFlags.PaymentType.CREDIT.toString();
		String cardType = PaymentFlags.CardType.V.toString();
		String splitType = PaymentFlags.SplitType.DOLLAR.toString();

		Logger.d("make payment with expiration " + expiration + " and pin " + pin + " and type = " + type + " and card type = " + cardType);

		Double tipAmount = 0d;
		CreatePayment newPayment = new CreatePayment(merchantId, customerId, invoiceId, totalBill, myBill, tipAmount, account, type, cardType, expiration, pin, null, splitType, null, null, null);

		MakePaymentTask task = new MakePaymentTask(token, newPayment, getApplicationContext());
		task.execute();
	}

	private void showPayAmountDialog() {
		payDialog = null;

		LayoutInflater factory = LayoutInflater.from(this);
		final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
		final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
		input.setFilters(new InputFilter[] { new CurrencyFilter() });
		final TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);

		final Double remainingBill = totalBill - amountPaid;
		remainingBalance.setText("Remaining balance: " + money.format(remainingBill));
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setView(makePaymentView);
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		payDialog = builder.create();
		payDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = payDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String paymentAmount = input.getText().toString();
						if (paymentAmount == null || paymentAmount.trim().length() == 0) {
							toastShort("Please enter an amount or cancel");
							return;
						} else {
							myBill = Double.parseDouble(paymentAmount);
							if (myBill > (remainingBill)) {
								toastShort("Can't pay more than is remaining");
								return;
							}
							toastShort("Make payment of $" + paymentAmount);
							payDialog.dismiss();
							makePayment();
						}
						payDialog.dismiss();
					}
				});
			}
		});
		payDialog.show();
	}

	private void showInfoDialog(String display) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewCheck.this);
		builder.setTitle(getString(R.string.app_dialog_title));
		builder.setMessage(display);
		//builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// hideSuccessMessage();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// hideSuccessMessage();
			}
		});
		builder.create().show();
	}
}
