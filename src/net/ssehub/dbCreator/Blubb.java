package net.ssehub.dbCreator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Blubb {
    
//    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE dd MM yyyy HH:mm:ss Z");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
    private static final SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat("dd MM yyyy HH:mm:ss Z");

    public static void main(String[] args) throws ParseException {
//        DATE_FORMAT.parse("Thu, 2 Aug 2018 07:04:23 +0800");
//        String date = "Thu, 02 03 2018 07:04:23 +0800";
        String date = "Thu, 03 Mar 2014 15:34:45 +0800";
        int pos = date.indexOf(',');
        date = date.substring(pos + 2);
        System.out.println(date);
        Date d = DATE_FORMAT.parse(date);
        System.out.println(DATE_FORMAT2.format(d));

    }

}
