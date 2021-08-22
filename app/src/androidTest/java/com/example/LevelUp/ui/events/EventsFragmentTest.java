package com.example.LevelUp.ui.events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import com.example.tryone.R;

import org.junit.Before;
import org.junit.Test;

public class EventsFragmentTest {
    private FragmentScenario<EventsFragment> scenario;

    @Before
    public void setup() {

        scenario = FragmentScenario.launchInContainer(EventsFragment.class);
        scenario.moveToState(Lifecycle.State.STARTED);
    }

    @Test
    public void floatingActionButtonTest() {
        onView(withId(R.id.fab))
            .perform(click());
        onView(withId(R.layout.events_adder)).check(matches(isDisplayed()));
    }
}