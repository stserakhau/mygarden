package com.i4biz.mygarden.expression.converters;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    private static final DateTimeFormatter HOUR_MINUTE_SECOND_FORMATTER = DateTimeFormat.forPattern("HHmmss");

    private static long MILLIS_IN_SEC = 1000;
    private static long MILLIS_IN_MIN = 60 * MILLIS_IN_SEC;
    private static long MILLIS_IN_HR = 60 * MILLIS_IN_MIN;
    private static long MILLIS_IN_DAY = 24 * MILLIS_IN_HR;

    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp nowPlusHours(int hours) {
        return new Timestamp(System.currentTimeMillis() + (MILLIS_IN_HR * hours));
    }

    public static Timestamp nowPlusDays(int days) {
        return new Timestamp(System.currentTimeMillis() + (MILLIS_IN_DAY * days));
    }

    public static long nowAsNanoSecond() {
        return System.nanoTime();
    }

    public static long nowAsMilliSecond() {
        return System.currentTimeMillis();
    }

    public static Timestamp nowPlusMinutes(int minutes) {
        return new Timestamp(System.currentTimeMillis() + (MILLIS_IN_MIN * minutes));
    }

    public static Timestamp nowPlusSeconds(long seconds) {
        return new Timestamp(System.currentTimeMillis() + (MILLIS_IN_SEC * seconds));
    }

    public static Timestamp nowMinusMinutes(int minutes) {
        return new Timestamp(System.currentTimeMillis() - (MILLIS_IN_MIN * minutes));
    }

    public static Timestamp nowMinusDays(int days) {
        return new Timestamp(System.currentTimeMillis() - (days * MILLIS_IN_DAY));
    }

    public static long timePlusHours(long timeInMilliseconds, int hours) {
        return timeInMilliseconds + (hours * MILLIS_IN_HR);
    }

    public static long timePlusDays(long timeInMilliseconds, int days) {
        return timeInMilliseconds + (days * MILLIS_IN_DAY);
    }

    public static String getYearMonthDay(long timeInMilliseconds) {
        return YEAR_MONTH_DAY_FORMATTER.print(timeInMilliseconds);
    }

    public static String getHoursMinutesSeconds(long timeInMilliseconds) {
        return HOUR_MINUTE_SECOND_FORMATTER.print(timeInMilliseconds);
    }

    public static boolean isToday(Date date) {
        return isTodayConsideringTimeZone(date, DateTimeZone.getDefault());
    }

    public static Date getTimeAsStartOfDay(Date date) {
        return new DateTime(date).withTimeAtStartOfDay().toDate();
    }

    public static boolean isTodayConsideringTimeZone(Date date, DateTimeZone userTimeZone) {
        LocalDate today = LocalDate.now(userTimeZone);
        LocalDate userDate = new DateTime(date, userTimeZone).toLocalDate();
        return today.equals(userDate);
    }

    public static long convertLocalToUTC(long localTime, String localId) {
        return DateTimeZone.forID(localId).convertLocalToUTC(localTime, true);
    }

    public static Timestamp convertLocalToUTCTimestamp(long localTime) {
        return new Timestamp(convertLocalToUTC(localTime, TimeZone.getDefault().getID()));
    }

    public static long convertUTCToLocal(long utcTime, String localId) {
        return DateTimeZone.forID(localId).convertUTCToLocal(utcTime);
    }

    public static Timestamp convertUTCToLocalTimestamp(long utcTime) {
        return new Timestamp(convertUTCToLocal(utcTime, TimeZone.getDefault().getID()));
    }

    public static Date parseDate(String date) {
        SimpleDateFormat dateFormat;
        if (date.toUpperCase().contains(" AM") || date.toUpperCase().contains(" PM")) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss a");
        } else if (date.toUpperCase().contains("AM") || date.toUpperCase().contains("PM")) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ssa");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        try {
            date = date.length() == 10 ? date + " 00:00:00" : date;
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Timestamp convertTimeStamp(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime dt = parser.parseDateTime(dateTime);
            return new Timestamp(dt.getMillis());
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
    

