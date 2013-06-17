package com.arcmobileapp.db.controllers;


import java.util.ArrayList;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.RemoteException;

import com.arcmobileapp.db.provider.Table_Funds.FundsColumns;
import com.arcmobileapp.domain.Cards;

public class DBController {
	
	public static synchronized void saveCreditCard(ContentProviderClient mProvider, Cards newCard) {
    	ContentValues values = newCard.getContentValues();
        try {
            mProvider.insert(FundsColumns.CONTENT_URI, values);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
	
	public static synchronized void deleteCard(ContentProviderClient mProvider, int cardId) {
        try {
        	String whereClause = FundsColumns._ID + " =?";
            String[] whereArgs = { String.valueOf(cardId) };
    	    mProvider.delete(FundsColumns.CONTENT_URI, whereClause, whereArgs);        	
        } catch (RemoteException e) {
            e.printStackTrace();
        }		
	}
	
	public static synchronized void clearCards(ContentProviderClient mProvider) {
        try {
    	    mProvider.delete(FundsColumns.CONTENT_URI, null, null);         	
        } catch (RemoteException e) {
            e.printStackTrace();
        }		
	}

	public static synchronized ArrayList<Cards> getCards(ContentProviderClient mProvider) {
    	Cursor cursor = null;
    	ArrayList<Cards> cards = new ArrayList<Cards>();
    	try {
            String whereClause = FundsColumns.NUMBER + " IS NOT NULL";
            String[] whereArgs = null;
            String sortOrder = FundsColumns._ID + " DESC";  // get the most recent card added first
            cursor = mProvider.query(FundsColumns.CONTENT_URI, null, whereClause, whereArgs, sortOrder);
        } catch (RemoteException e) {
            e.printStackTrace();
             return null;
        }
        
        if(cursor!=null && cursor.moveToFirst()) {
             do {
            	 cards.add(new Cards(cursor));
             } while (cursor.moveToNext());    
        	cursor.close();
        }
        
        return cards;
    }
	
	public static synchronized int getCardCount(ContentProviderClient mProvider) {
		Cursor countCursor = null;
		int count = 0;
		try {
			countCursor = mProvider.query(FundsColumns.CONTENT_URI, new String[] {"count(*) AS count"}, null, null, null);
		} catch (RemoteException e) {
			e.printStackTrace();
			return 0;
		}
		if(countCursor != null && countCursor.moveToFirst()) {
			count = countCursor.getInt(0);
		}
		countCursor.close();
	    return count;
    }
}
