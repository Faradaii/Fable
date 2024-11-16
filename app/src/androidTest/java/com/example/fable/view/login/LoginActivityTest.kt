package com.example.fable.view.login

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.fable.R
import com.example.fable.util.EspressoIdlingResource
import com.example.fable.util.WaitActivityIsResumedIdlingResource
import com.example.fable.view.HomeActivity
import com.example.fable.view.welcome.WelcomeActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    private val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
    private lateinit var homeActivityClassName: String
    private lateinit var waitActivityHome: WaitActivityIsResumedIdlingResource

    @Before
    fun setUp() {
        homeActivityClassName = HomeActivity::class.java.name
        waitActivityHome = WaitActivityIsResumedIdlingResource(homeActivityClassName)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }

    @Test
    fun loginWithInvalidData() {
        val invalidEmail = "email"
        val invalidPassword = "pass"

        onView(withId(R.id.ed_login_email)).perform(click())
        onView(withId(R.id.ed_login_email)).perform(typeText(invalidEmail), closeSoftKeyboard())
        onView(withId(R.id.emailEditTextLayout)).check(
            matches(
                hasDescendant(withText(resources.getString(R.string.email_is_not_valid)))
            )
        )

        onView(withId(R.id.ed_login_email)).perform(clearText())
        onView(withId(R.id.emailEditTextLayout)).check(
            matches(
                hasDescendant(withText(resources.getString(R.string.email_cannot_be_empty)))
            )
        )

        onView(withId(R.id.ed_login_password)).perform(click())
        onView(withId(R.id.ed_login_password)).perform(
            typeText(invalidPassword),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditTextLayout)).check(
            matches(
                hasDescendant(withText(resources.getString(R.string.password_must_be_at_least_8_characters)))
            )
        )

        onView(withId(R.id.ed_login_password)).perform(clearText())
        onView(withId(R.id.passwordEditTextLayout)).check(
            matches(
                hasDescendant(withText(resources.getString(R.string.password_cannot_be_empty)))
            )
        )
    }

    @Test
    fun loginWithValidDataAndLogout() {
        val validEmail = "usertest@test.com"
        val validPassword = "usertest"

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))

        onView(withId(R.id.ed_login_email)).perform(click())
        onView(withId(R.id.ed_login_email)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.emailEditTextLayout)).check(
            matches(
                allOf(
                    not(hasDescendant(withText(resources.getString(R.string.email_is_not_valid)))),
                    not(hasDescendant(withText(resources.getString(R.string.email_cannot_be_empty))))
                )
            )
        )

        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))

        onView(withId(R.id.ed_login_password)).perform(click())
        onView(withId(R.id.ed_login_password)).perform(typeText(validPassword), closeSoftKeyboard())
        onView(withId(R.id.passwordEditTextLayout)).check(
            matches(
                allOf(
                    not(hasDescendant(withText(resources.getString(R.string.password_cannot_be_empty)))),
                    not(hasDescendant(withText(resources.getString(R.string.password_must_be_at_least_8_characters))))
                )
            )
        )

        onView(withId(R.id.loginButton)).perform(click())

        IdlingRegistry.getInstance().register(waitActivityHome)
        try {
            intended(hasComponent(hasClassName(homeActivityClassName)))
        } finally {
            IdlingRegistry.getInstance().unregister(waitActivityHome)
        }

        onView(withId(R.id.topAppBar)).check(matches(isDisplayed()))
        onView(withId(R.id.action_logout)).perform(click())

        EspressoIdlingResource.increment()
        Thread.sleep(1000)
        EspressoIdlingResource.decrement()

        intended(hasComponent(WelcomeActivity::class.java.name))
    }
}