package com.example.kebabapp

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kebabapp.fragments.SuggestionFormFragment
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuggestionFormFragmentTest : TestCase() {

    private lateinit var scenario: FragmentScenario<SuggestionFormFragment>


    @Before
    fun setup(){
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_KebabApp)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test

    fun testInputFieldIsClickable() {
        onView(withId(R.id.suggestionInput))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())
        val input = "Lorem ipsum sit dolor amet"
        onView(withId(R.id.suggestionInput)).perform(typeText(input))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.suggestionInput))
            .check(matches(withText(input)))
    }
}
