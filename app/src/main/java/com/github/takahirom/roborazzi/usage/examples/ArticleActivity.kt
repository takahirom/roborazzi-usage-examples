package com.github.takahirom.roborazzi.usage.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.takahirom.roborazzi.usage.examples.ui.theme.RoborazziusageexamplesTheme
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ArticleApi {
    suspend fun getArticles(): List<String>
}

class ArticleApiClient @Inject constructor() : ArticleApi {
    override suspend fun getArticles(): List<String> {
        return listOf("article title 1", "article title 1")
    }
}

@Module
@InstallIn(SingletonComponent::class)
class ArticleApiModule {
    // You can use @Bind, but for explanation, we use Provides
    @Provides
    fun bindArticleApi(articleApiClient: ArticleApiClient): ArticleApi {
        return articleApiClient
    }
}

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articleApi: ArticleApi
) : ViewModel() {
    private val _articlesStateFlow = MutableStateFlow<List<String>>(emptyList())
    val articlesStateFlow: StateFlow<List<String>> = _articlesStateFlow.asStateFlow()

    fun onCreate() {
        viewModelScope.launch {
            _articlesStateFlow.value = articleApi.getArticles()
        }
    }
}

@AndroidEntryPoint
class ArticleActivity : ComponentActivity() {
    private val articlesViewModel: ArticlesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articlesViewModel.onCreate()

        setContent {
            RoborazziusageexamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        val articles by articlesViewModel.articlesStateFlow.collectAsState()
                        articles.forEach {
                            Article(name = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Article(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Article $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoborazziusageexamplesTheme {
        Article("Android")
    }
}