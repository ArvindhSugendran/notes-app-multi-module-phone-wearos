package com.app.notesappandroidproject.utils;

import com.app.notesappandroidproject.App;
import com.app.notesappandroidproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DATE_FORMAT_1 = "yyyy.MM.dd HH:mm";
    public static final String DATE_FORMAT_2 = "yyyy.MM.dd";
    private static final String SPACE = " ";
    private static final long ONE_DAY = 60 * 60 * 24 * 1000L;
    private static final int ONE_HOUR = 60 * 60 * 1000;
    private static final int ONE_MINUTE = 60 * 1000;

    public static String getStampByDate(Date date, String pattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            return dateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String longToDateString(long time, String fomat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fomat);
            return sdf.format(time);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDateFromTimeString(long time) {
        String result = "";
        Date publishDate = new Date(time);
        Date currentDate = new Date();
        int hour = 0;
        int min = 0;
        float day = 0;
        long totalSec = (currentDate.getTime() - publishDate.getTime());
        if (totalSec > 0) {
            day = (float) (totalSec / ONE_DAY);
            if (day >= 30) {
                return getStampByDate(publishDate, DATE_FORMAT_2);
            } else if (day >= 2) {
                result = ((int) day) + SPACE + App.getInstance().getString(R.string.lb_day_ago);
            } else if (day > 0) {
                result = App.getInstance().getString(R.string.lb_yesterday);
            } else {
                hour = (int) (totalSec / ONE_HOUR);
                if (hour > 0) {
                    result = hour + SPACE + App.getInstance().getString(R.string.lb_hour_ago);
                } else {
                    min = (int) (totalSec) / ONE_MINUTE;
                    if (min == 0) {
                        result = App.getInstance().getString(R.string.lb_just_finished);
                    } else {
                        result = min + SPACE + App.getInstance().getString(R.string.lb_minute_ago);
                    }
                }
            }
        }
        return result;
    }

}

