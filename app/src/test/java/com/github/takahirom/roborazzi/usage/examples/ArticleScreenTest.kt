package com.github.takahirom.roborazzi.usage.examples

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ArticleScreenTest {
    private val composeTestRule = createAndroidComposeRule<ArticleActivity>()

    @get:Rule
    val ruleChain = RuleChain
        .outerRule(HiltAndroidRule(this))
        .around(composeTestRule)

    @Test
    fun checkScreenShot() {
        composeTestRule
            .onRoot()
            .captureRoboImage()
    }
}


class FakeArticleApi : ArticleApi {
    override suspend fun getArticles(): List<String> {
        return listOf("fake article 1", "fake article 2")
    }
}