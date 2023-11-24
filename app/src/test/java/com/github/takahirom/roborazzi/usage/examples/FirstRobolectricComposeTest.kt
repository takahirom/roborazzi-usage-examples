package com.github.takahirom.roborazzi.usage.examples

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FirstRobolectricComposeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun test() {
        composeRule.setContent {
            Greeting(name = "Robolectric")
        }

        composeRule
            .onNode(hasText("Hello Robolectric!"))
            .assertExists()
    }
}