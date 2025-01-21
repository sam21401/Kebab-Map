package com.example.kebabapp

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kebabapp.fragments.user.UserLoginFragment
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserLoginFragmentTest : TestCase() {
    private lateinit var scenario: FragmentScenario<UserLoginFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_KebabApp)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testEmailAndPasswordFieldsAreClickable() {
        onView(withId(R.id.etUseremail))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())
        onView(withId(R.id.etPassword))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())
        val email = "abc@abc.com"
        val password = "password"
        onView(withId(R.id.etUseremail)).perform(typeText(email))
        onView(withId(R.id.etPassword)).perform(typeText(password))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.etUseremail))
            .check(matches(withText(email)))
        onView(withId(R.id.etPassword))
            .check(matches(withText(password)))
    }
}
