package com.arcmobileapp.utils;

public final class PaymentFlags {
		
	public enum CardType {
		V,   //V: Visa
		N,   //N: Diner's
		M,   //M: MasterCard
		A,   //A: American Express
		D,   //D: Discovery
		Z    //Z: Dwolla
	}
	
	public enum PaymentType {
		DWOLLA,
		DEBIT,
		CREDIT
	}
	
	public enum SplitType {
		NONE,
		DOLLAR,
		PERCENT,
		ITEMIZED
	}

	
	public enum TipEntry {
		NONE,
		MANUAL,
		SHORTCUT18,
		SHORTCUT20,
		SHORTCUT22,
	}
	
	public enum PercentEntry {
		NONE,
		MANUAL,
		SHORTCUT25,
		SHORTCUT33,
		SHORTCUT50
	}
	
}
