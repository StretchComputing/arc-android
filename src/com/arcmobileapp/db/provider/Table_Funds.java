package com.arcmobileapp.db.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Table_Funds {
	public static final String AUTHORITY = "com.arcmobileapp.db.provider.table_funds";
    
    public static final class FundsColumns implements BaseColumns {
        

        public static final Uri CONTENT_URI = Uri.parse("content://com.arcmobileapp.db.provider.table_funds/funds");
        
        public static final String NUMBER = "number";
        public static final String EXPIRATION_MONTH = "expiration_month";
        public static final String EXPIRATION_YEAR = "expiration_year";
        public static final String ZIP = "zip";
        public static final String CVV = "cvv";
        public static final String CARD_TYPE_ID = "card_type_id";
        public static final String CARD_TYPE_LABEL = "card_type_label";
        public static final String PIN = "pin";
        public static final String CARD_NAME = "card_name";

    }
}
