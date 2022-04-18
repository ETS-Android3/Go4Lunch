package com.tonyocallimoutou.go4lunch.test;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;

import java.util.Arrays;
import java.util.List;

public class FakeData {

    private static final List<User> FAKE_WORKMATES = Arrays.asList(
            new User("1","Tonyo",null,"emailTonyo"),
            new User("2","Jennifer",null,"emailJennifer"),
            new User("3","Marc",null,"emailMarc"),
            new User("4","Alfred",null,"emailAlfred"),
            new User("5","Jean",null,"emailJean"),
            new User("6","Belle",null,"emailBelle")
    );

    private static final List<RestaurantDetails> FAKE_NEARBY_RESTAURANT = Arrays.asList(
            new RestaurantDetails("FAKE1","name1","type1","address1","phoneNumber1","website1"),
            new RestaurantDetails("FAKE2","name2","type2","address2","phoneNumber2","website2"),
            new RestaurantDetails("FAKE3","name3","type3","address3","phoneNumber3","website3"),
            new RestaurantDetails("FAKE4","name4","type4","address4","phoneNumber4","website4"),
            new RestaurantDetails("FAKE5","name5","type5","address5","phoneNumber5","website5")
    );

    private static final List<RestaurantDetails> FAKE_BOOKED_RESTAURANT = Arrays.asList(
            new RestaurantDetails("FAKE1","name1","type1","address1","phoneNumber1","website1"),
            new RestaurantDetails("FAKE3","name3","type3","address3","phoneNumber3","website3"),
            new RestaurantDetails("FAKE0","name0","type0","address0","phoneNumber0","website0")
    );

    private static final List<Prediction> FAKE_PREDICTION_RESTAURANT = Arrays.asList(
            new Prediction("FAKE1","nameOfPrediction1"),
            new Prediction("FAKE2","nameOfPrediction2"),
            new Prediction("FAKE3","nameOfPrediction3"),
            new Prediction("FAKE4","nameOfPrediction4"),
            new Prediction("FAKE5","nameOfPrediction5")
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
}
