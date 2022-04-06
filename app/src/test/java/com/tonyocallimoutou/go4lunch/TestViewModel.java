package com.tonyocallimoutou.go4lunch;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.test.FakeData;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
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


    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());

    private final RestaurantDetails restaurantTest =
            new RestaurantDetails("99", "NameTest","TypeTest", "AddressTest","phoneTest","websiteTest");


    private User currentUser = new User("test","NameCurrentUser",null);

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    public TestViewModel() {
    }

    @Before
    public void setup() {
        openMocks(this);

        when(userRepository.getCurrentUser()).thenReturn(currentUser);

        initAnswer();

        viewModelUser = new ViewModelUser(userRepository);
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
    }


    @Test
    public void getCurrentUser() {

        User user = viewModelUser.getCurrentUser();

        assertEquals(currentUser.getUid(), user.getUid());
        assertEquals(currentUser.getUsername(), user.getUsername());
        assertEquals(currentUser.getUrlPicture(), user.getUrlPicture());
    }

    @Test
    public void setWorkmatesLiveData() {

        viewModelUser.setWorkmatesList();

        LiveData<List<User>> test = viewModelUser.getWorkmates();

        assertEquals(fakeWorkmates.size(), test.getValue().size());
        assertEquals(test.getValue(), fakeWorkmates);
    }

    @Test
    public void setNearbyRestaurantsLiveData() {


        viewModelRestaurant.setNearbyPlace(userLocation);

        LiveData<List<RestaurantDetails>> liveData = viewModelRestaurant.getNearbyRestaurantLiveData();

        assertEquals(fakeNearbyRestaurants.size(), liveData.getValue().size());
        assertEquals(liveData.getValue(), fakeNearbyRestaurants);
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

}