package com.github.takahirom.roborazzi.usage.examples

import android.R
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
class FirstRobolectricEspressoTest {
    @Test
    fun test() {
        val activityScenario = launch(ArticleActivity::class.java)

        onView(withId(R.id.content))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun roborazziTest() {
        val activityScenario = launch(ArticleActivity::class.java)

        onView(withId(R.id.content))
            .captureRoboImage()

        onView(isRoot())
            .captureRoboImage()
    }
}