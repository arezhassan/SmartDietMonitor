package com.example.smartdietmonitoring.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {


    public static String convertToFirebaseKey(String inputDate) {
        try {
            DateFormat inputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = day + " " + getMonthName(month) + " " + year;
        String keyDate = convertToFirebaseKey(currentDate);
        return keyDate;
    }
public static String getNormalCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = day + " " + getMonthName(month) + " " + year;
        return currentDate;
    }

    public static String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }
    public static Date convertFromFirebase(String firebaseDate) {
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = inputFormat.parse(firebaseDate);
            DateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }





}
