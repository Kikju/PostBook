package com.senacor.postbook.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.senacor.postbook.db.model.Comment
import com.senacor.postbook.db.model.CommentDao
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CommentDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var postDao: PostDao
    private lateinit var commentDao: CommentDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        postDao = db.postsDao()
        commentDao = db.commentsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    companion object {
        fun newComment(postId: Int = 1, id: Int = 1) = Comment(
            postId,
            id,
            "name #$id",
            "email",
            "body"
        )
    }

    @Test
    fun getAllComments_positive() = runBlockingTest {
        postDao.insertPost(Post(1, 1, "", "", false))
        commentDao.insertComment(newComment())
        commentDao.insertComment(newComment(1, 2))
        val comments = commentDao.getAllComments(1).getOrAwaitValue()
        assertThat(comments).hasSize(2)
        assertThat(comments[0].id).isEqualTo(1)
        assertThat(comments[1].id).isEqualTo(2)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun getAllComments_foreignKeyValidationFailed() = runBlockingTest {
        commentDao.insertComment(newComment())
    }

}