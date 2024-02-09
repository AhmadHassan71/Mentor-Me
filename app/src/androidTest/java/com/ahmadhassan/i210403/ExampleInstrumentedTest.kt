package com.ahmadhassan.i210403


import android.app.Activity
import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Intent
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*



@RunWith(AndroidJUnit4::class)
class HomeAndLoginActivityTest {

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testBottomNavigationNavigation() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            // Search button
            val searchButton = scenario.onActivity { it.findViewById<ImageView>(R.id.navigation_search) }
            val expectedIntent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, SearchPageActivity::class.java)
            val matcher: Matcher<Intent> = IntentMatchers.hasComponent(SearchPageActivity::class.java.name)
            val result = Instrumentation.ActivityResult(Activity.RESULT_OK, expectedIntent)
            intending(matcher).respondWith(result)
            Espresso.onView(withId(R.id.navigation_search)).perform(ViewActions.click())
            intended(matcher)

            // Chat button
            val chatButton = scenario.onActivity { it.findViewById<ImageView>(R.id.navigation_chat) }
            val chatIntent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, ChatActivity::class.java)
            val chatMatcher: Matcher<Intent> = IntentMatchers.hasComponent(ChatActivity::class.java.name)
            val chatResult = Instrumentation.ActivityResult(Activity.RESULT_OK, chatIntent)
            intending(chatMatcher).respondWith(chatResult)
            Espresso.onView(withId(R.id.navigation_chat)).perform(ViewActions.click())
            intended(chatMatcher)

            // Profile button
            val profileButton = scenario.onActivity { it.findViewById<ImageView>(R.id.navigation_profile) }
            val profileIntent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, MyProfileActivity::class.java)
            val profileMatcher: Matcher<Intent> = IntentMatchers.hasComponent(MyProfileActivity::class.java.name)
            val profileResult = Instrumentation.ActivityResult(Activity.RESULT_OK, profileIntent)
            intending(profileMatcher).respondWith(profileResult)
            Espresso.onView(withId(R.id.navigation_profile)).perform(ViewActions.click())
            intended(profileMatcher)
        }
    }

    @Test
    fun testInvalidEmailFormat() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            // Type invalid email format
            onView(withId(R.id.EmailEditText)).perform(typeText("invalid_email"))

            // Click on the login button
            onView(withId(R.id.LoginButton)).perform(click())
        }
    }


    @Test
    fun testLoginSuccess() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            // Type email and password
            onView(withId(R.id.EmailEditText)).perform(typeText("example@gmail.com"))
            onView(withId(R.id.PasswordEditText)).perform(typeText("password"))
            Espresso.closeSoftKeyboard()

            // Click on the login button
            onView(withId(R.id.LoginButton)).perform(click())

            // Verify that HomeActivity is launched
            onView(withId(R.id.home_activity)).check(matches(isDisplayed()))

        }
    }
}
