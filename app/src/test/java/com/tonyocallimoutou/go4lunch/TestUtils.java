package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.utils.CompareRestaurant;
import com.tonyocallimoutou.go4lunch.utils.PredictionOfWorkmates;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;
import com.tonyocallimoutou.go4lunch.utils.UtilChatId;
import com.tonyocallimoutou.go4lunch.utils.WorkmatesLunch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Test
    public void getHashMapOfUserIdAndNumberOfNoReadingMessage() {
        User currentUser = new User("currentUser","currentUserName",null,"emailCurrentUser");
        List<User> userChat1 = new ArrayList<>();
        userChat1.add(workmates.get(0));
        userChat1.add(currentUser);
        Chat chat1 = new Chat(null,userChat1);
        Message message10 = new Message("message10NoRead",workmates.get(0));
        Message message11 = new Message("message11NoRead",workmates.get(0));
        chat1.getMessages().add(message10);
        chat1.getMessages().add(message11);

        List<User> userChat2 = new ArrayList<>();
        userChat2.add(workmates.get(1));
        userChat2.add(currentUser);
        Chat chat2 = new Chat(null, userChat2);
        Message message2 = new Message("message2Read",workmates.get(0));
        message2.readMessage(currentUser);
        chat2.getMessages().add(message2);

        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat1);
        chatList.add(chat2);

        Map<String, Integer> map = UtilChatId.getNumberOfNoReadingMessage(currentUser,chatList);

        List<Integer> newListInteger = new ArrayList<>();
        for (User user : workmates) {
            if (map.get(user.getUid()) != null) {
                newListInteger.add(map.get(user.getUid()));
            }
            else {
                newListInteger.add(0);
            }
        }

        assertEquals(2, (int) newListInteger.get(0));
        assertEquals(0, (int) newListInteger.get(1));
        assertEquals(0,(int) newListInteger.get(2));
    }

    @Test
    public void getHashMapOfRestaurantIdAndNumberOfNoReadingMessage() {
        User currentUser = new User("currentUser","currentUserName",null,"emailCurrentUser");
        currentUser.setBookedRestaurant(bookedRestaurant.get(0));
        List<User> userChat3 = new ArrayList<>();
        userChat3.add(workmates.get(2));
        userChat3.add(currentUser);
        Chat chat3 = new Chat(bookedRestaurant.get(0), userChat3);
        Message message3 = new Message("message3NoRead",workmates.get(0));
        chat3.getMessages().add(message3);

        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat3);

        Map<String, Integer> map = UtilChatId.getNumberOfNoReadingMessage(currentUser,chatList);

        List<Integer> newListInteger = new ArrayList<>();
        for (RestaurantDetails restaurant : bookedRestaurant) {
            if (map.get(restaurant.getPlaceId()) != null) {
                newListInteger.add(map.get(restaurant.getPlaceId()));
            }
            else {
                newListInteger.add(0);
            }
        }

        assertEquals(1, (int) newListInteger.get(0));
        assertEquals(0, (int) newListInteger.get(1));
    }
}
