package com.tonyocallimoutou.go4lunch.MockViewModel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.repository.ChatRepository;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.utils.UtilChatId;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class test {

    @Mock
    private static RestaurantRepository restaurantRepository;
    @Mock
    private static UserRepository userRepository;
    @Mock
    private static ChatRepository chatRepository;


    private static final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private static final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private static final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    private static final List<Prediction> fakePredictionRestaurant = new ArrayList<>(FakeData.getFakePredictionRestaurant());
    private static final List<Chat> fakeChat = new ArrayList<>(FakeData.getFakeChats());

    private static final User currentUser = new User("test","NameCurrentUser",null,"emailTest");

    public static RestaurantRepository getRestaurantRepository() {
        return restaurantRepository;
    }
    public static UserRepository getUserRepository() {
        return userRepository;
    }
    public static ChatRepository getChatRepository() {
        return chatRepository;
    }
    public static User getCurrentUser() {
        return currentUser;
    }

    public static List<User> getFakeWorkmates() {
        return fakeWorkmates;
    }

    public static List<RestaurantDetails> getFakeNearbyRestaurants() {
        return fakeNearbyRestaurants;
    }

    public static List<RestaurantDetails> getFakeBookedRestaurants() {
        return fakeBookedRestaurants;
    }

    public static List<Prediction> getFakePredictionRestaurant() {
        return fakePredictionRestaurant;
    }

    public static List<Chat> getFakeChat() {
        return fakeChat;
    }

    public static void initAnswerForViewModel() {
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

    }

}
