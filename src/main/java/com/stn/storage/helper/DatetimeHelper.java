package com.stn.storage.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeHelper {
    private final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static String getCurrentTimeStamp() {
        return Long.toString(System.currentTimeMillis());
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date date = new Date();
        return formatter.format(date);
    }
}
