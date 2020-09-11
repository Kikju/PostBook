package com.senacor.postbook.ui.posts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.senacor.postbook.CoroutineTestRule
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PostsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testAllPosts() = coroutineTestRule.testDispatcher.runBlockingTest {
        val liveData = MutableLiveData<List<Post>>()
        val postsRepository = mock<PostsRepository> {
            on { getAllPosts(any()) } doReturn liveData
        }
        val savedStateHandle = SavedStateHandle()
        val vm = PostsViewModel(postsRepository, savedStateHandle)

        vm.setUserId(18)
        vm.setFavorite(false)

        val post0 = Post(18, 1, "title #1", "body", false)
        val post1 = Post(18, 2, "title #2", "body", false)
        liveData.value = listOf(post0, post1)

        val posts = vm.posts.getOrAwaitValue()

        assertThat(posts).hasSize(2)
        assertThat(posts[0]).isEqualTo(post0)
        assertThat(posts[1]).isEqualTo(post1)

        verify(postsRepository).getAllPosts(eq(18))
        verify(postsRepository, never()).getFavoritePosts(any())
    }

    @Test
    fun testFavoritePosts() = coroutineTestRule.testDispatcher.runBlockingTest {
        val liveData = MutableLiveData<List<Post>>()
        val postsRepository = mock<PostsRepository> {
            on { getFavoritePosts(any()) } doReturn liveData
        }
        val savedStateHandle = SavedStateHandle()
        val vm = PostsViewModel(postsRepository, savedStateHandle)

        vm.setUserId(19)
        vm.setFavorite(true)

        val post0 = Post(19, 1, "title #1", "body", true)
        val post1 = Post(19, 2, "title #2", "body", true)
        liveData.value = listOf(post0, post1)

        val posts = vm.posts.getOrAwaitValue()

        assertThat(posts).hasSize(2)
        assertThat(posts[0]).isEqualTo(post0)
        assertThat(posts[1]).isEqualTo(post1)

        verify(postsRepository).getFavoritePosts(eq(19))
        verify(postsRepository, never()).getAllPosts(any())
    }

    @Test
    fun testRefreshPosts() {
        val postsRepository = mock<PostsRepository>()
        val savedStateHandle = SavedStateHandle()
        val vm = PostsViewModel(postsRepository, savedStateHandle)

        vm.setUserId(25)
        vm.setFavorite(true)

        vm.refreshPosts()

        verifyBlocking(postsRepository) { refreshPosts(eq(25)) }
    }

}