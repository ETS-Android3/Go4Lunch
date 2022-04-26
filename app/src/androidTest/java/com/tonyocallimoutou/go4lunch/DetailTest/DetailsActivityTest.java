package com.tonyocallimoutou.go4lunch.DetailTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DetailsActivityTest {

    private final RestaurantDetails restaurantTest =
            new RestaurantDetails("99", "NameTest","TypeTest", "AddressTest",0.0,0.0,"phoneTest","websiteTest");


    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);


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

        onView(withId(R.id.detail_recycler_view))
                .check(matches(hasMinimumChildCount(0)));

        onView(withId(R.id.lbl_no_workmates))
                .check(matches(isDisplayed()));

    }

    /*

    Test CALL + LIKE + WEBSITE

     */

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

        pressBack();

        onView(withId(R.id.detail_booked_restaurant))
                .perform(click());
    }

}

