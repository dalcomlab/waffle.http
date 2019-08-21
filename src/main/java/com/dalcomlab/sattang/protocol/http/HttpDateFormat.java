package com.dalcomlab.sattang.protocol.http;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HttpDateFormat {

    private static final String DATE_RFC5322 = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     *
     * @return
     */
    public static final String getCurrentDate() {
        long now = System.currentTimeMillis();
        SimpleDateFormat date = createDateFormat(DATE_RFC5322, Locale.US, TimeZone.getTimeZone("GMT"));
        return date.format(new Date(now));
    }

    /**
     *
     * @param format
     * @param locale
     * @param zone
     * @return
     */
    public static SimpleDateFormat createDateFormat(String format, Locale locale, TimeZone zone) {
        SimpleDateFormat date = new SimpleDateFormat(format, locale);
        date.setTimeZone(zone);
        return date;
    }

}
