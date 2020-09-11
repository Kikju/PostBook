package com.senacor.postbook.ui.comments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.senacor.postbook.CoroutineTestRule
import com.senacor.postbook.db.model.Comment
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CommentsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testPost() = coroutineTestRule.testDispatcher.runBlockingTest {
        val liveData = MutableLiveData<Post>()
        val commentsRepository = mock<CommentsRepository> {
            on { getPost(any()) } doReturn liveData
        }
        val savedStateHandle = SavedStateHandle()
        val vm = CommentsViewModel(commentsRepository, savedStateHandle)

        vm.setPostId(36)

        val postToLiveData = Post(12, 36, "title #2", "body", false)
        liveData.value = postToLiveData

        val post = vm.post.getOrAwaitValue()

        assertThat(post).isEqualTo(postToLiveData)

        verify(commentsRepository).getPost(eq(36))
    }

    @Test
    fun testComments() = coroutineTestRule.testDispatcher.runBlockingTest {
        val liveData = MutableLiveData<List<Comment>>()
        val commentsRepository = mock<CommentsRepository> {
            on { getComments(any()) } doReturn liveData
        }
        val savedStateHandle = SavedStateHandle()
        val vm = CommentsViewModel(commentsRepository, savedStateHandle)

        vm.setPostId(35)

        val comment0 = Comment(35, 1, "name", "email", "body")
        val comment1 = Comment(35, 2, "name", "email", "body")
        liveData.value = listOf(comment0, comment1)

        val comments = vm.comments.getOrAwaitValue()

        assertThat(comments).hasSize(2)
        assertThat(comments[0]).isEqualTo(comment0)
        assertThat(comments[1]).isEqualTo(comment1)

        verify(commentsRepository).getComments(eq(35))
    }

    @Test
    fun testRefreshPosts() {
        val commentsRepository = mock<CommentsRepository>()
        val savedStateHandle = SavedStateHandle()
        val vm = CommentsViewModel(commentsRepository, savedStateHandle)

        vm.setPostId(37)

        vm.refreshComments()

        verifyBlocking(commentsRepository) { refreshComments(eq(37)) }
    }

}