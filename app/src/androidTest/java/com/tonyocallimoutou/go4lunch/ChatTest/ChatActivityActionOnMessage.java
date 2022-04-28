package com.tonyocallimoutou.go4lunch.ChatTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tonyocallimoutou.go4lunch.utils.utilsTest.atPositionOnView;
import static org.hamcrest.Matchers.not;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tonyocallimoutou.go4lunch.FAKE.FakeData;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.ui.chat.ChatActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelChat;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class ChatActivityActionOnMessage {

    private final List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private static int nbrMessage;

    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @BeforeClass
    public static void beforeAll() {
        nbrMessage = 0;
    }

    @Before
    public void init() {
        mainScenarioRule.getScenario().onActivity(activity -> {
            ChatActivity.navigate(activity,fakeWorkmates,null);
        });
    }

    @Test
    public void getDetail() {
        String str = "Test Example " + nbrMessage;

        onView(withId(R.id.chat_edit_text))
                .perform(typeText(str));

        onView(withId(R.id.chat_send_button))
                .perform(click());

        nbrMessage++;

        onView(withId(R.id.chat_recycler_view))
                .perform(actionOnItemAtPosition(nbrMessage-1,click()));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,isDisplayed(),R.id.chat_message_info_current_user)));

        onView(withId(R.id.chat_recycler_view))
                .perform(actionOnItemAtPosition(nbrMessage-1,click()));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,not(isDisplayed()),R.id.chat_message_info_current_user)));

    }

    @Test
    public void moreActionOnLongClick() {
        String str = "Test Example " + nbrMessage;

        onView(withId(R.id.chat_edit_text))
                .perform(typeText(str));

        onView(withId(R.id.chat_send_button))
                .perform(click());

        nbrMessage++;

        onView(withId(R.id.chat_recycler_view))
                .perform(actionOnItemAtPosition(nbrMessage-1,longClick()));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,isDisplayed(),R.id.chat_container_more_action)));

        onView(withId(R.id.chat_recycler_view))
                .perform(actionOnItemAtPosition(nbrMessage-1,click()));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,not(isDisplayed()),R.id.chat_container_more_action)));

    }

    @Test
    public void deleteMessage() {
        String str = "Test Example " + nbrMessage;

        onView(withId(R.id.chat_edit_text))
                .perform(typeText(str));

        onView(withId(R.id.chat_send_button))
                .perform(click());

        nbrMessage++;

        onView(withId(R.id.chat_recycler_view))
                .perform(actionOnItemAtPosition(nbrMessage-1,longClick()));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,isDisplayed(),R.id.chat_container_more_action)));

        onView(withId(R.id.chat_recycler_view))
                .perform(actionOnItemAtPosition(nbrMessage - 1, new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.findViewById(R.id.chat_message_remove)
                                .performClick();
                    }
                }));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,not(isDisplayed()),R.id.chat_container_more_action)));

        onView(withId(R.id.chat_recycler_view))
                .check(matches(atPositionOnView(nbrMessage-1,withText("message delete by : Test User"),R.id.chat_message_text_view)));

    }
}
