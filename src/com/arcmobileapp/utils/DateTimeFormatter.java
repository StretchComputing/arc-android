package com.arcmobileapp.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.arcmobileapp.ArcMobileApp;

/** Date time formatter. Handles local formatting of dates to strings. */
public class DateTimeFormatter {

    /** Update date displayed on button */    
    private static DateFormat numericDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private DateFormat shortDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
    private DateFormat timeUnitsFormat = new SimpleDateFormat("a", Locale.getDefault());
    private DateFormat time24HourFormat = new SimpleDateFormat("HH:mm");
    private DateFormat unified24HourFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
    private DateFormat unifiedFormat = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());    

    /** converts from a unified 24 hour string format "dd MMM yyyy HH:mm".
     * 
     * @param display
     *            a formatted string
     * @return the GregorianCalendar for that date time */
    public GregorianCalendar unifiedStringToCalendar(String display) {        
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext())) {
                calendar.setTime(unified24HourFormat.parse(display));
            } else {
                calendar.setTime(unifiedFormat.parse(display));
            }

        } catch (ParseException e) {
        	Logger.d(DateTimeFormatter.class.toString(), "ParseException while trying to create a calendar object from a string.");
            e.printStackTrace();
        }
        return calendar;
    }

    /** formats a calendar to a string "dd MMM yyyy HH:mm"
     * 
     * @param calendar
     * @return a formatted string */
    public String calendarToUnifiedString(GregorianCalendar calendar) {
        if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext()))
            return unified24HourFormat.format(calendar.getTime());
        return unifiedFormat.format(calendar.getTime());
    }
    
    /** formats a calendar to a string "dd MMM yyyy HH:mm" using the specific timezone instead of default
     * 
     * @param calendar
     * @param timezone
     * @return a formatted string */
    public String calendarToUnifiedString(GregorianCalendar calendar, TimeZone tz) {
        DateFormat formatter;
        if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext())) {
            formatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        } else {
            formatter = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());
        }
        
        formatter.setTimeZone(tz);
        return formatter.format(calendar.getTime());
    }

    /** Returns only the Hours and Minutes portion of a timestamp, with proper formatting */
    public String calendarToTimeString(GregorianCalendar calendar) {
        if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext()))
            return time24HourFormat.format(calendar.getTime());
        return timeFormat.format(calendar.getTime());
    }

    /** Returns the calendar's time units.
     * 
     * @param calendar
     * @return */
    public String calendarToTimeUnits(GregorianCalendar calendar) {
        if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext()))
            return null;
        return timeUnitsFormat.format(calendar.getTime());
    }

    /** Returns the calendar to a date string "dd MMM yyyy"
     * 
     * @param calendar
     * @return */
    public String calendarToDateString(GregorianCalendar calendar) {
        return dateFormat.format(calendar.getTime());
    }

    /** Returns the calendar as a short date string "dd MMM"
     * 
     * @param calendar
     * @return */
    public String calendarToShortDateString(GregorianCalendar calendar) {
        return shortDateFormat.format(calendar.getTime());
    }

    /** Returns a numeric sortable date format with no local "yyyy-MM-dd"
     * 
     * @param display
     * @return */
    public static GregorianCalendar numericDateToCalendar(String display) {
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(numericDateFormat.parse(display));
        } catch (ParseException e) {
        	Logger.d("MyApplication.getFormatter()", "Failed parse of " + display);
        }
        return calendar;
    }

    /** Returns a calendar from a string formatted "dd MMM yyyy"
     * 
     * @param display
     * @return */
    public GregorianCalendar dateStringToCalendar(String display) {
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(dateFormat.parse(display));
        } catch (ParseException e) {
        	Logger.d("MyApplication.getFormatter()", "Failed parse of " + display);
        }
        return calendar;
    }

    /** returns a calendar from a string formatter from a time "hh:mm a" or "HH:mm"
     * 
     * @param display
     * @return */
    public GregorianCalendar timeStringToCalendar(String display) {
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext()))
                calendar.setTime(time24HourFormat.parse(display));
            calendar.setTime(timeFormat.parse(display));
        } catch (ParseException e) {
        	Logger.d("MyApplication.getFormatter()", "Failed parse of " + display);
        }
        return calendar;
    }

    /** Convert a time string to a local time string "hh:mm a" or "HH:mm" -> "hh:mm a" or "HH:mm"
     * 
     * @param display
     * @return */
    public String storageTimeStringToLocalTimeString(String display) {
        return calendarToTimeString(timeStringToCalendar(display));
    }

    /** time string to a time string for storage "hh:mm a" or "HH:mm" -> "h:mm a"
     * 
     * @param display
     * @return */
    public String timeStringToStorageTimeString(String display) {
        return timeFormat.format(timeStringToCalendar(display).getTime());
    }

    public GregorianCalendar twentyFourHourTimeStringToCalendar(String display) {
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(time24HourFormat.parse(display));
        } catch (ParseException e) {
        	Logger.d("MyApplication.getFormatter()", "Failed parse of " + display);
        }
        return calendar;
    }

    /** Numeric date format YYYY-MM-DD
     * 
     * @param calendar
     * @return the date string formatted YYYY-MM-DD */
    public static String calendarToNumericDateString(GregorianCalendar calendar) {
        return numericDateFormat.format(calendar.getTime());
    }

    /** Time in 24 hour and minutes HH:mm
     * 
     * @param calendar
     * @return the time string */
    public String calendarTo24HourTimeString(GregorianCalendar calendar) {
        return time24HourFormat.format(calendar.getTime());
    }

    private final String[][] conversionTable = new String[][] { { "dd", "%d" }, { "MMM", "%b" }, { "yyyy", "" }, { "HH", "%H" },
            { "mm", "%M" }, { "h", "%I" }, { "a", "%p" } };

    /** Converts to a format understood by the javascript bridge
     * 
     * @param javaFormat
     * @return a javascript formatted date time */
    public String convertToJavascriptFormat(String javaFormat) {
        String result = javaFormat;
        for (int i = 0; i < conversionTable.length; i++) {
            result = result.replaceAll(conversionTable[i][0], conversionTable[i][1]);
        }
        return result;
    }

    /** Convert the current Java date format into a Javascript date format string
     * 
     * @return */
    public String getJavascriptFormat() {
        String jsDateFormat = convertToJavascriptFormat(((SimpleDateFormat) dateFormat).toPattern());
        if (android.text.format.DateFormat.is24HourFormat(ArcMobileApp.getContext())) {
            return jsDateFormat + "<br/>" + convertToJavascriptFormat(((SimpleDateFormat) time24HourFormat).toPattern());
        }
        return jsDateFormat + "<br/>" + convertToJavascriptFormat(((SimpleDateFormat) timeFormat).toPattern());
    }
}
