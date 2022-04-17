package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.Geometry;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.test.FakeData;
import com.tonyocallimoutou.go4lunch.utils.CompareRestaurant;
import com.tonyocallimoutou.go4lunch.utils.PredictionOfWorkmates;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;
import com.tonyocallimoutou.go4lunch.utils.UtilDistance;
import com.tonyocallimoutou.go4lunch.utils.WorkmatesLunch;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TestUtils {

    List<RestaurantDetails> nearbyRestaurant = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    List<RestaurantDetails> bookedRestaurant = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    List<User> workmates = new ArrayList<>(FakeData.getFakeWorkmates());

    @Before
    public void init() {
        openMocks(this);
    }

    @Test
    public void getNearbyRestaurantWithoutBooked() {
        bookedRestaurant.clear();

        for (int i=1; i<nearbyRestaurant.size(); i++) {
            bookedRestaurant.add(nearbyRestaurant.get(i));
        }

        List<RestaurantDetails> listTest = RestaurantMethod.getNearbyRestaurantWithoutBooked(nearbyRestaurant,bookedRestaurant);
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
    public void compareRestaurant() {

        assertFalse(CompareRestaurant.ListRestaurantIsEqual(nearbyRestaurant,bookedRestaurant));

        List<RestaurantDetails> listTest = new ArrayList<>();

        assertFalse(CompareRestaurant.ListRestaurantIsEqual(nearbyRestaurant,listTest));

        listTest.addAll(nearbyRestaurant);

        assertNotSame(listTest, nearbyRestaurant);
        assertTrue(CompareRestaurant.ListRestaurantIsEqual(nearbyRestaurant,listTest));

    }

    @Test
    public void getPredictionOfWorkmates() {
        List<User> workmates = Arrays.asList(
                new User("test1","Jean",null),
                new User("test2","JEAN",null),
                new User("test3","Jeannette",null),
                new User("test4","Jules",null),
                new User("test5","Patrick",null)
        );

        List<Prediction> predictions = PredictionOfWorkmates.getWorkmates("jea",workmates);
        List<Prediction> predictions2 = PredictionOfWorkmates.getWorkmates("p",workmates);


        assertEquals(3,predictions.size());
        for (int i=0; i<3; i++) {
            assertEquals(workmates.get(i).getUid(), predictions.get(i).getPlaceId());
            assertEquals(workmates.get(i).getUsername(), predictions.get(i).getDescription());
        }

        assertEquals(1,predictions2.size());
        assertEquals("test5", predictions2.get(0).getPlaceId());
        assertEquals("Patrick", predictions2.get(0).getDescription());
    }

    @Test
    public void roundToNearestFiftyMeters() {
        Location locationWitness = new Location("");
        locationWitness.setLatitude(32.59999);
        locationWitness.setLongitude(99.566677);


        Location locationTest = new Location("");
        locationTest.setLatitude(locationWitness.getLatitude());
        locationTest.setLongitude(locationWitness.getLongitude());

        UtilDistance.roundToNearestFiftyMeters(locationTest);

        assertSame(locationTest.getLongitude(),locationWitness.getLatitude());
        assertEquals(50, locationWitness.distanceTo(locationTest), 0.0);
    }

    @Test
    public void distanceWithRestaurant() {
        Location locationWitness = new Location("");
        locationWitness.setLatitude(32.59999);
        locationWitness.setLongitude(99.566677);

        Location locationTest = new Location("");
        locationTest.setLatitude(31);
        locationTest.setLongitude(98);

        int distance = Math.round(locationWitness.distanceTo(locationTest));

        RestaurantDetails restaurant = new RestaurantDetails();

        Geometry geo = new Geometry();
        com.tonyocallimoutou.go4lunch.model.places.Location location = new com.tonyocallimoutou.go4lunch.model.places.Location();
        location.setLat(locationTest.getLatitude());
        location.setLng(locationTest.getLongitude());
        geo.setLocation(location);

        restaurant.setGeometry(geo);

        int distanceRestaurant = UtilDistance.getDistanceWithRestaurant(locationWitness,restaurant);

        assertEquals(distance, distanceRestaurant);
    }


    @Test
    public void WorkmatesLunch() {
        RestaurantDetails restaurant = nearbyRestaurant.get(0);
        List<User> workmatesLunch = WorkmatesLunch.getWorkmatesLunch(restaurant,workmates);

        assertEquals(0, workmatesLunch.size());

        List<String> workmatesId = new ArrayList<>();
        for (User user : workmates) {
            user.setBookedRestaurant(restaurant);
            workmatesId.add(user.getUid());
        }
        restaurant.setWorkmatesId(workmatesId);

        workmatesLunch.clear();
        workmatesLunch = WorkmatesLunch.getWorkmatesLunch(restaurant,workmates);

        assertEquals(workmates, workmatesLunch);

    }
}
