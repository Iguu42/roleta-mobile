package com.example.roleta;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateRouletteFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);
    @Test
    public void testCreateRoulette() {
        onView(withId(R.id.btCreateRoulettes)).perform(click());
        onView(withId(R.id.editTextTitulo)).perform(typeText("Test Roulette"), closeSoftKeyboard());
        onView(withId(R.id.editTextOpcao1)).perform(typeText("Option 1"), closeSoftKeyboard());
        onView(withId(R.id.editTextOpcao2)).perform(typeText("Option 2"), closeSoftKeyboard());
        onView(withId(R.id.editTextOpcao3)).perform(typeText("Option 3"), closeSoftKeyboard());
        onView(withId(R.id.buttonCriar)).perform(click());
    }
}
