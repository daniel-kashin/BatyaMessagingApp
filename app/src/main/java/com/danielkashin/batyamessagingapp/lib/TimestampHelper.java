package com.danielkashin.batyamessagingapp.lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Кашин on 27.11.2016.
 */

public class TimestampHelper {

    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat monthDay = new SimpleDateFormat("MMM dd", new Locale("en-US"));
        SimpleDateFormat hourMinute = new SimpleDateFormat("HH:mm", new Locale("en-US"));
        monthDay.setTimeZone(Calendar.getInstance().getTimeZone());
        hourMinute.setTimeZone(Calendar.getInstance().getTimeZone());

        String inputDate = monthDay.format(timestamp*1000);

        if (inputDate.equals(monthDay.format(System.currentTimeMillis()))){
            return hourMinute.format(timestamp*1000);
        } else {
            return monthDay.format(timestamp*1000);
        }
    }

    public static boolean datesDiffer(long oldTimestamp, long newTimestamp){
        SimpleDateFormat monthDay = new SimpleDateFormat("MMM dd", new Locale("en-US"));
        monthDay.setTimeZone(Calendar.getInstance().getTimeZone());

        return !monthDay.format(oldTimestamp*1000)
                .equals(monthDay.format(newTimestamp*1000));
    }

    public static String formatTimestampToDate(long timestamp){
        SimpleDateFormat monthDay = new SimpleDateFormat("MMM dd", new Locale("en-US"));
        monthDay.setTimeZone(Calendar.getInstance().getTimeZone());

        return monthDay.format(timestamp*1000);
    }

    public static String formatTimestampToTime(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", new Locale("en-US"));
        simpleDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
        return simpleDateFormat.format(timestamp * 1000);
    }
}
