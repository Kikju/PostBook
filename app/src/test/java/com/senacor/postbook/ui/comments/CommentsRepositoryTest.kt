package com.senacor.postbook.ui.comments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.senacor.postbook.CoroutineTestRule
import com.senacor.postbook.db.model.Comment
import com.senacor.postbook.db.model.CommentDao
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.getOrAwaitValue
import com.senacor.postbook.network.AppApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import com.senacor.postbook.network.model.Comment as NetworkComment

@ExperimentalCoroutinesApi
class CommentsRepositoryTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testGetPost() = coroutineTestRule.testDispatcher.runBlockingTest {
        val postsDao = mock<PostDao> {
            onBlocking { getPost(any()) } doReturn Post(1, 1, "title", "body", true)
        }
        val commentsDao = mock<CommentDao>()
        val appApi = mock<AppApi>()
        val repository = CommentsRepository(postsDao, commentsDao, appApi)

        val postFromRepository = repository.getPost(1).getOrAwaitValue()

        assertThat(postFromRepository).isNotNull()
        assertThat(postFromRepository).isEqualTo(Post(1, 1, "title", "body", true))
    }


    @Test
    fun testGetComments() {
        val postsDao = mock<PostDao>()
        val liveData = MutableLiveData<List<Comment>>()
        val commentsDao = mock<CommentDao> {
            on { getAllComments(any()) } doReturn liveData
        }
        val appApi = mock<AppApi>()
        val repository = CommentsRepository(postsDao, commentsDao, appApi)

        val commentToLiveData = Comment(1, 1, "name", "email", "body")
        liveData.value = listOf(commentToLiveData)

        val commentsFromRepository = repository.getComments(1).getOrAwaitValue()

        assertThat(commentsFromRepository).hasSize(1)
        assertThat(commentsFromRepository[0]).isEqualTo(commentToLiveData)
    }

    @Test
    fun testRefreshComments() {
        val postsDao = mock<PostDao>()
        val commentsDao = mock<CommentDao>()
        val networkComments = listOf(
            NetworkComment(1, 1, "firstName", "firstEmail", "firstBody"),
            NetworkComment(1, 2, "secondName", "secondEmail", "secondBody")
        )
        val appApi = mock<AppApi> {
            onBlocking { getComments(any()) } doReturn networkComments
        }
        val repository = CommentsRepository(postsDao, commentsDao, appApi)

        runBlocking { repository.refreshComments(1) }

        verifyBlocking(appApi) { getComments(eq(1)) }
        verifyBlocking(commentsDao) { insertComment(eq(Comment(1, 1, "firstName", "firstEmail", "firstBody"))) }
        verifyBlocking(commentsDao) { insertComment(eq(Comment(1, 2, "secondName", "secondEmail", "secondBody"))) }
    }
}