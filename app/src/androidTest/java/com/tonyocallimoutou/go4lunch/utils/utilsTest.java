package com.tonyocallimoutou.go4lunch.utils;


import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Root;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class utilsTest {


    public static Matcher<View> atPositionOnView(final int position, final Matcher<View> itemMatcher,
                                                 final int targetViewId) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has view id " + itemMatcher + " at position " + position);
            }

            @Override
            public boolean matchesSafely(final RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                View targetView = viewHolder.itemView.findViewById(targetViewId);
                return itemMatcher.matches(targetView);
            }
        };
    }

    public static Matcher<View> withTextColor(final int expectedId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            protected boolean matchesSafely(TextView textView) {
                int colorId = ContextCompat.getColor(textView.getContext(), expectedId);
                return textView.getCurrentTextColor() == colorId;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                description.appendValue(expectedId);
            }
        };
    }

}