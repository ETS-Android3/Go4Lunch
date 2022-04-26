package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.utils.CompareRestaurant;
import com.tonyocallimoutou.go4lunch.utils.PredictionOfWorkmates;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;
import com.tonyocallimoutou.go4lunch.utils.WorkmatesLunch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TestUtils {



    List<RestaurantDetails> nearbyRestaurant = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    List<RestaurantDetails> bookedRestaurant = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    List<User> workmates = new ArrayList<>(FakeData.getFakeWorkmates());


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
                new User("test1","Jean",null,"emailTest1"),
                new User("test2","JEAN",null,"emailTest2"),
                new User("test3","Jeannette",null,"emailTest3"),
                new User("test4","Jules",null,"emailTest1"),
                new User("test5","Patrick",null,"emailTest1")
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

    @Test
    public void getIdOfChat() {
        RestaurantDetails restaurant = nearbyRestaurant.get(0);
        List<User> userAlone = new ArrayList<>();
        userAlone.add(workmates.get(0));
        List<User> users = workmates;

        Chat chat1 = new Chat(restaurant,users);
        Chat chat2 = new Chat(null, userAlone);
        Chat chat3 = new Chat(null, users);

        String strChat1 = "restaurantId:" + restaurant.getPlaceId();
        String strChat2 = workmates.get(0).getUid();

        String strChat3 = "";
        for (User user : users) {
            strChat3 += user.getUid();
        }

        assertEquals(strChat1,chat1.getId());
        assertEquals(strChat2,chat2.getId());
        assertEquals(strChat3,chat3.getId());
    }
}
