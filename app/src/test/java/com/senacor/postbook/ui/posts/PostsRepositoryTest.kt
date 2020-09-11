package com.senacor.postbook.ui.posts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.getOrAwaitValue
import com.senacor.postbook.network.AppApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import com.senacor.postbook.network.model.Post as NetworkPost
import com.senacor.postbook.ui.posts.Post as UiPost

class PostsRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testAllPosts() {
        val liveData = MutableLiveData<List<Post>>()
        val postsDao = mock<PostDao> {
            on { getAllPosts(any()) } doReturn liveData
        }
        val appApi = mock<AppApi>()
        val repository = PostsRepositoryImpl(postsDao, appApi)

        val postToLiveData = Post(1, 1, "", "", false)
        liveData.value = listOf(postToLiveData)

        val postsFromRepository = repository.getAllPosts(1).getOrAwaitValue()

        assertThat(postsFromRepository).isNotEmpty()
        assertThat(postsFromRepository[0]).isEqualTo(postToLiveData)
    }

    @Test
    fun testFavoritePosts() {
        val liveData = MutableLiveData<List<Post>>()
        val postsDao = mock<PostDao> {
            on { getFavoritePosts(any()) } doReturn liveData
        }
        val appApi = mock<AppApi>()
        val repository = PostsRepositoryImpl(postsDao, appApi)

        val postToLiveData = Post(1, 1, "", "", false)
        liveData.value = listOf(postToLiveData)

        val postsFromRepository = repository.getFavoritePosts(1).getOrAwaitValue()

        assertThat(postsFromRepository).isNotEmpty()
        assertThat(postsFromRepository[0]).isEqualTo(postToLiveData)
    }

    @Test
    fun testRefreshPosts() {
        val liveData = MutableLiveData<List<Post>>()
        val postsDao = mock<PostDao> {
            on { getAllPosts(any()) } doReturn liveData
            onBlocking { getPost(any()) }.thenReturn(null, Post(1, 2, "oldInDb", "", true))
        }
        val networkPosts = listOf(
            NetworkPost(1, 1, "firstTitle", ""),
            NetworkPost(1, 2, "secondTitle", "")
        )
        val appApi = mock<AppApi> {
            onBlocking { getPosts(any()) } doReturn networkPosts
        }

        val repository = PostsRepositoryImpl(postsDao, appApi)

        runBlocking { repository.refreshPosts(1) }

        verifyBlocking(appApi) { getPosts(eq(1)) }
        verifyBlocking(postsDao) { insertPost(eq(Post(1, 1, "firstTitle", ""))) }
        verifyBlocking(postsDao) { updatePost(eq(Post(1, 2, "secondTitle", "", true))) }
    }

    @Test
    fun testUpdatePost() {
        val postsDao = mock<PostDao>()
        val appApi = mock<AppApi>()
        val repository = PostsRepositoryImpl(postsDao, appApi)

        val uiPost = UiPost(23, "title", "body", true)
        runBlocking { repository.updatePost(11, uiPost) }

        verifyBlocking(postsDao) { updatePost(eq(Post(11, 23, "title", "body", true))) }
    }
}