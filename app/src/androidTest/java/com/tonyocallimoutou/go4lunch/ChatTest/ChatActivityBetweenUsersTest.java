package com.tonyocallimoutou.go4lunch.ChatTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tonyocallimoutou.go4lunch.utils.utilsTest.atPositionOnView;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.ui.chat.ChatActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ChatActivityBetweenUsersTest {

    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    int nbrMessage;

    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void init() {
        mainScenarioRule.getScenario().onActivity(activity -> {
            ChatActivity.navigate(activity,fakeWorkmates,null);
        });
    }

    @Test
    public void CheckNameOfChat() {
         onView(ViewMatchers.withId(R.id.chat_text_resume))
                 .check(matches(not(withText(""))));
    }

    @Test
    public void testMessage() {
        nbrMessage = 0;
        String str = "Test Example " + nbrMessage;

        onView(withId(R.id.chat_edit_text))
                .perform(typeText(str));

        onView(withId(R.id.chat_send_button))
                .perform(click());

        nbrMessage++;

        onView(withId(R.id.chat_recycler_view))
                .check(matches(hasMinimumChildCount(nbrMessage)));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,withText(str),R.id.chat_message_text_view_current_user)));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,not(isDisplayed()),R.id.chat_message_text_view)));
    }

}
