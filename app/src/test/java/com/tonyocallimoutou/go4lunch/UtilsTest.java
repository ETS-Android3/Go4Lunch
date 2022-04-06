package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;

import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.test.FakeData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UtilsTest {

    List<RestaurantDetails> nearbyRestaurant = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    List<RestaurantDetails> bookedRestaurant = new ArrayList<>(FakeData.getFakeBookedRestaurant());

    @Test
    public void FilterRestaurantIfBooked() {
        bookedRestaurant.clear();

        for (int i=1; i<nearbyRestaurant.size(); i++) {
            bookedRestaurant.add(nearbyRestaurant.get(i));
        }

        List <RestaurantDetails> listTest = RestaurantMethod.getNearbyRestaurantWithoutBooked(nearbyRestaurant,bookedRestaurant);
        List<RestaurantDetails> testAllRestaurant = new ArrayList<>();
        testAllRestaurant.addAll(listTest);
        testAllRestaurant.addAll(bookedRestaurant);


        assertEquals( nearbyRestaurant.size() -1, bookedRestaurant.size());
        assertEquals(1, listTest.size());

        assertEquals(testAllRestaurant.size(),nearbyRestaurant.size());

        for (int i=0; i<nearbyRestaurant.size() ; i++) {
            assertEquals(nearbyRestaurant.get(i), testAllRestaurant.get(i));
        }
    }

    @Test
    public void getDataFromRestaurant() {
        RestaurantDetails restaurant = nearbyRestaurant.get(0);

        List<String> workmatesId = new ArrayList<>();
        workmatesId.add("Test1");
        workmatesId.add("Test2");

        restaurant.setWorkmatesId(workmatesId);

        RestaurantData.newInstance(restaurant);

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
}
