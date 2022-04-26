package com.tonyocallimoutou.go4lunch.FAKE;

import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FakeData {

    private static final User user1 = new User("1","Tonyo",null,"emailTonyo");
    private static final User user2 = new User("2","Jennifer",null,"emailJennifer");
    private static final User user3 = new User("3","Marc",null,"emailMarc");
    private static final User user4 =  new User("4","Alfred",null,"emailAlfred");
    private static final User user5 = new User("5","Jean",null,"emailJean");
    private static final User user6 = new User("6","Belle",null,"emailBelle");


    private static final List<User> FAKE_WORKMATES = Arrays.asList(
            user1,
            user2,
            user3,
            user4,
            user5,
            user6
    );

    private static final List<RestaurantDetails> FAKE_NEARBY_RESTAURANT = Arrays.asList(
            new RestaurantDetails("FAKE1","name1","type1","address1",0.0,0.0,"phoneNumber1","website1"),
            new RestaurantDetails("FAKE2","name2","type2","address2",0.0,0.0,"phoneNumber2","website2"),
            new RestaurantDetails("FAKE3","name3","type3","address3",0.0,0.0,"phoneNumber3","website3"),
            new RestaurantDetails("FAKE4","name4","type4","address4",0.0,0.0,"phoneNumber4","website4"),
            new RestaurantDetails("FAKE5","name5","type5","address5",0.0,0.0,"phoneNumber5","website5")
    );

    private static final List<RestaurantDetails> FAKE_BOOKED_RESTAURANT = Arrays.asList(
            new RestaurantDetails("FAKE1","name1","type1","address1",0.0,0.0,"phoneNumber1","website1"),
            new RestaurantDetails("FAKE3","name3","type3","address3",0.0,0.0,"phoneNumber3","website3"),
            new RestaurantDetails("FAKE0","name0","type0","address0",0.0,0.0,"phoneNumber0","website0")
    );

    private static final List<Prediction> FAKE_PREDICTION_RESTAURANT = Arrays.asList(
            new Prediction("FAKE1","nameOfPrediction1"),
            new Prediction("FAKE2","nameOfPrediction2"),
            new Prediction("FAKE3","nameOfPrediction3"),
            new Prediction("FAKE4","nameOfPrediction4"),
            new Prediction("FAKE5","nameOfPrediction5")
    );

    private static final List<Chat> FAKE_CHATS = Arrays.asList(
            new Chat(null,FAKE_WORKMATES),
            new Chat(null, new ArrayList<>(Arrays.asList(user1,user2))),
            new Chat(null,new ArrayList<>(Arrays.asList(user3,user4))),
            new Chat(null,new ArrayList<>(Arrays.asList(user5,user6))),
            new Chat(null,new ArrayList<>(Arrays.asList(user3,user6)))
    );

    private static final List<Message> FAKE_MESSAGES_CHAT1 = Arrays.asList(
            new Message("Chat1 message1",user1),
            new Message("Chat1 message2", user2)
    );
    private static final List<Message> FAKE_MESSAGES_CHAT2 = Arrays.asList(
            new Message("Chat2 message1",user3),
            new Message("Chat2 message2", user4)
    );
    private static final List<Message> FAKE_MESSAGES_CHAT3 = Arrays.asList(
            new Message("Chat3 message1",user5),
            new Message("Chat3 message2", user6)
    );
    private static final List<Message> FAKE_MESSAGES_CHAT4 = Arrays.asList(
            new Message("Chat4 message1",user3),
            new Message("Chat4 message2", user6)
    );

    public static List<User> getFakeWorkmates() {
        return FAKE_WORKMATES;
    }
    public static List<RestaurantDetails> getFakeNearbyRestaurant() {
        return FAKE_NEARBY_RESTAURANT;
    }
    public static List<RestaurantDetails> getFakeBookedRestaurant() {
        return FAKE_BOOKED_RESTAURANT;
    }
    public static List<Prediction> getFakePredictionRestaurant() {
        return FAKE_PREDICTION_RESTAURANT;
    }
    public static List<Chat> getFakeChats() {
        FAKE_CHATS.get(0).setMessages(FAKE_MESSAGES_CHAT1);
        FAKE_CHATS.get(1).setMessages(FAKE_MESSAGES_CHAT2);
        FAKE_CHATS.get(2).setMessages(FAKE_MESSAGES_CHAT3);
        FAKE_CHATS.get(3).setMessages(FAKE_MESSAGES_CHAT4);
        return FAKE_CHATS;
    }
}
