package com.example.batyamessagingapp.lib;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Кашин on 27.11.2016.
 */

public class TimestampHelper {
    static final int secondsInDay = 86400; //60 * 60 * 24

    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat simpleDateFormat;

        if (timestamp / secondsInDay == System.currentTimeMillis() / 1000 / secondsInDay) {
            simpleDateFormat = new SimpleDateFormat("hh:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("MMM dd", new Locale("en-US"));
        }

        simpleDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
        return simpleDateFormat.format(timestamp * 1000);
    }

    public static String formatTimestampWithoutDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        simpleDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
        return simpleDateFormat.format(timestamp * 1000);
    }
}
