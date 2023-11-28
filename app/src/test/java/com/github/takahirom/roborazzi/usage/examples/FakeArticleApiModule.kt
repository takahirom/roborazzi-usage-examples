package com.github.takahirom.roborazzi.usage.examples

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ArticleApiModule::class]
)
object FakeArticleApiModule {
    @Provides
    fun provideArticleApi(): ArticleApi {
        return FakeArticleApi()
    }
}