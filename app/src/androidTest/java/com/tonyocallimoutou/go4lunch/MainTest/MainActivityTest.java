package com.tonyocallimoutou.go4lunch.MainTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void CheckIfMainActivityHaveBottomNavBar() {
        onView(ViewMatchers.withId(R.id.bottom_nav_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void CheckIfSideViewIsCreated(){
        onView(withContentDescription("Navigate up"))
                .perform(click());

        onView(withId(R.id.logo_header_side_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_picture_header_side_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.navigation_setting))
                .check(matches(isDisplayed()));
    }

    @Test
    public void CheckIfSideViewHaveInformationOfCurrentUser() {
        onView(withContentDescription("Navigate up"))
                .perform(click());
/*
        onView(withId(R.id.profile_picture_header_side_view))
                .check((null));


        onView(withId(R.id.user_name))
                .check(matches(withText(currentUser.getUsername())));

        onView(withId(R.id.user_email))
                .check(matches(withText(currentUser.getEmail())));

 */
    }
}