package com.tonyocallimoutou.go4lunch.MainTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;

import com.tonyocallimoutou.go4lunch.R;

import org.junit.Test;

public class MapFragmentTest {

    @Test
    public void CheckIfListIsNotEmpty() {
        onView(ViewMatchers.withId(R.id.list_view_recycler_view))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void checkIfInformationIsCorrect() {
    }

    @Test
    public void checkGoToDetailActivity() {
    }
}
