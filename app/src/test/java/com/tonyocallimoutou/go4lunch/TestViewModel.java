package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.test.FakeData;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestViewModel {
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Location userLocation;
    @Mock
    private ViewModelUser viewModelUser;
    @Mock
    private ViewModelRestaurant viewModelRestaurant;
    @Mock
    private Context context;



    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    private final List<Prediction> fakePredictionRestaurant = new ArrayList<>(FakeData.getFakePredictionRestaurant());

    private final RestaurantDetails restaurantTest =
            new RestaurantDetails("99", "NameTest","TypeTest", "AddressTest","phoneTest","websiteTest");


    private User currentUser = new User("test","NameCurrentUser",null,"emailTest");

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        openMocks(this);

        when(userRepository.getCurrentUser()).thenReturn(currentUser);

        initAnswer();

        viewModelUser = new ViewModelUser(userRepository,restaurantRepository);
        viewModelRestaurant = new ViewModelRestaurant(restaurantRepository,userRepository);
    }

    private void initAnswer() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<List<RestaurantDetails>> liveData = (MutableLiveData<List<RestaurantDetails>>) args[1];
                liveData.setValue(fakeNearbyRestaurants);
                return null;
            }
        }).when(restaurantRepository).setNearbyPlace(any(Location.class), any(MutableLiveData.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<List<RestaurantDetails>> liveData = (MutableLiveData<List<RestaurantDetails>>) args[0];
                liveData.setValue(fakeBookedRestaurants);
                return null;
            }
        }).when(restaurantRepository).setBookedRestaurantFirestore(any(MutableLiveData.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RestaurantDetails arg = invocation.getArgument(0);

                RestaurantDetails restaurantToRemove = new RestaurantDetails();

                for (RestaurantDetails restaurant : fakeBookedRestaurants) {
                    if (restaurant.getPlaceId().equals(arg.getPlaceId())) {
                        restaurantToRemove = restaurant;
                    }
                }
                fakeBookedRestaurants.remove(restaurantToRemove);
                fakeBookedRestaurants.add(arg);

                return null;
            }
        }).when(restaurantRepository).createBookedRestaurantInFirebase(any(RestaurantDetails.class));


        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RestaurantDetails arg = invocation.getArgument(0);

                RestaurantDetails restaurantToRemove = new RestaurantDetails();
                for (RestaurantDetails restaurant : fakeBookedRestaurants) {
                    if (restaurant.getPlaceId().equals(arg.getPlaceId())) {
                        restaurantToRemove = restaurant;
                    }
                }
                fakeBookedRestaurants.remove(restaurantToRemove);
                if (arg.getWorkmatesId().size() != 0) {
                    fakeBookedRestaurants.add(arg);
                }

                return null;
            }
        }).when(restaurantRepository).cancelBookedRestaurantInFirebase(any(RestaurantDetails.class));


        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<List<User>> liveData = (MutableLiveData<List<User>>) args[0];
                liveData.setValue(fakeWorkmates);
                return null;
            }
        }).when(userRepository).setWorkmatesList(any(MutableLiveData.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<User> listTest = new ArrayList<>();
                listTest.addAll(fakeWorkmates);
                for (User user : listTest) {
                    if (user.getUid().equals(currentUser.getUid())) {
                        fakeWorkmates.remove(user);
                        fakeWorkmates.add(currentUser);
                    }
                }
                if (! fakeWorkmates.contains(currentUser)) {
                    fakeWorkmates.add(currentUser);
                }
                return null;
            }
        }).when(userRepository).createUser();


        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<User> listTest = new ArrayList<>();
                listTest.addAll(fakeWorkmates);
                for (User user : listTest) {
                    if (user.getUid().equals(currentUser.getUid())) {
                        fakeWorkmates.remove(user);
                    }
                }
                return null;
            }
        }).when(userRepository).deleteUser(any(Context.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String newName = (String) args[0];
                currentUser.setUsername(newName);
                return null;
            }
        }).when(userRepository).setNameOfCurrentUser(any(String.class));


        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantDetails restaurant = (RestaurantDetails) args[0];
                currentUser.setBookedRestaurant(restaurant);
                return null;
            }
        }).when(userRepository).bookedRestaurant(any(RestaurantDetails.class));


        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                currentUser.setBookedRestaurant(null);
                return null;
            }
        }).when(userRepository).cancelRestaurant();

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantDetails restaurant = (RestaurantDetails) args[0];
                if (! currentUser.getLikeRestaurantId().contains(restaurant.getPlaceId())) {
                    currentUser.getLikeRestaurantId().add(restaurant.getPlaceId());
                }
                return null;
            }
        }).when(userRepository).likeThisRestaurant(any(RestaurantDetails.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantDetails restaurant = (RestaurantDetails) args[0];
                if (currentUser.getLikeRestaurantId().contains(restaurant.getPlaceId())) {
                    currentUser.getLikeRestaurantId().remove(restaurant.getPlaceId());
                }
                return null;
            }
        }).when(userRepository).dislikeThisRestaurant(any(RestaurantDetails.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<List<Prediction>> liveData = (MutableLiveData<List<Prediction>>) args[2];
                liveData.postValue(fakePredictionRestaurant);
                return null;
            }
        }).when(restaurantRepository).setSearchRestaurant(any(),any(),any(MutableLiveData.class));

    }

    @Test
    public void getCurrentUser() {

        User user = viewModelUser.getCurrentUser();

        assertEquals(currentUser.getUid(), user.getUid());
        assertEquals(currentUser.getUsername(), user.getUsername());
        assertEquals(currentUser.getUrlPicture(), user.getUrlPicture());
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    public void createUser() {
        int workmatesSize = fakeWorkmates.size();
        for (User user : fakeWorkmates) {
            assertNotEquals(user.getUid(), currentUser.getUid());
        }
        viewModelUser.createUser();
        assertEquals(workmatesSize+1, fakeWorkmates.size());
        assertTrue(fakeWorkmates.contains(currentUser));
    }

    @Test
    public void deleteUser() {
        viewModelUser.createUser();
        assertTrue(fakeWorkmates.contains(currentUser));

        viewModelUser.deleteUser(context);

        assertFalse(fakeWorkmates.contains(currentUser));
        for (User user : fakeWorkmates) {
            assertNotEquals(user.getUid(), currentUser.getUid());
        }
    }

    @Test
    public void setName() {
        assertEquals("NameCurrentUser", currentUser.getUsername());

        viewModelUser.setNameOfCurrentUser("newNameTest");
        assertEquals("newNameTest", currentUser.getUsername());
    }

    @Test
    public void setWorkmatesLiveData() {

        viewModelUser.setWorkmatesList();

        LiveData<List<User>> test = viewModelUser.getWorkmates();

        assertEquals(fakeWorkmates.size(), test.getValue().size());
        assertEquals(fakeWorkmates, test.getValue());
    }

    @Test
    public void setNearbyRestaurantsLiveData() {

        viewModelRestaurant.setNearbyPlace(userLocation);

        LiveData<List<RestaurantDetails>> liveData = viewModelRestaurant.getNearbyRestaurantLiveData();

        assertEquals(fakeNearbyRestaurants.size(), liveData.getValue().size());
        assertEquals(fakeNearbyRestaurants, liveData.getValue());
    }


    @Test
    public void setBookedRestaurantsLiveData() {


        viewModelRestaurant.setBookedRestaurantList();

        LiveData<List<RestaurantDetails>> liveData = viewModelRestaurant.getBookedRestaurantLiveData();

        assertEquals(fakeBookedRestaurants.size(), liveData.getValue().size());
        assertEquals(liveData.getValue(), fakeBookedRestaurants);
    }


    @Test
    public void getRestaurantOfCurrentUser() {

        assertNull(viewModelRestaurant.getRestaurantOfCurrentUser());

        currentUser.setBookedRestaurant(restaurantTest);
        assertEquals(currentUser,userRepository.getCurrentUser());
        assertEquals(restaurantTest, viewModelRestaurant.getRestaurantOfCurrentUser());
    }

    @Test
    public void bookedRestaurant(){

        assertNull(viewModelRestaurant.getRestaurantOfCurrentUser());

        viewModelRestaurant.bookedThisRestaurant(restaurantTest);
        assertEquals(restaurantTest, viewModelRestaurant.getRestaurantOfCurrentUser());
    }

    @Test
    public void cancelRestaurant() {

        viewModelRestaurant.bookedThisRestaurant(restaurantTest);
        assertEquals(restaurantTest, viewModelRestaurant.getRestaurantOfCurrentUser());

        viewModelRestaurant.cancelBookedRestaurant(restaurantTest);
        assertNull(viewModelRestaurant.getRestaurantOfCurrentUser());
    }

    @Test
    public void WhenBookedRestaurantLiveDataChanged() {

        viewModelRestaurant.setBookedRestaurantList();
        LiveData<List<RestaurantDetails>> liveData = viewModelRestaurant.getBookedRestaurantLiveData();

        assertEquals(fakeBookedRestaurants.size(), liveData.getValue().size());
        assertEquals(liveData.getValue(), fakeBookedRestaurants);

        assertFalse(liveData.getValue().contains(restaurantTest));

        viewModelRestaurant.bookedThisRestaurant(restaurantTest);

        assertTrue(liveData.getValue().contains(restaurantTest));
    }

    @Test
    public void WhenCancelRestaurantLiveDataChanged() {

        viewModelRestaurant.setBookedRestaurantList();
        LiveData<List<RestaurantDetails>> liveData = viewModelRestaurant.getBookedRestaurantLiveData();

        int sizeOfFakeBookedRestaurant = fakeBookedRestaurants.size();

        assertEquals(sizeOfFakeBookedRestaurant, liveData.getValue().size());
        assertEquals(liveData.getValue(), fakeBookedRestaurants);

        RestaurantDetails restaurantToRemove = fakeBookedRestaurants.get(0);

        assertTrue(liveData.getValue().contains(restaurantToRemove));
        viewModelRestaurant.cancelBookedRestaurant(restaurantToRemove);


        assertEquals(sizeOfFakeBookedRestaurant -1, liveData.getValue().size());
        assertFalse(liveData.getValue().contains(restaurantToRemove));
    }

    @Test
    public void WhenBookedRestaurantChangedWorkmatesList() {

        int sizeWorkmate = restaurantTest.getWorkmatesId().size();

        viewModelRestaurant.bookedThisRestaurant(restaurantTest);

        assertEquals(sizeWorkmate+1, restaurantTest.getWorkmatesId().size());
        assertTrue(restaurantTest.getWorkmatesId().contains(currentUser.getUid()));
    }

    @Test
    public void WhenCancelRestaurantChangedWorkmatesList() {

        viewModelRestaurant.bookedThisRestaurant(restaurantTest);
        int sizeWorkmate = restaurantTest.getWorkmatesId().size();
        assertTrue(restaurantTest.getWorkmatesId().contains(currentUser.getUid()));


        viewModelRestaurant.cancelBookedRestaurant(restaurantTest);

        assertEquals(sizeWorkmate-1, restaurantTest.getWorkmatesId().size());
        assertFalse(restaurantTest.getWorkmatesId().contains(currentUser.getUid()));
    }

    @Test
    public void likeThisRestaurant() {
        assertEquals(0, currentUser.getLikeRestaurantId().size());

        viewModelRestaurant.likeThisRestaurant(restaurantTest);

        assertEquals(1,currentUser.getLikeRestaurantId().size());
        assertEquals(restaurantTest.getPlaceId(),currentUser.getLikeRestaurantId().get(0));
    }

    @Test
    public void dislikeRestaurant() {
        viewModelRestaurant.likeThisRestaurant(restaurantTest);

        assertEquals(1,currentUser.getLikeRestaurantId().size());
        assertEquals(restaurantTest.getPlaceId(),currentUser.getLikeRestaurantId().get(0));

        viewModelRestaurant.dislikeThisRestaurant(restaurantTest);
        assertEquals(0,currentUser.getLikeRestaurantId().size());

    }

    @Test
    public void setSearchRestaurant() {
        viewModelRestaurant.setSearchRestaurant("test");

        LiveData<List<Prediction>> liveData = viewModelRestaurant.getPredictionLiveData();

        assertEquals(fakePredictionRestaurant, liveData.getValue());
        assertEquals(fakePredictionRestaurant.size(), liveData.getValue().size());
    }

    @Test
    public void setSearchWorkmates() {
        List<User> newUsers = Arrays.asList(
                new User("test1","TEST1",null,"email1"),
                new User("test2","Test2",null,"email2"),
                new User("test3","test3",null,"email3")
        );

        fakeWorkmates.addAll(newUsers);

        viewModelRestaurant.setSearchWorkmates("tes",fakeWorkmates);


        LiveData<List<Prediction>> liveData = viewModelRestaurant.getPredictionLiveData();

        // JEAN1 - JEAN2 - JEAN3
        assertEquals(3, liveData.getValue().size());
        for (int i=0; i<3; i++) {
            assertEquals(liveData.getValue().get(i).getPlaceId(), newUsers.get(i).getUid());
            assertEquals(liveData.getValue().get(i).getDescription(), newUsers.get(i).getUsername());
        }
    }

}