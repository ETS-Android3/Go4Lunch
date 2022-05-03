package com.tonyocallimoutou.go4lunch.MainTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.repository.ChatRepository;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class MainActivityTest {


    private static UserRepository userRepository;
    private static RestaurantRepository restaurantRepository;
    private static ChatRepository chatRepository;

    private static final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private static final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private static final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    private static final List<Prediction> fakePredictionRestaurant = new ArrayList<>(FakeData.getFakePredictionRestaurant());
    private static User currentUser = new User("test","NameCurrentUser",null,"emailTest");

    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @BeforeClass
    public static void beforeAll() {
        userRepository = mock(UserRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        chatRepository = mock(ChatRepository.class);

        ViewModelFactory.InstrumentedTestInitFactory(userRepository,restaurantRepository,chatRepository);

        // User Repository
        when(userRepository.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.isCurrentLogged()).thenReturn(true);
        initAnswerUser();

        // Restaurant repository
        initAnswerRestaurant();

    }
    private static void initAnswerUser() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                boolean isExisting = false;
                for (User user : fakeWorkmates) {
                    if (user.getUid().equals(currentUser.getUid())) {
                        currentUser = user;
                        isExisting = true;
                    }
                }
                if (! isExisting) {
                    fakeWorkmates.add(currentUser);
                }
                return null;
            }
        }).when(userRepository).createUser();

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                currentUser = null;
                return null;
            }
        }).when(userRepository).signOut(any(Context.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                fakeWorkmates.remove(currentUser);
                currentUser = null;
                return null;
            }
        }).when(userRepository).deleteUser(any(Context.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String name = (String) args[0];
                currentUser.setUsername(name);
                return null;
            }
        }).when(userRepository).setNameOfCurrentUser(any(String.class));

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

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantDetails restaurant = (RestaurantDetails) args[0];

                List<String> listRestaurantId = currentUser.getLikeRestaurantId();
                if ( ! listRestaurantId.contains(restaurant.getPlaceId())) {
                    listRestaurantId.add(restaurant.getPlaceId());
                }

                currentUser.setLikeRestaurantId(listRestaurantId);
                return null;
            }
        }).when(userRepository).likeThisRestaurant(any(RestaurantDetails.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantDetails restaurant = (RestaurantDetails) args[0];

                List<String> listRestaurantId = currentUser.getLikeRestaurantId();
                listRestaurantId.remove(restaurant.getPlaceId());

                currentUser.setLikeRestaurantId(listRestaurantId);
                return null;
            }
        }).when(userRepository).dislikeThisRestaurant(any(RestaurantDetails.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<User> liveData = (MutableLiveData<User>) args[0];
                liveData.setValue(currentUser);
                return null;
            }
        }).when(userRepository).setCurrentUserLivedata(any(MutableLiveData.class));
    }

    private static void initAnswerRestaurant() {
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
                MutableLiveData<List<RestaurantDetails>> liveData = (MutableLiveData<List<RestaurantDetails>>) args[0];
                liveData.setValue(fakeBookedRestaurants);
                return null;
            }
        }).when(restaurantRepository).setBookedRestaurantFirestore(any(MutableLiveData.class));

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
                MutableLiveData<List<RestaurantDetails>> liveData = (MutableLiveData<List<RestaurantDetails>>) args[1];
                liveData.setValue(fakeNearbyRestaurants);
                return null;
            }
        }).when(restaurantRepository).setNearbyPlace(any(Location.class), any(MutableLiveData.class));

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
    public void CheckIfMainActivityHaveBottomNavBar() {
        onView(ViewMatchers.withId(R.id.bottom_nav_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void CheckIfSideViewIsCreated(){
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.logo_header_side_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_picture_header_side_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.navigation_setting))
                .check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.close());

        onView(withId(R.id.logo_header_side_view))
                .check(matches(not(isDisplayed())));

    }

    @Test
    public void CheckIfSideViewHaveInformationOfCurrentUser() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.user_name))
                .check(matches(withText(currentUser.getUsername())));

        onView(withId(R.id.user_email))
                .check(matches(withText(currentUser.getEmail())));
    }

    @Test
    public void goToSetting() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.navigation_setting))
                .perform(click());

        onView(withId(R.id.setting_host_fragment))
                .check(matches(isDisplayed()));
    }

    @Test
    public void goToRestaurantDetail() {

        onView(ViewMatchers.withId(R.id.navigation_list))
            .perform(click());

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.navigation_your_lunch))
                .perform(click());

        onView(withId(R.id.navigation_your_lunch))
                .check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.close());

        bookedRestaurant();

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.navigation_your_lunch))
                .perform(click());

        onView(withId(R.id.detail_relative_layout))
                .check(matches(isDisplayed()));


    }

    private void bookedRestaurant() {

        // Test List View Go to Detail Activity

        onView(withId(R.id.list_view_recycler_view))
                .perform(actionOnItemAtPosition(0,click()));

        // Test Detail Activity Booked Restaurant

        onView(withId(R.id.detail_booked_restaurant))
                .perform(click());

        // Return to Main Activity
        pressBack();


    }
}
