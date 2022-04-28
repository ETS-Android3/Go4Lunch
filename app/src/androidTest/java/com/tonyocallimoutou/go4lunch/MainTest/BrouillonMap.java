package com.tonyocallimoutou.go4lunch.MainTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.android.dx.mockito.inline.extended.ExtendedMockito.doAnswer;
import static com.android.dx.mockito.inline.extended.ExtendedMockito.doNothing;
import static com.android.dx.mockito.inline.extended.ExtendedMockito.mockitoSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.repository.ChatRepository;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelChat;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoSession;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class BrouillonMap {

    ViewModelUser viewModelUser;
    ViewModelRestaurant viewModelRestaurant;
    ViewModelChat viewModelChat;
    ViewModelFactory viewModelFactory;

    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    private final List<Prediction> fakePredictionRestaurant = new ArrayList<>(FakeData.getFakePredictionRestaurant());
    private final List<Chat> fakeChat = new ArrayList<>(FakeData.getFakeChats());

    private final RestaurantDetails restaurantTest =
            new RestaurantDetails("99", "NameTest","TypeTest", "AddressTest",0.0,0.0,"phoneTest","websiteTest");

    private User currentUser;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> workmatesLiveData = new MutableLiveData<>();


    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);


    @Before
    public void init() {
        initMocks(this);

        viewModelUser = mock(ViewModelUser.class);
        viewModelRestaurant = mock(ViewModelRestaurant.class);
        viewModelChat = mock(ViewModelChat.class);
        viewModelFactory = mock(ViewModelFactory.class);
        initAnswer();
        initFactory();

        when(viewModelUser.getCurrentUser()).thenReturn(currentUser);
        when(viewModelUser.getCurrentUserLiveData()).thenReturn(userLiveData);
        when(viewModelUser.isCurrentLogged()).thenReturn(true);
        when(viewModelUser.getWorkmates()).thenReturn(workmatesLiveData);
    }

    private void initAnswer() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                userLiveData.setValue(currentUser);
                return null;
            }
        }).when(viewModelUser).setCurrentUserLiveData();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                currentUser = new User("test","NameCurrentUser",null,"emailTest");
                return null;
            }
        }).when(viewModelUser).createUser();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                workmatesLiveData.setValue(fakeWorkmates);
                return null;
            }
        }).when(viewModelUser).setWorkmatesList();

    }

    private void initFactory() {

        when(viewModelFactory.create(any(Class.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                if (args.getClass().isAssignableFrom(ViewModelUser.class)) {
                    return viewModelUser;
                }
                else if (args.getClass().isAssignableFrom(ViewModelRestaurant.class)) {
                    return viewModelRestaurant;
                }
                else if (args.getClass().isAssignableFrom(ViewModelChat.class)) {
                    return viewModelChat;
                }
                else {
                    return null;
                }
            }
        });
    }

    @Test
    public void CheckIfListIsNotEmpty() {
        MockitoSession session = mockitoSession().mockStatic(ViewModelFactory.class).startMocking();

        try {
            lenient().when(ViewModelFactory.getInstance()).thenReturn(viewModelFactory);

            onView(withContentDescription("Navigate up"))
                    .perform(click());

            onView(withId(R.id.logo_header_side_view))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.profile_picture_header_side_view))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.navigation_setting))
                    .check(matches(isDisplayed()));

        } finally {
            session.finishMocking();
        }

    }

    @Test
    public void checkIfInformationIsCorrect() {
    }

    @Test
    public void checkGoToDetailActivity() {
    }
}