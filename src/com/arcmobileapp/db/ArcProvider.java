package com.arcmobileapp.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.arcmobileapp.db.provider.Table_Funds;
import com.arcmobileapp.db.provider.Table_Funds.FundsColumns;

/**
 * Contains methods for assembling and maintaining the database used by the application
 */
public class ArcProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://com.arcmobileapp.db.provider");

	public static final int UNSAVED_ID = -1;

	public static final String DATABASE_NAME = "arcdb.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String FUNDS_TABLE = "funds";
	
	//URI codes
	private static final int FUNDS = 1;
	

	private static final UriMatcher sUriMatcher;

	/**
     * This class helps open, create, and upgrade the database file.
     */
    private static class ArcDatabaseHelper extends SQLiteOpenHelper {
    	
    	public ArcDatabaseHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}
    	
    	@Override
    	public void onCreate(SQLiteDatabase database) {
    		
            // Structured Test table
    	    String createTableString = "CREATE TABLE IF NOT EXISTS " + FUNDS_TABLE + " ("                    
                    + FundsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + FundsColumns.NUMBER + " TEXT,"
                    + FundsColumns.EXPIRATION_MONTH + " TEXT,"
                    + FundsColumns.EXPIRATION_YEAR + " TEXT,"
                    + FundsColumns.ZIP + " TEXT,"
                    + FundsColumns.CVV + " TEXT,"
                    + FundsColumns.CARD_TYPE_ID + " TEXT,"
                    + FundsColumns.CARD_TYPE_LABEL + " TEXT,"
                    + FundsColumns.PIN + " TEXT"
                    + ");";
            
            database.execSQL(createTableString);
		}
    	
//		private String generateForeignKey(String column, String foreignTable) {
//			return generateForeignKey(column, foreignTable, BaseColumns._ID);
//		}
//
//		private String generateForeignKey(String column, String foreignTable, String foreignColumn) {
//			return "FOREIGN KEY(" + column + ") REFERENCES " + foreignTable + "(" + foreignColumn + ")";
//		}
		

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    if (newVersion != oldVersion){
	            database.execSQL("DROP TABLE IF EXISTS " + FUNDS_TABLE);
		    }
			
			onCreate(database);
		}
	}

	private static ArcDatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new ArcDatabaseHelper(getContext());
		return true;
	}

	public SQLiteDatabase open() {
		return mOpenHelper.getWritableDatabase();
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(getTableNameFromUri(uri));

		// TODO: build the query, and set the order

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		Context context = getContext();
		ContentResolver contentResolver = context.getContentResolver();
		c.setNotificationUri(contentResolver, uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {

		// TODO: setup content type
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		String tableName = getTableNameFromUri(uri);
		if (tableName == null)
			return null;

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long newId = db.insert(tableName, null, initialValues);
		// TODO : set to entity uri.
		return uri.buildUpon().appendPath(String.valueOf(newId)).build();
	}

	public static String getTableNameFromUri(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		
		case FUNDS:
			return FUNDS_TABLE;
		
		default:
			return null;
		}
	}
	
	public void clearData(){
	    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	    db.delete(FUNDS_TABLE, null, null);
	    db.close();
	}
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
	    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	    String tableName = getTableNameFromUri(uri);
	    int count = db.delete(tableName, where, selectionArgs);
	    getContext().getContentResolver().notifyChange(uri, null);
	    return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;

		String tableName = getTableNameFromUri(uri);
		// TODO doesn't handle updates of ID and other where clause
//		if (where == null) {
//			// single update
//			String id = uri.getPathSegments().get(1);
//			count = db.update(tableName, values, EntryColumns._ID + "=" + id, whereArgs);
//		} else {
//			// multi-update
//			count = db.update(tableName, values, where, whereArgs);
//		}
		count = db.update(tableName, values, where, whereArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Table_Funds.AUTHORITY, "funds", FUNDS);
        sUriMatcher.addURI(Table_Funds.AUTHORITY, "funds/#", FUNDS);
	}
}
