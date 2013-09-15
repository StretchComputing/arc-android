package com.arcmobileapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Utils {

	public static void goToWebsite(Context context) {
		Uri uriUrl = Uri.parse("http://arcmobileapp.com");
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		context.startActivity(launchBrowser);
	}

	/**
	 * turns a byte array to a hex string
	 * 
	 * @param hash
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String byteArray2Hex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
	
	// returns ISO 8601 format: yyyy-MM-ddTHH:mm:ss.SSSZ
	public static String convertToIsoDate(Date theGmtDate) {
		if(theGmtDate == null) {return null;}
	    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	    DateFormat df = new SimpleDateFormat(dateFormat);
	    return df.format(theGmtDate);
	}


	public static String convertModernPicType(Enums.ModernPicTypes type) {
		String symbol = "";
		switch (type) {
		case Dollar:
			return "#";
		case Receipt:
			return "a";
		case MoneyBag:
			return "$";
		case Facebook:
			return "F";
		case FacebookBox:
			return "G";
		case Twitter:
			return "T";
		case TwitterBox:
			return "U";
		case Guy:
			return "f";
		case Girl:
			return "k";
		case GuyGirl:
			return "g";
		case ThumbsUp:
			return "I";
		case ThumbsDown:
			return "L";
		case Windows:
			return "W";
		case CheckMark:
			return "%";
		case PlusSign:
			return "+";
		case Star:
			return "*";
		case Settings:
			return "(";
		case Badge:
			return ")";
		case Search:
			return "s";
		case Tag:
			return "J";
		case Heart:
			return "j";
		case Refresh:
			return "R";
		case Paper:
			return "K";
		case Pencil:
			return "r";
		case World:
			return "w";
		case Location:
			return ",";
		case Megaphone:
			return "Y";
		case Lock:
			return "n";
		case Unlock:
			return "q";
		case Expand:
			return "v";
		case Shrink:
			return "u";
		case MessageBubble:
			return "b";
		case Book:
			return "B";
		case WriteNote:
			return "V";
		case XMark:
			return "X";
		case XMarkCircle:
			return "x";
		case Stats:
			return "7";
		case StatsStacked:
			return "Z";
		case Mail:
			return "m";
		case Info:
			return "=";
		case Question:
			return "?";
		case Building:
			return "p";

		}
		return symbol;
	}
	
	// returns dollar and cents format as a string. Example105.32
	public static String toDollarCents(Double theRawAmount) {
		return String.format("%.2f", theRawAmount);
	}

}
