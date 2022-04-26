package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.details.Close;
import com.tonyocallimoutou.go4lunch.model.places.details.Open;
import com.tonyocallimoutou.go4lunch.model.places.details.OpeningHours;
import com.tonyocallimoutou.go4lunch.model.places.details.Period;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TestRestaurantData {

    @Mock
    Context context;

    List<RestaurantDetails> nearbyRestaurant = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    List<RestaurantDetails> bookedRestaurant = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    RestaurantDetails restaurant = nearbyRestaurant.get(0);
    List<Period> periods = Arrays.asList(
            // Lundi
            new Period(
                    new Open(1,"1000"),
                    new Close(1,"1300")
            ),
            new Period(
                    new Open(1,"1800"),
                    new Close(1,"2200")
            )
    );

    @Before
    public void setup() {
        openMocks(this);
        when(context.getString(R.string.restaurant_data_open_until)).thenReturn("Open until");
        when(context.getString(R.string.restaurant_data_close)).thenReturn("Close");
        when(context.getString(R.string.restaurant_data_open)).thenReturn("Open");
        when(context.getString(R.string.restaurant_data_no_data)).thenReturn("No data");

    }

    @Test
    public void getSimpleDataFromRestaurant() {

        List<String> workmatesId = new ArrayList<>();
        workmatesId.add("Test1");
        workmatesId.add("Test2");

        restaurant.setWorkmatesId(workmatesId);

        RestaurantData.newInstance(context,restaurant);

        String name = RestaurantData.getRestaurantName();
        String phone = RestaurantData.getPhone();
        String website = RestaurantData.getWebsite();
        String nbrWorkmate = RestaurantData.getNbrWorkmates();
        String typeAddress = RestaurantData.getTypeAndAddress();

        assertEquals(restaurant.getName(), name);
        assertEquals(restaurant.getInternationalPhoneNumber(), phone);
        assertEquals(restaurant.getWebsite(), website);
        assertEquals("(2)", nbrWorkmate);
        assertEquals("type1 - address1", typeAddress);

    }
    @Test
    public void getOpeningHoursFromRestaurantForLunch() {

        OpeningHours openingHours = new OpeningHours(true,periods);

        restaurant.setOpeningHours(openingHours);


        // Lundi 4 Avril, 12h
        Calendar timeForTest = Calendar.getInstance();
        Date testDate = new Date(2022,3,3,12,0);
        timeForTest.setTime(testDate);

        RestaurantData.newInstance(context,restaurant);

        try (MockedStatic<Calendar> mocked = mockStatic(Calendar.class)) {
            mocked.when((MockedStatic.Verification) Calendar.getInstance()).thenReturn(timeForTest);

            String test = RestaurantData.getOpeningHour();
            assertEquals("Open until 13:00", test);
        }
    }

    @Test
    public void getOpeningHoursFromRestaurantForDinner() {

        OpeningHours openingHours = new OpeningHours(true,periods);

        restaurant.setOpeningHours(openingHours);


        // Lundi 4 Avril, 19h
        Calendar timeForTest = Calendar.getInstance();
        Date testDate = new Date(2022,3,3,19,0);
        timeForTest.setTime(testDate);

        RestaurantData.newInstance(context,restaurant);

        try (MockedStatic<Calendar> mocked = mockStatic(Calendar.class)) {
            mocked.when((MockedStatic.Verification) Calendar.getInstance()).thenReturn(timeForTest);

            String test = RestaurantData.getOpeningHour();
            assertEquals("Open until 22:00", test);
        }

    }

    @Test
    public void getOpeningHoursWithConflict() {

        OpeningHours openingHours = new OpeningHours(true,periods);

        restaurant.setOpeningHours(openingHours);


        // Lundi 4 Avril, 15h
        Calendar timeForTest = Calendar.getInstance();
        Date testDate = new Date(2022,3,3,15,0);
        timeForTest.setTime(testDate);

        RestaurantData.newInstance(context,restaurant);

        try (MockedStatic<Calendar> mocked = mockStatic(Calendar.class)) {
            mocked.when((MockedStatic.Verification) Calendar.getInstance()).thenReturn(timeForTest);

            String test = RestaurantData.getOpeningHour();
            assertEquals("Open", test);
        }
    }

    @Test
    public void getOpeningHoursButClose() {

        OpeningHours openingHours = new OpeningHours(false,periods);

        restaurant.setOpeningHours(openingHours);


        // Lundi 4 Avril, 15h
        Calendar timeForTest = Calendar.getInstance();
        Date testDate = new Date(2022,3,3,15,0);
        timeForTest.setTime(testDate);

        RestaurantData.newInstance(context,restaurant);

        try (MockedStatic<Calendar> mocked = mockStatic(Calendar.class)) {
            mocked.when((MockedStatic.Verification) Calendar.getInstance()).thenReturn(timeForTest);

            String test = RestaurantData.getOpeningHour();
            assertEquals("Close", test);
        }
    }

    @Test
    public void getOpeningHoursButNoData() {

        restaurant.setOpeningHours(null);

        RestaurantData.newInstance(context,restaurant);

        String test = RestaurantData.getOpeningHour();
        assertEquals("No data", test);
    }


}
