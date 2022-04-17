package com.tonyocallimoutou.go4lunch.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilData {

    private static int hoursNoRestaurant = 11;
    private static int minutesNoRestaurant = 0;
    private static int hoursRestaurant = 12;
    private static int minutesRestaurant = 0;
    private static List<String> language = Arrays.asList("English","French");
    private static String defaultLanguage = language.get(0);

    public static int getHoursNoRestaurant() {
        return hoursNoRestaurant;
    }

    public static void setHoursNoRestaurant(int hoursNoRestaurant) {
        UtilData.hoursNoRestaurant = hoursNoRestaurant;
    }

    public static int getMinutesNoRestaurant() {
        return minutesNoRestaurant;
    }

    public static void setMinutesNoRestaurant(int minutesNoRestaurant) {
        UtilData.minutesNoRestaurant = minutesNoRestaurant;
    }

    public static int getHoursRestaurant() {
        return hoursRestaurant;
    }

    public static void setHoursRestaurant(int hoursRestaurant) {
        UtilData.hoursRestaurant = hoursRestaurant;
    }

    public static int getMinutesRestaurant() {
        return minutesRestaurant;
    }

    public static void setMinutesRestaurant(int minutesRestaurant) {
        UtilData.minutesRestaurant = minutesRestaurant;
    }

    public static List<String> getLanguage() {
        return language;
    }

    public static void setLanguage(List<String> language) {
        UtilData.language = language;
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    public static void setDefaultLanguage(String defaultLanguage) {
        UtilData.defaultLanguage = defaultLanguage;
    }
}
