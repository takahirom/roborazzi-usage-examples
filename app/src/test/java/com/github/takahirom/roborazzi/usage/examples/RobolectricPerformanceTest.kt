package com.github.takahirom.roborazzi.usage.examples

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
class RobolectricPerformanceTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun test() {
        composeRule.setContent {
            Article(name = "Robolectric")
        }

        composeRule
            .onNode(hasText("Article Robolectric!"))
            .assertExists()
    }

    @Test
    fun test2() {
        composeRule.setContent {
            Article(name = "Robolectric test2")
        }

        composeRule
            .onNode(hasText("Article Robolectric test2!"))
            .assertExists()
    }

    @Test
    fun test3() {
        composeRule.setContent {
            Article(name = "Robolectric test3")
        }

        composeRule
            .onNode(hasText("Article Robolectric test3!"))
            .assertExists()
    }


    @Test
    fun test4() {
        composeRule.setContent {
            Article(name = "Robolectric test4")
        }

        composeRule
            .onNode(hasText("Article Robolectric test4!"))
            .assertExists()
    }
}