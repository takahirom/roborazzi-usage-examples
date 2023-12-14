package com.github.takahirom.roborazzi.usage.examples

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
class FirstRobolectricComposeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun test() {
        composeRule.setContent {
            Article(name = "Robolectric")
        }

        composeRule
            .onNode(hasText("Hello Robolectric!"))
            .assertExists()
    }

    @Test
    fun roborazziTest() {
        composeRule.setContent {
            Article(name = "Roborazzi")
        }

        composeRule
            .onNode(hasText("Hello Robolectric!"))
            .captureRoboImage()

        composeRule
            .onRoot()
            .captureRoboImage()
    }
}