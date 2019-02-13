package com.simonk.project.prettyrss.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

    public static String convertRssDateToLocale(Context context, String rssDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ", Locale.US);
        try {
            return android.text.format.DateFormat.getDateFormat(context).format(dateFormat.parse(rssDate)) +
                    " " +
                    android.text.format.DateFormat.getTimeFormat(context).format(dateFormat.parse(rssDate));
        } catch (ParseException e) {
            return rssDate;
        }
    }

}
