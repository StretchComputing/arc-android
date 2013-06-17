package com.arcmobileapp.utils;


import java.util.GregorianCalendar;

import android.database.Cursor;

/**
 * Cursor utilities to simplify cursor reading
 */
public class CursorUtilities {
	
	public static String getString(Cursor c, String s){
		int colId = c.getColumnIndex(s);
		return c.getString(colId);
	}
	
	public static int getInt(Cursor c, String s){
		int colId = c.getColumnIndex(s);
		return c.getInt(colId);
	}
	
	public static long getLong(Cursor c, String s){
        int colId = c.getColumnIndex(s);
        return c.getLong(colId);
    }
	
	public static byte[] getBlob(Cursor c, String s){
        int colId = c.getColumnIndex(s);
        return c.getBlob(colId);
    }
	
    public static <T extends Enum<T>> T getEnum(Cursor c, String s, Class<T> enumType){           
        T newType;
        int index = c.getColumnIndex(s);
        String packedI = c.getString(index);
        if(packedI == null || packedI.equals(""))
            return null;
        newType = Enum.valueOf(enumType, Encrypter.INSTANCE.unpackString(packedI));
        return newType;
	}
    
    public static <T extends Enum<T>> T getUnencryptedEnum(Cursor c, String s, Class<T> enumType){           
        T newType;
        int index = c.getColumnIndex(s);
        String val = c.getString(index);
        if(val == null || val.equals(""))
            return null;
        newType = Enum.valueOf(enumType, val);
        return newType;
	}
    
    /**
     * Converts a db timestamp into a calendar object
     * @param cursor
     * @param columnName
     * @return
     */
    public static GregorianCalendar getCalendarFromDbTimestamp(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(cursor.getLong(columnIndex));
        return c;
    }
}
