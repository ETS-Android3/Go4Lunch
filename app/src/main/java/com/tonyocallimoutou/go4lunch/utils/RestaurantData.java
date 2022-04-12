package com.tonyocallimoutou.go4lunch.utils;

import android.content.Context;
import android.location.Location;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.details.Period;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.Calendar;
import java.util.Random;

public class RestaurantData {

    private static Context context;
    private static RestaurantDetails result;
    private static Location userLocation;

    private RestaurantData(Context data, RestaurantDetails restaurant) {
        context = data;
        result = restaurant;
    }


    public static RestaurantData newInstance(Context context, RestaurantDetails restaurant) {
        return new RestaurantData(context, restaurant);
    }
    public static void newInstanceOfPosition(Location location) {
        userLocation = location;
    }


    // Getter

    public static String getDistance() {
        if (userLocation != null) {
            double lat1 = userLocation.getLatitude();
            double lng1 = userLocation.getLongitude();
            double lat2 = result.getGeometry().getLocation().getLat();
            double lng2 = result.getGeometry().getLocation().getLng();
            float[] distanceFloat = new float[1];
            Location.distanceBetween(lat1,lng1,lat2,lng2,distanceFloat);

            int distance = Math.round(distanceFloat[0]);
            return distance + "m";
        }
        else {
            return null;
        }
    }

    public static String getRestaurantName() {
        return result.getName();
    }

    public static String getOpeningHour() {
        Calendar currentTime = Calendar.getInstance();

        int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK)-1;

        int time = currentTime.get(Calendar.HOUR_OF_DAY)*100 + currentTime.get(Calendar.MINUTE);

        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {

                if(result.getOpeningHours().getPeriods().size() == 1) {
                    Period period = result.getOpeningHours().getPeriods().get(0);
                    if (period.getOpen().getTime().equals("0000") && period.getClose() == null) {
                        return context.getString(R.string.restaurant_data_open_24h7j);
                    }
                }

                for (Period period : result.getOpeningHours().getPeriods()) {
                    if (period.getOpen().getDay() == dayOfWeek) {

                        if (period.getClose() == null) {
                            return context.getString(R.string.restaurant_data_open_24h);
                        }

                        int openHour = Integer.parseInt(period.getOpen().getTime());
                        int closeHour = Integer.parseInt(period.getClose().getTime());

                        if (openHour < time && closeHour > time ) {
                            int hour = closeHour/100;
                            int minute = closeHour%100;

                            if (minute == 0) {
                                return context.getString(R.string.restaurant_data_open_until) + " " + hour +":00";
                            }

                            return context.getString(R.string.restaurant_data_open_until)+" " + hour + ":" + minute;
                        }
                    }
                }
                return context.getString(R.string.restaurant_data_open);
            }
            else {
                return context.getString(R.string.restaurant_data_close);
            }
        }
        else {
            return context.getString(R.string.restaurant_data_no_data);
        }
    }

    public static String getTypeAndAddress() {
        String type = result.getTypes().get(0);
        String address = result.getVicinity();

        return type + " - " + address;
    }

    public static String getNbrWorkmates() {
        if (result.getWorkmatesId().size() > 0) {
            return "(" + result.getWorkmatesId().size() + ")";
        }
        else {
            return null;
        }
    }

    public static int getRate() {
        Random ran = new Random();
        return ran.nextInt(4);
    }

    public static String getPicture() {
        if (result.getPhotos() != null) {
            return result.getPhotos().get(0).getImage();
        }
        else {
            return null;
        }
    }

    public static String getPhone() {
        return result.getInternationalPhoneNumber();
    }

    public static String getWebsite() {
        return result.getWebsite();
    }


}
