package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
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
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelChat;
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
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestViewModel {
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private Location userLocation;
    @Mock
    private ViewModelUser viewModelUser;
    @Mock
    private ViewModelRestaurant viewModelRestaurant;
    @Mock
    private ViewModelChat viewModelChat;
    @Mock
    private Context context;



    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private final List<RestaurantDetails> fakeNearbyRestaurants = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private final List<RestaurantDetails> fakeBookedRestaurants = new ArrayList<>(FakeData.getFakeBookedRestaurant());
    private final List<Prediction> fakePredictionRestaurant = new ArrayList<>(FakeData.getFakePredictionRestaurant());
    private final List<Chat> fakeChat = new ArrayList<>(FakeData.getFakeChats());

    private final RestaurantDetails restaurantTest =
            new RestaurantDetails("99", "NameTest","TypeTest", "AddressTest",0.0,0.0,"phoneTest","websiteTest");


    private final User currentUser = new User("test","NameCurrentUser",null,"emailTest");

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        openMocks(this);

        when(userRepository.getCurrentUser()).thenReturn(currentUser);

        initAnswerUser();
        initAnswerRestaurant();
        initAnswerChat();

        viewModelUser = new ViewModelUser(userRepository,restaurantRepository);
        viewModelRestaurant = new ViewModelRestaurant(restaurantRepository,userRepository);
        viewModelChat = new ViewModelChat(chatRepository,userRepository);
    }

    private void initAnswerUser() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {

                User newUser = new User("test","NameCurrentUser",null,"emailTest");
                boolean isExisting = false;
                for (User user : fakeWorkmates) {
                    if (user.getUid().equals(newUser.getUid())) {
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
                fakeWorkmates.remove(currentUser);
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

    private void initAnswerRestaurant() {
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

    private void initAnswerChat() {
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
                User user = (User) args[0];
                Chat chat = (Chat) args[1];
                for (Message message : chat.getMessages()) {
                    message.readMessage(user);
                }

                return null;
            }
        }).when(chatRepository).readMessage(any(User.class),any(Chat.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Chat currentChat = (Chat) args[1];
                MutableLiveData<List<Message>> liveData = (MutableLiveData<List<Message>>) args[2];

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

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                User user = (User) args[0];
                MutableLiveData<Map<String,Integer>> liveData = (MutableLiveData<Map<String, Integer>>) args[1];
                liveData.setValue(UtilChatId.getNumberOfNoReadingMessage(currentUser,fakeChat));

                return null;
            }
        }).when(chatRepository).setPinsNoReadingMessage(any(User.class),any(MutableLiveData.class));

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

    @Test
    public void createChatBetweenUser() {
        List<User> users = new ArrayList<>();
        users.add(fakeWorkmates.get(0));
        users.add(fakeWorkmates.get(4));

        int chatsSize = fakeChat.size();

        viewModelChat.createChat(null,users);

        Chat currentChat = viewModelChat.getCurrentChatLivedata().getValue();

        assertEquals(chatsSize+1, fakeChat.size());
        assertNotNull(currentChat);
        assertTrue(fakeChat.contains(currentChat));
        assertEquals(users,currentChat.getUsers());

    }

    @Test
    public void createChatWithRestaurant() {
        List<User> users = new ArrayList<>();
        users.add(fakeWorkmates.get(0));
        users.add(fakeWorkmates.get(4));

        int chatsSize = fakeChat.size();

        viewModelChat.createChat(restaurantTest,users);

        Chat currentChat = viewModelChat.getCurrentChatLivedata().getValue();

        assertEquals(chatsSize+1, fakeChat.size());
        assertNotNull(currentChat);
        assertTrue(fakeChat.contains(currentChat));
        assertEquals(users,currentChat.getUsers());
        assertEquals(restaurantTest,currentChat.getRestaurant());
    }

    @Test
    public void getMessageFromChat() {
        viewModelChat.setAllMessageForChat(fakeChat.get(0));

        List<Message> messages = viewModelChat.getAllMessage().getValue();

        assertEquals(messages,fakeChat.get(0).getMessages());
        assertNotNull(messages);
    }


    @Test
    public void createMessageInChat() {
        String newMessage = "new message";
        int messageSize = fakeChat.get(0).getMessages().size();

        viewModelChat.createMessagesInChat(newMessage, fakeChat.get(0));

        assertEquals(messageSize+1, fakeChat.get(0).getMessages().size());
        assertEquals(fakeChat.get(0).getMessages().get(messageSize).getMessage(), newMessage);

    }

    @Test
    public void removeMessageInChat() {
        Chat currentChat = fakeChat.get(0);
        String messageToDelete = "Test du message";

        viewModelChat.createMessagesInChat(messageToDelete,currentChat);

        int messageSize = currentChat.getMessages().size();

        assertEquals(messageToDelete,currentChat.getMessages().get(messageSize-1).getMessage());

        Message messageTest = currentChat.getMessages().get(messageSize-1);

        assertFalse(messageTest.getIsDelete());

        viewModelChat.removeMessageInChat(messageTest,currentChat);
        assertTrue(messageTest.getIsDelete());

    }

    @Test
    public void readMessageInChat() {
        Chat currentChat = fakeChat.get(0);

        int messageSize = currentChat.getMessages().size();

        assertFalse(currentChat.getMessages().get(messageSize-1).getReadByUserId().contains(currentUser.getUid()));

        viewModelChat.readMessage(currentChat);

        assertTrue(currentChat.getMessages().get(messageSize-1).getReadByUserId().contains(currentUser.getUid()));

    }

    @Test
    public void getNumberOfNoReadingMessage() {
        List<User> userChat1 = new ArrayList<>();
        userChat1.add(fakeWorkmates.get(0));
        userChat1.add(currentUser);
        Chat chat1 = new Chat(null,userChat1);
        Message message10 = new Message("message10NoRead",fakeWorkmates.get(0));
        Message message11 = new Message("message11NoRead",fakeWorkmates.get(0));
        chat1.getMessages().add(message10);
        chat1.getMessages().add(message11);

        List<User> userChat2 = new ArrayList<>();
        userChat2.add(fakeWorkmates.get(1));
        userChat2.add(currentUser);
        Chat chat2 = new Chat(null, userChat2);
        Message message2 = new Message("message2Read",fakeWorkmates.get(0));
        message2.readMessage(currentUser);
        chat2.getMessages().add(message2);

        fakeChat.add(chat1);
        fakeChat.add(chat2);

        viewModelChat.setPinsNoReadingMessage(currentUser);
        Map<String,Integer> map = viewModelChat.getNumberNoReadingMessageMap().getValue();

        List<Integer> newListInteger = new ArrayList<>();
        for (User user : fakeWorkmates) {
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

}