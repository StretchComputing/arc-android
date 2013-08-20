package com.arcmobileapp.domain;


import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

import com.arcmobileapp.db.provider.Table_Funds.FundsColumns;
import com.arcmobileapp.utils.CursorUtilities;
import com.arcmobileapp.utils.Encrypter;

public class Cards implements Serializable {

	private static final long serialVersionUID = 1936138445318344457L;
	private int mId;
    private String mNumber;

    private String mExpirationMonth;
    private String mExpirationYear;
    private String mZip;
    private String mCVV;
    private String mCardId;
    private String mCardLabel;
    private String mPIN;
    private String mCardName;

    
    public Cards(int id, String mNumber, String month, String year, String zip, String cvv, String cardId, String cardLabel, String pin) {
       
    	setId(id);
    	setNumber(mNumber);
        setExpirationMonth(month);
        setExpirationYear(year);
        setZip(zip);
        setCVV(cvv);
        setCardId(cardId);
        setCardLabel(cardLabel);
        setPIN(pin);
        setCardName("");
    }
    
    public Cards(String mNumber, String month, String year, String zip, String cvv, String cardId, String cardLabel, String pin) {
        this(-1,  mNumber,  month,  year,  zip,  cvv,  cardId,  cardLabel, pin);
    }
 
    public Cards(Cursor cursor) {
    	if(cursor == null)
    		return;
    	
        mId = CursorUtilities.getInt(cursor, FundsColumns._ID);
        
        if (CursorUtilities.getString(cursor, FundsColumns.NUMBER) != null)
        	//mNumber = decrypt(CursorUtilities.getString(cursor, FundsColumns.NUMBER));
        
        	mNumber = CursorUtilities.getString(cursor, FundsColumns.NUMBER);
        
        if (CursorUtilities.getString(cursor, FundsColumns.EXPIRATION_MONTH) != null)
            mExpirationMonth = decrypt(CursorUtilities.getString(cursor, FundsColumns.EXPIRATION_MONTH));
        
        if (CursorUtilities.getString(cursor, FundsColumns.EXPIRATION_YEAR) != null)
            mExpirationYear = decrypt(CursorUtilities.getString(cursor, FundsColumns.EXPIRATION_YEAR));
        
        if (CursorUtilities.getString(cursor, FundsColumns.ZIP) != null)
            mZip = decrypt(CursorUtilities.getString(cursor, FundsColumns.ZIP));
        
        if (CursorUtilities.getString(cursor, FundsColumns.CVV) != null)
            mCVV = decrypt(CursorUtilities.getString(cursor, FundsColumns.CVV));
        
        if (CursorUtilities.getString(cursor, FundsColumns.CARD_TYPE_ID) != null)
            mCardId = decrypt(CursorUtilities.getString(cursor, FundsColumns.CARD_TYPE_ID));
        
        if (CursorUtilities.getString(cursor, FundsColumns.CARD_TYPE_LABEL) != null)
            mCardLabel = decrypt(CursorUtilities.getString(cursor, FundsColumns.CARD_TYPE_LABEL));
        
        if (CursorUtilities.getString(cursor, FundsColumns.PIN) != null)
            mPIN = decrypt(CursorUtilities.getString(cursor, FundsColumns.PIN));
        
        if (CursorUtilities.getString(cursor, FundsColumns.CARD_NAME) != null)
            mCardName = decrypt(CursorUtilities.getString(cursor, FundsColumns.CARD_NAME));
    }
    
    /**
     * Serializes in to content values for DB storage
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (mId > 0) {
            values.put(FundsColumns._ID, mId);
        } else {
            values.putNull(FundsColumns._ID);
        }

        if (mNumber != null) {
           // values.put(FundsColumns.NUMBER, encrypt(mNumber));
        	values.put(FundsColumns.NUMBER, mNumber);
        } else {
            values.putNull(FundsColumns.NUMBER);
        }
        
        if (mExpirationMonth != null) {
            values.put(FundsColumns.EXPIRATION_MONTH, encrypt(mExpirationMonth));
        } else {
            values.putNull(FundsColumns.EXPIRATION_MONTH);
        }
        
        if (mExpirationYear != null) {
            values.put(FundsColumns.EXPIRATION_YEAR, encrypt(mExpirationYear));
        } else {
            values.putNull(FundsColumns.EXPIRATION_YEAR);
        }
        
        if (mZip != null) {
            values.put(FundsColumns.ZIP, encrypt(mZip));
        } else {
            values.putNull(FundsColumns.ZIP);
        }
        
        if (mCVV != null) {
            values.put(FundsColumns.CVV, encrypt(mCVV));
        } else {
            values.putNull(FundsColumns.CVV);
        }
        
        if (mCardId != null) {
            values.put(FundsColumns.CARD_TYPE_ID, encrypt(mCardId));
        } else {
            values.putNull(FundsColumns.CARD_TYPE_ID);
        }
        
        if (mCardLabel != null) {
            values.put(FundsColumns.CARD_TYPE_LABEL, encrypt(mCardLabel));
        } else {
            values.putNull(FundsColumns.CARD_TYPE_LABEL);
        }
        
        if (mPIN != null) {
            values.put(FundsColumns.PIN, encrypt(mPIN));
        } else {
            values.putNull(FundsColumns.PIN);
        }        
        
        if (mCardName != null) {
            values.put(FundsColumns.CARD_NAME, encrypt(mCardName));
        } else {
            values.putNull(FundsColumns.CARD_NAME);
        } 
        
        return values;
    }
    
   
   
	public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

	public String getNumber() {
		return mNumber;
	}

	public void setNumber(String number) {
		this.mNumber = number;
	}
	

	
	
	
	public String getExpirationMonth() {
		return mExpirationMonth;
	}
	
	public void setExpirationMonth(String expirationMonth) {
		this.mExpirationMonth = expirationMonth;
	}

	public String getExpirationYear() {
		return mExpirationYear;
	}

	public void setExpirationYear(String expirationYear) {
		this.mExpirationYear = expirationYear;
	}

	public String getZip() {
		return mZip;
	}

	public void setZip(String zip) {
		this.mZip = zip;
	}

	public String getCVV() {
		return mCVV;
	}

	public void setCVV(String cvv) {
		this.mCVV = cvv;
	}
	
	public String getCardId() {
		return mCardId;
	}

	public void setCardId(String cardId) {
		this.mCardId = cardId;
	}
	
	public String getCardName() {
		return mCardName;
	}

	public void setCardName(String cardName) {
		this.mCardName = cardName;
	}
	
	

	public String getCardLabel() {
		return mCardLabel;
	}

	public void setCardLabel(String cardLabel) {
		this.mCardLabel = cardLabel;
	}
	
	public String getPIN() {
		return mPIN;
	}

	public void setPIN(String pin) {
		this.mPIN = pin;
	}
	
	protected Encrypter getEncrypter() {
		Encrypter encrypter = Encrypter.INSTANCE;
		if (encrypter == null || !encrypter.isReadyToRun()) {
			encrypter.setupEncrypter();
		}
		return encrypter;
	}

	protected String decrypt(String encrypted) {
		return getEncrypter().unpackString(encrypted);
		
	}

	protected String encrypt(String data) {
		return getEncrypter().packString(data);
	}
}
