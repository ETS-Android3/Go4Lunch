package com.tonyocallimoutou.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;

import android.widget.ImageView;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.test.FakeData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantRate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class TestRateRestaurant {

    @Mock
    private static ImageView restaurantRate1;
    @Mock
    private static ImageView restaurantRate2;
    @Mock
    private static ImageView restaurantRate3;

    private final List<RestaurantDetails> nearbyRestaurant = new ArrayList<>(FakeData.getFakeNearbyRestaurant());
    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private final RestaurantDetails restaurantTest = nearbyRestaurant.get(0);


    @Before
    public void init() {
        openMocks(this);
        for (User user : fakeWorkmates) {
            user.getLikeRestaurantId().clear();
        }
    }

    @Test
    public void noStar() {
        // 0 like on 6 workmates

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);
        for (User user : fakeWorkmates) {
            assertEquals(0, user.getLikeRestaurantId().size());
        }

        RestaurantRate.setImage();
        assertEquals(0, RestaurantRate.getRate());
    }

    @Test
    public void oneStarMin() {
        // 1 like on 6 workmates

        fakeWorkmates.get(0).getLikeRestaurantId().add(restaurantTest.getPlaceId());

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);

        RestaurantRate.setImage();
        assertEquals(1, RestaurantRate.getRate());
    }

    @Test
    public void oneStarMax() {
        // 2 like on 6 workmates

        fakeWorkmates.get(0).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(1).getLikeRestaurantId().add(restaurantTest.getPlaceId());

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);

        RestaurantRate.setImage();
        assertEquals(1, RestaurantRate.getRate());
    }

    @Test
    public void twoStarMin() {
        // 3 like on 6 workmates

        fakeWorkmates.get(0).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(1).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(2).getLikeRestaurantId().add(restaurantTest.getPlaceId());

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);

        RestaurantRate.setImage();
        assertEquals(2, RestaurantRate.getRate());
    }

    @Test
    public void twoStarMax() {
        // 4 like on 6 workmates

        fakeWorkmates.get(0).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(1).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(2).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(3).getLikeRestaurantId().add(restaurantTest.getPlaceId());

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);

        RestaurantRate.setImage();
        assertEquals(2, RestaurantRate.getRate());
    }


    @Test
    public void threeStarMin() {
        // 5 like on 6 workmates

        fakeWorkmates.get(0).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(1).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(2).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(3).getLikeRestaurantId().add(restaurantTest.getPlaceId());
        fakeWorkmates.get(4).getLikeRestaurantId().add(restaurantTest.getPlaceId());

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);

        RestaurantRate.setImage();
        assertEquals(3, RestaurantRate.getRate());
    }

    @Test
    public void threeStarMax() {
        // 6 like on 6 workmates

        for (User user : fakeWorkmates) {
            user.getLikeRestaurantId().add(restaurantTest.getPlaceId());
        }

        assertEquals(6, fakeWorkmates.size());

        RestaurantRate.newInstance(restaurantTest,restaurantRate1,restaurantRate2,restaurantRate3,fakeWorkmates);

        RestaurantRate.setImage();
        assertEquals(3, RestaurantRate.getRate());
    }

}
