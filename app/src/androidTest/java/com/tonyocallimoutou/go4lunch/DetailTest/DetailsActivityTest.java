package com.tonyocallimoutou.go4lunch.DetailTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.repository.ChatRepository;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.utils.UtilChatId;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivityTest {

    private static UserRepository userRepository;
    private static RestaurantRepository restaurantRepository;
    private static ChatRepository chatRepository;

    private static final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private static final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private static final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    private static final List<Prediction> fakePredictionRestaurant = new ArrayList<>(FakeData.getFakePredictionRestaurant());
    private static final List<Chat> fakeChat = new ArrayList<>(FakeData.getFakeChats());
    private static User currentUser = new User("test","NameCurrentUser",null,"emailTest");

    private static final RestaurantDetails restaurantTest =
            new RestaurantDetails("99", "NameTest","TypeTest", "AddressTest",0.0,0.0,"phoneTest","websiteTest");


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

        // Chat repository
        initAnswerChat();
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

    private static void initAnswerChat() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantDetails restaurant = (RestaurantDetails) args[0];
                List<User> users = (List<User>) args[1];
                MutableLiveData<Chat> liveData = (MutableLiveData<Chat>) args[2];

                String id = UtilChatId.getChatIdWithUsers(restaurant, users);

                boolean isExisting = false;
                for (Chat chat : fakeChat) {
                    if (chat.getId().equals(id)) {
                        isExisting = true;
                        liveData.setValue(chat);
                    }
                }
                if (!isExisting) {
                    Chat chat = new Chat(restaurant,users);
                    fakeChat.add(chat);
                    liveData.setValue(chat);
                }

                return null;
            }
        }).when(chatRepository).createChat(nullable(RestaurantDetails.class),any(List.class),any(MutableLiveData.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String message = (String) args[0];
                User user = (User) args[1];
                Chat currentChat = (Chat) args[2];

                Message newMessage = new Message(message,user);

                List<Message> list = new ArrayList<>();
                for (Chat chat : fakeChat) {
                    if (chat.getId().equals(currentChat.getId())) {
                        list.addAll(chat.getMessages());
                        list.add(newMessage);
                        chat.setMessages(list);
                    }
                }

                return null;
            }
        }).when(chatRepository).createMessagesInChat(any(String.class),any(User.class),any(Chat.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Chat currentChat = (Chat) args[0];
                MutableLiveData<List<Message>> liveData = (MutableLiveData<List<Message>>) args[1];

                for (Chat chat : fakeChat) {
                    if (chat.getId().equals(currentChat.getId())) {
                        liveData.setValue(chat.getMessages());
                    }
                }

                return null;
            }
        }).when(chatRepository).getAllMessageForChat(any(Chat.class),any(MutableLiveData.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Message messageToDelete = (Message) args[0];
                Chat currentChat = (Chat) args[1];
                User user = (User) args[2];

                for (Message message : currentChat.getMessages()) {
                    if (message.equals(messageToDelete)) {
                        message.delete(user);
                    }
                }
                return null;
            }
        }).when(chatRepository).removeMessageInChat(any(Message.class),any(Chat.class),any(User.class));

    }

    @Before
    public void GoToDetailActivity() {
        mainScenarioRule.getScenario().onActivity(activity -> {
            DetailsActivity.navigate(activity,restaurantTest);
        });
    }

    @Test
    public void CheckIfDetailActivityHaveInformation() {
        onView(ViewMatchers.withId(R.id.detail_name_restaurant))
                .check(matches((withText(restaurantTest.getName()))));

        String str = restaurantTest.getTypes() + "-" + restaurantTest.getVicinity();
        onView(withId(R.id.detail_restaurant_address))
                .check(matches(not(withText(str))));
    }

    @Test
    public void CheckIfListIsUpdate() {

        onView(withId(R.id.detail_recycler_view))
                .check(matches(hasMinimumChildCount(0)));

        onView(withId(R.id.lbl_no_workmates))
                .check(matches(isDisplayed()));

        onView(withId(R.id.detail_booked_restaurant))
                .perform(click());

        onView(withId(R.id.detail_recycler_view))
                .check(matches(hasMinimumChildCount(1)));

        onView(withId(R.id.lbl_no_workmates))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.detail_booked_restaurant))
                .perform(click());

    }

    @Test
    public void likeThisRestaurant() {

        onView(withId(R.id.detail_like))
                .perform(click());

        assertTrue(currentUser.getLikeRestaurantId().contains(restaurantTest.getPlaceId()));
    }


    @Test
    public void goToChatActivity() {
        onView(withId(R.id.fab_chat_restaurant))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.detail_booked_restaurant))
                .perform(click());

        onView(withId(R.id.fab_chat_restaurant))
                .perform(click());

        onView(withId(R.id.chat_text_resume))
                .check(matches(isDisplayed()));
    }

}

