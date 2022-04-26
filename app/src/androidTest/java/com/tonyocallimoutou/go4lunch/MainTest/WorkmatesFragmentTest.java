package com.tonyocallimoutou.go4lunch.MainTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class WorkmatesFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);


    @Before
    public void init() {
        onView(ViewMatchers.withId(R.id.navigation_workmates))
                .perform(click());
    }

    @Test
    public void CheckIfListIsNotEmpty() {
        onView(withId(R.id.workmates_recycler_view))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void checkIfInformationIsCorrect() {
        onView(withId(R.id.workmates_text))
                .check(matches(not(withText(""))));
/*
        onView(withId(R.id.profile_picture_workmate))
                .check(null);

 */
    }

    @Test
    public void checkGoToChatActivity() {
        onView(withId(R.id.workmates_recycler_view))
                .perform(actionOnItemAtPosition(0,click()));

        onView(withId(R.id.chat_text_resume))
                .check(matches(isDisplayed()));
    }
}
